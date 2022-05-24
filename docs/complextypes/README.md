# Complex types

So we are now familiar with the basic steps to create our graphQL API. Surely now you want to learn more. So let's make some improvements to our API. We want to create methods that allow us to **add/edit/delete** our characters and startships. We also want to extend our characters as follows.

**Character**

- There will be 2 kinds of `Character`: `Biological` and `Droid`.
- `Biological` **can** indicate their amount of credits.
- `Droid` **can** have a main function.

## The schema

### Interfaces and unions

Let´s introduce [interfaces](https://graphql.org/learn/schema/#interfaces) and [unions](https://graphql.org/learn/schema/#union-types). Both are similar concepts. They are used to define a common parent between 2 types. The main difference between them it is that interfaces require to implement mandatory fields in childrens. Union on the other hand does not include fields. 

We could implement Biological and Droid with union as:

```
union Character = Biological | Droid

type Biological {
  id: ID!
  name: String!
  appearsIn: [Episode]!
  totalCredits: Int
}

type Droid {
  id: ID!
  name: String!
  appearsIn: [Episode]!
  primaryFunction: String
}
```

Or using interface as:

```
interface Character {
  id: ID!
  name: String!
  appearsIn: [Episode]!
}

type Biological implements Character {
  id: ID!
  name: String!
  appearsIn: [Episode]!
  totalCredits: Int
}

type Droid implements Character {
  id: ID!
  name: String!
  appearsIn: [Episode]!
  primaryFunction: String
}
```

In this tutorial we have decided to go through the interface solution. Because it facilitates future queries from our customers.

### Input types

So far, we have only implemented queries that can be easily handled with default types. But in order to store new items will be useful provide [input](https://graphql.org/learn/schema/#input-types) definitions in our schema.

```
input StarshipInput {
  id: ID
  name: String!
  length: Int  
}

input BiologicalInput {
  id: ID
  name: String!
  appearsIn: [Episode]!
  totalCredits: Int
}

input DroidInput {
  id: ID
  name: String!
  appearsIn: [Episode]!
  primaryFunction: String
}
```

### Mutations

Now that we plan modify our data we need to define [mutations](https://graphql.org/learn/schema/#the-query-and-mutation-types)

```
type Mutation {
  saveStarship(starship: StarshipInput!, replaceNull: Boolean = false): Starship
  saveDroidCharacter(droid: DroidInput!, replaceNull: Boolean = false): Droid
  saveBiologicalCharacter(biological: BiologicalInput!, replaceNull: Boolean = false): Biological
  deleteCharacterById(id: Int!): Boolean
  deleteStarshipById(id: Int!): Boolean
  deleteAllCharacters: Boolean
  deleteAllStarships: Boolean
}
```

We have added `replaceNull` parameter for our save functions with a default value of false. If this value it is set as true then our save function will replace all not provided values as null. Also note there are not different methods for **add/edit**, we are just going to check if some ID it is provided when functions are called.

## The code with graphql-java

### DataFetchers and DTOs

We have modified our schema which means that we also have to modify our DTOs. We have updated our CharacterOutDTO to an abstract class and we have created two children, BiologicalOutDTO and DroidOutDTO, for them. We have also  added input types which means that input DTOs are required. Again we will not go into this in depth as we assume you already know how to implement DTOs.

Let´s directly implement our datafetcher methods. 

```java
public class CharacterResolver {

	private final ObjectMapper objectMapper;
	
	private final CharacterService characterService;
	
	public CharacterResolver(ObjectMapper objectMapper, CharacterService characterService) {
		super();
		this.objectMapper = objectMapper;
		this.characterService = characterService;
	}
	
	public CharacterOutDTO queryGetCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
		return characterService.findById(id);
	}
	
	public CharacterOutDTO mutationSaveDroidCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
			Map<String, Object> inMap = dataFetchingEnvironment.getArgument("droid");
			Boolean replaceNull = dataFetchingEnvironment.getArgument("replaceNull");
			DroidInDTO droidInDTO = objectMapper.convertValue(inMap, DroidInDTO.class);
			return characterService.saveDroidDTO(droidInDTO, replaceNull);
	}
	
	public CharacterOutDTO mutationSaveBiologicalCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument("biological");
		Boolean replaceNull = dataFetchingEnvironment.getArgument("replaceNull");
		BiologicalInDTO biologicalInDTO = objectMapper.convertValue(inMap, BiologicalInDTO.class);
		return characterService.saveBiologicalDTO(biologicalInDTO, replaceNull);
	}
	
	public Boolean mutationDeleteCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = dataFetchingEnvironment.getArgument("id");
		return characterService.deleteById(id);
	}
	
	public Boolean mutationDeleteAllCharacters(DataFetchingEnvironment dataFetchingEnvironment) {
		return characterService.deleteAllCharacters();
	}

	public GraphQLObjectType getCharacterTypeResolver(TypeResolutionEnvironment typeResolutionEnvironment) {
		Object javaObject = typeResolutionEnvironment.getObject();
		if (javaObject instanceof BiologicOutDTO) {
			return typeResolutionEnvironment.getSchema().getObjectType("Biological");
		} else {
			return typeResolutionEnvironment.getSchema().getObjectType("Droid");
		}
	}
}
```

Note here we have added `getCharacterTypeResolver` method. This function it is required by the framework in order to resolve interface or union types and return the correct schema. We have also added an objectMapper to parse our input types. The input types are resolved by default as `Map<String,Object>`.


```java
public class StarshipResolver {

	private final ObjectMapper objectMapper;

	private final StarshipService starshipService;

	public StarshipResolver(ObjectMapper objectMapper, StarshipService starshipService) {
		super();
		this.objectMapper = objectMapper;
		this.starshipService = starshipService;
	}

	public StarshipOutDTO queryGetStarshipById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
		return  starshipService.findById(id);
	}
	
	public StarshipOutDTO mutationSaveStarship(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument("starship");
		Boolean replaceNull = Boolean.parseBoolean(dataFetchingEnvironment.getArgument("replaceNull"));
		StarshipInDTO starshipInDTO = objectMapper.convertValue(inMap, StarshipInDTO.class);
		return starshipService.save(starshipInDTO, replaceNull);
	}

	public Boolean mutationDeleteStarshipById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument("id"));
		return starshipService.deleteById(id);
	}

	public Boolean mutationDeleteAllStarships(DataFetchingEnvironment dataFetchingEnvironment) {
		return starshipService.deleteAllStarships();
	}
}
```

### The RuntimeWiring

We also need to update our RuntimeWiring and provide the new functionality. We have also to register the `getCharacterTypeResolver` for our character type resolution.

```java
public static RuntimeWiring buildGraphQLWiring(CharacterResolver characterResolver, StarshipResolver starshipResolver, ObjectMapper objectMapper) {
	return RuntimeWiring.newRuntimeWiring()
			.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getCharacterById", characterResolver::queryGetCharacterById))
			.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getStarshipById", starshipResolver::queryGetStarshipById))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("saveDroidCharacter", characterResolver::mutationSaveDroidCharacter))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("saveBiologicalCharacter", characterResolver::mutationSaveBiologicalCharacter))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("saveStarship", starshipResolver::mutationSaveStarship))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("deleteCharacterById", characterResolver::mutationDeleteCharacterById))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("deleteAllCharacters", characterResolver::mutationDeleteAllCharacters))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("deleteStarshipById", starshipResolver::mutationDeleteStarshipById))
			.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("deleteAllStarships", starshipResolver::mutationDeleteAllStarships))
			.type(TypeRuntimeWiring.newTypeWiring("Character").typeResolver(characterResolver::getCharacterTypeResolver))
			.build();
}
```