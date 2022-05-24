# Relations

You have come so far, at this point you have wasted so much time that you cannot give up without knowing how to resolve the relationships. The next requirements will be:

**Characters**

- `Character` **can** have a list of friends. Which means a list of `Character`.
- `Biological` **can** have a `Starship`.

## The schema

### The output types

Once again we have to modify our schema to support this new feature. Our output types look like this.

```
interface Character {
  id: ID!
  name: String!
  friends: [Character]
  appearsIn: [Episode]!
}

type Biological implements Character {
  id: ID!
  name: String!
  friends: [Character]
  appearsIn: [Episode]!
  starship: Starship
  totalCredits: Int
}

type Droid implements Character {
  id: ID!
  name: String!
  friends: [Character]
  appearsIn: [Episode]!
  primaryFunction: String
}

type Starship {
  id: ID!
  name: String!
  length: Int  
}
```

### The input types

We also need to update our input types to support relationships. Here we are just passing fiendsIds for the fiends relationship. We just want to keep this example simpler.

```
input StarshipInput {
  id: ID
  name: String!
  length: Int  
}

input BiologicalInput {
  id: ID
  name: String!
  friendsIds: [Int]
  appearsIn: [Episode]!
  starship: StarshipInput
  totalCredits: Int
}

input DroidInput {
  id: ID
  name: String!
  friendsIds: [Int]
  appearsIn: [Episode]!
  primaryFunction: String
}
```

## The code with graphql-java

### DataFetchers and DTOs

Again, the DTOs have been modified. Note here specially that for outDTOs the relations are done just through ids instead object reference. For the DataFetchers we need to add the relation resolution.

In `CharacterResolver`:

```java
public List<CharacterOutDTO> characterFriends(DataFetchingEnvironment dataFetchingEnvironment) {
	CharacterOutDTO character = dataFetchingEnvironment.getSource();
	List<Integer> friendsIds = character.getFriends();
	if (friendsIds != null) {
		return characterService.findAllByIds(friendsIds);
	} else {
		return null;
	}
}
```

In `StarshipResolver`:

```java
public StarshipOutDTO characterStarship(DataFetchingEnvironment dataFetchingEnvironment) {
	BiologicOutDTO biologicDTO = dataFetchingEnvironment.getSource();
	Integer id = biologicDTO.getStarshipID();
	if (id != null) {
		return starshipService.findById(id);
	} else {
		return null;
	}
}
```

### The RuntimeWiring

Now we have to add the methods in RuntimeWiring:

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
			.type(TypeRuntimeWiring.newTypeWiring("Biological").dataFetcher("friends", characterResolver::characterFriends))
			.type(TypeRuntimeWiring.newTypeWiring("Droid").dataFetcher("friends", characterResolver::characterFriends))
			.type(TypeRuntimeWiring.newTypeWiring("Biological").dataFetcher("starship", starshipResolver::characterStarship))
			.build();
}
```