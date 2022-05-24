# Risks

You have completed all tasks that were purely graphQL, congratulations, you must be a genius! However there are another concepts to have in mind when we are serving graphQL API. You, who are a very observant man. Probably have noticed that we have cyclic reference in our Character with friends relation. That means that if we have some kind of evil client it could make an endless deep query to our server.

```
query VeryLongQuery {
    getCharacterById(id: 1){
        ...AllDataLessFriends
        friends {
            ...AllDataLessFriends
            friends {
                ...AllDataLessFriends
                friends {
                    ...AllDataLessFriends
                    friends {
                        ...AllDataLessFriends
                    }
                }
            }
        }
    }
}

fragment AllDataLessFriends on Character {
    id
    name
    appearsIn
    ... on Biological {
        totalCredits
        starship {
            id
            name
            length
        }
    }
    ... on Droid {
        primaryFunction
    }
}
```

## Caching and Batching

Since you are smart guy, you have realized that probably this people will be friends in common. The next question that will come to your mind will be. Does graphql-java provide any kind of [mechanism](https://www.graphql-java.com/documentation/batching) to avoid asking again for data that we already have received? The answer it is, yes!

First of all mention that in order to correctly [batching](https://github.com/graphql-java/java-dataloader#batching-requires-batched-backing-apis) we need service which provides that functionality. However we always can still caching which will makes our API more efficient. If you want to read more about this functionality you can check [java-dataloader](https://github.com/graphql-java/java-dataloader).

### The BatchLoader function

Fist, if we want to implement caching and batching functionality we need to provide a BatchLodader. A BatchLodader is an Interface with a single method,  defined as:

```java
public interface BatchLoader<K,V> {
	CompletionStage<List<V>> load(List<K> keys);
}
```

We might implement this loaders interface directly in our resolvers. Also we will need to update our getMethods as follows.

On `CharacterResolver`:

```java
public CompletableFuture<List<CharacterOutDTO>> characterBatchLoader(List<Integer> ids) {
	return CompletableFuture.supplyAsync(() -> characterService.findAllByIds(ids));
}

public CompletableFuture<CharacterOutDTO> queryGetCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
	DataLoader<Integer, CharacterOutDTO> characterLoader = dataFetchingEnvironment.getDataLoader("character");
	Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
	return characterLoader.load(id);
}

public CompletableFuture<List<CharacterOutDTO>> characterFriends(DataFetchingEnvironment dataFetchingEnvironment) {
	CharacterOutDTO character = dataFetchingEnvironment.getSource();
	List<Integer> friendsIds = character.getFriends();
	if (friendsIds != null) {
		DataLoader<Integer, CharacterOutDTO> characterLoader = dataFetchingEnvironment
				.getDataLoader("character");
		return characterLoader.loadMany(friendsIds);
	} else {
		return null;
	}
}
```

On `StarshipResolver`:

```java
public CompletableFuture<List<StarshipOutDTO>> starshipBatchLoader(List<Integer> ids) {
	return CompletableFuture.supplyAsync(() -> starshipService.findAllById(ids));
}

public CompletableFuture<StarshipOutDTO> queryGetStarshipById(DataFetchingEnvironment dataFetchingEnvironment) {
	DataLoader<Integer, StarshipOutDTO> starshipLoader = dataFetchingEnvironment.getDataLoader("starship");
	Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
	return starshipLoader.load(id);
}

public CompletableFuture<StarshipOutDTO> characterStarship(DataFetchingEnvironment dataFetchingEnvironment) {
	DataLoader<Integer, StarshipOutDTO> starshipLoader = dataFetchingEnvironment.getDataLoader("starship");
	BiologicOutDTO biologicDTO = dataFetchingEnvironment.getSource();
	Integer id = biologicDTO.getStarshipID();
	if (id != null) {
		return starshipLoader.load(id);
	} else {
		return null;
	}
}
```

### Register the DataLoaders

We have also to register our DataLoaders. Here we are creating a cache per request. However, if your data can be shared across the request, then you may want to use [ValueCache](https://www.graphql-java.com/documentation/batching#per-request-data-loaders) implementation.

You can edit `GraphQLController` as:

```java
public Map<String, Object> post(Request req, Response res) throws Exception {
	res.header("Content-Type", "application/json");
	GraphQLHttpPostInput graphQLInput = objectMapper.readValue(req.body(), GraphQLHttpPostInput.class);

	DataLoaderRegistry registry = createRegistry();

	ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(graphQLInput.getQuery())
			.dataLoaderRegistry(registry).build();

	ExecutionResult executionResult = graphQL.execute(executionInput);

	return executionResult.toSpecification();

}

private DataLoaderRegistry createRegistry() {
	DataLoader<Integer, CharacterOutDTO> characterDataLoader = DataLoaderFactory
			.newDataLoader(characterResolver::characterBatchLoader);
	DataLoader<Integer, StarshipOutDTO> starshipDataLoader = DataLoaderFactory
			.newDataLoader(starshipResolver::starshipBatchLoader);

	DataLoaderRegistry registry = new DataLoaderRegistry();
	registry.register("character", characterDataLoader);
	registry.register("starship", starshipDataLoader);
	return registry;
}
```



## Instrumentation

We have fixed our database overloading. But this it not enough. Your evil clients still making endless queries so you are serving very sized data. This it is one of the main [security](https://www.howtographql.com/advanced/4-security/) issues of graphql. Does java-graphql provides any solution for this issue? The answer again it is, yes!

java-graphql provides different types of [instrumentation](https://www.graphql-java.com/documentation/instrumentation) which allows us archive that goal.

### MaxQueryComplexityInstrumentation and MaxQueryDepthInstrumentation

One of the ways to avoid that issue would be implement MaxQueryComplexityInstrumentation and MaxQueryDepthInstrumentation in order to analyze the query before we proceed to resolve it.

:warning: **Warning!**

- [MaxQueryComplexityInstrumentation](https://github.com/graphql-java/graphql-java/issues/1695) have currently an issue an can be easily exploited using fragments in our queries.
- In order to be able to fecth our shema we need at least MaxQueryDepthInstrumentation of depth 13.

```java
public class StarWarsFieldComplexityCalculator implements FieldComplexityCalculator {

	@Override
	public int calculate(FieldComplexityEnvironment environment, int childComplexity) {
		Field field=environment.getField();
		String fieldName=field.getName();
		int newComplexity = 0;
		
		if("friends".equals(fieldName)) {
			newComplexity = childComplexity * 10;
		}else {
			newComplexity = childComplexity + 1;
		}
		return newComplexity;

	}

}
```

```java
public static GraphQL buildGraphQL(CharacterResolver characterResolver, StarshipResolver starshipResolver,
		ObjectMapper objectMapper) {
	String stringSchema = readGraphQLSchema();
	TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(stringSchema);

	RuntimeWiring runtimeWiring = buildGraphQLWiring(characterResolver, starshipResolver, objectMapper);
	GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);

	ChainedInstrumentation chainedInstrumentation = buildInstrumentation();
	
	return GraphQL.newGraphQL(graphQLSchema)
			.instrumentation(chainedInstrumentation)
			.build();
}

private static ChainedInstrumentation buildInstrumentation() {
	DataLoaderDispatcherInstrumentationOptions options = DataLoaderDispatcherInstrumentationOptions.newOptions()
			.includeStatistics(true);
	DataLoaderDispatcherInstrumentation dataLoaderDispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(options);
	
	MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation = new MaxQueryComplexityInstrumentation(100,
			new StarWarsFieldComplexityCalculator());
	MaxQueryDepthInstrumentation maxQueryDepthInstrumentation = new MaxQueryDepthInstrumentation(13);

	List<Instrumentation> chainedList = new ArrayList<>();
	chainedList.add(maxQueryComplexityInstrumentation);
	chainedList.add(maxQueryDepthInstrumentation);
	chainedList.add(dataLoaderDispatcherInstrumentation);
	return new ChainedInstrumentation(chainedList);
}
```