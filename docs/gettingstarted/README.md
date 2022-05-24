# Getting started

First step to start working it is define our API and its contract. Let´s start with a simple situation. We want to expose a **graphQL** API that allows us **get** star-wars `Characters` and also their `Starships`.

**Starship**

- `Starship` **must** have a name.
- `Starship` **can** indicate their length.

**Character**

- `Character` **must** have a name.
- `Character` **must** indicate on which episode/s appears.

## The schema

Knowing our specifications we can start building our schema definition. So let´s create a `schema.graphqls` file inside our `resources/graphql` folder. If you need information about schema specification you can consult [GraphQL](https://graphql.org/learn/) documentation.

### Enum

We need something to indicate the Episodes. Since the episodes are something fixed, we decided to make an enum with the options.

```
enum Episode {
  A_NEW_HOPE
  THE_EMPIRE_STRIKES_BACK
  RETURN_OF_THE_JEDI
}
```

### Scalar types

A GraphQL object type has a name and fields, but at some point those fields have to resolve to some concrete data. That's where the [scalar](https://graphql.org/learn/schema/#scalar-types) types come in: they represent the leaves of the query.

java-graphql provides a way to implement our [custom scalars](https://www.graphql-java.com/documentation/scalars)

On this tutorial we are going to use just the default types.

### Types

The most basic components of a GraphQL schema are the [types](https://graphql.org/learn/schema/#type-language), which simply represent a kind of object that can be fetched from your service. Based on our requirements. We can write the definition for starship and character as follows

```
type Starship {
  id: ID!
  name: String!
  length: Int  
}

type Character {
  id: ID!
  name: String!
  appearsIn: [Episode]!
}
```

### Queries and Mutations

There are 2 special definitions in graphQL schema. They are the [queries and mutations](https://graphql.org/learn/schema/#the-query-and-mutation-types). On this step we only focus on queries.

```
type Query {
  getCharacterById(id: ID!): Character
  getStarshipById(id: ID!): Starship
}
```

## The code with graphql-java

In order to load graphQL we need to accomplish the following goals:

- [The schema definition.](#the-schema)
- [DataFetchers (also called resolvers) and DTOs.](#datafetchers-and-dtos)
- [Build the RuntimeWiring.](#the-runtimewiring)
- [Load schema and parse to TypeDefinitionRegistry.](#load-schema-and-parse-to-typedefinitionregistry)

We already have our schema file but we still needing the other points.

![java-graphql architecture](./graphql_creation.png)

### DataFetchers and DTOs

Probably the most important concept for a GraphQL Java server is a [DataFetcher](https://www.graphql-java.com/documentation/data-fetching/). While GraphQL Java is executing a query, it calls the appropriate DataFetcher for each field it encounters in query. A DataFetcher is an Interface with a single method, taking a single argument of type DataFetcherEnvironment.

```java
public interface DataFetcher<T> {
    T get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception;
}
```

Each DataFetcher it is going to return us a POJO that we will serve to our clients. We assume that you are familiarized with this concept so DTOs are already coded in the folder dto.

In order to create the datafetcher we need a new folder in our project called resolver. Inside we are going to create 2 new classes for our resolvers. We have one query to resolve so we need the method to handle it.

```java
public class CharacterResolver {

	private final CharacterService characterService;

	public CharacterResolver(CharacterService characterService) {
		super();
		this.characterService = characterService;
	}

	public CharacterOutDTO queryGetCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
		return characterService.findById(id);
	}
}
```

```java
public class StarshipResolver {

	private final StarshipService starshipService;

	public StarshipResolver(StarshipService starshipService) {
		super();
		this.starshipService = starshipService;
	}

	public StarshipOutDTO queryGetStarshipById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
		return  starshipService.findById(id);
	}
}
```

### The RuntimeWiring

The RuntimeWiring object maps our graphQL schema definition with the resolvers/datafetchers. We are going to put this code directly on `StarWarsApplication.java`. We need to map our 2 queries with the DataFetchers, in order to build the RuntimeWiring, as follows.

```java
public static RuntimeWiring buildGraphQLWiring(CharacterResolver characterResolver, StarshipResolver starshipResolver) {
	return RuntimeWiring.newRuntimeWiring()
			.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getCharacterById", characterResolver::queryGetCharacterById))
			.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getStarshipById", starshipResolver::queryGetStarshipById))
			.build();
}
```

### Load schema and parse to TypeDefinitionRegistry

Now we have all the ingredients to create the graphQL object which will be in charge to serve our API. So we are going to read our schema.graphqls file. You can put this code directly on StarWarsApplication class

```java
public static String readGraphQLSchema() {
	try {
		URL url = Thread.currentThread().getContextClassLoader().getResource("graphql/schema.graphqls");
		System.out.println(url);
		return Files.readString(Paths.get(url.toURI()), Charsets.UTF_8);
	} catch (Exception e) {
		throw new RuntimeException("Unexpected Error Reading GraphQL Shcema", e);
	}
}
```
Then we can build the GraphQL class.

```java
public static GraphQL buildGraphQL() {
	// Resolver-Service-Repository creation
	StarshipRepository starshipRepository = new StarshipRepository();
	CharacterRepository characterRepository = new CharacterRepository();

	StarshipService starshipService = new StarshipService(starshipRepository);
	CharacterService characterService = new CharacterService(characterRepository);

	CharacterResolver characterResolver = new CharacterResolver(characterService);
	StarshipResolver starshipResolver = new StarshipResolver(starshipService);
	
	// GraphQL creation
	String stringSchema = readGraphQLSchema();
	TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(stringSchema);
	
	RuntimeWiring runtimeWiring = buildGraphQLWiring(characterResolver, starshipResolver);
	GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);


	return GraphQL.newGraphQL(graphQLSchema).build();
}
```

### The HTTP server

With the previous steps we have covered the graphQL creation. We just need to create our HTTP server. We assume here you are familiarized with spark framework and HTTP protocol. So just put this code on the main method. GraphQL deliberately does not specify any transfer protocol, it is agnostic on this subject. However, here it is a community spec about serving [graphQL over http](https://github.com/graphql/graphql-over-http) that you can follow.

```java
public static void main(String[] args) {
	ObjectMapper objectMapper = new ObjectMapper();
	GraphQL graphQL = buildGraphQL();
	
	Spark.port(8080);
	Spark.post("/graphql", (req, res)->{
		res.header("Content-Type", "application/json");
		
		GraphQLHttpPostInput graphQLInput = objectMapper.readValue(req.body(), GraphQLHttpPostInput.class);

		ExecutionResult executionResult = graphQL.execute(graphQLInput.getQuery());

		return executionResult.toSpecification();
	}, objectMapper::writeValueAsString);
}
```