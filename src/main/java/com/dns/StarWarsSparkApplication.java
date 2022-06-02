package com.dns;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.dns.controller.GraphQLController;
import com.dns.repository.CharacterRepository;
import com.dns.repository.StarshipRepository;
import com.dns.resolver.CharacterResolver;
import com.dns.resolver.StarshipResolver;
import com.dns.service.CharacterService;
import com.dns.service.StarshipService;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQL;
import graphql.com.google.common.base.Charsets;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import spark.Spark;

public class StarWarsSparkApplication {

	public static void main(String[] args) {
		ObjectMapper objectMapper = new ObjectMapper();

		GraphQLController graphQLController = buildController(objectMapper);
		
		Spark.port(8080);
		Spark.post("/graphql", graphQLController::post, objectMapper::writeValueAsString);
	}

	private static GraphQLController buildController(ObjectMapper objectMapper) {
		StarshipRepository starshipRepository = new StarshipRepository();
		CharacterRepository characterRepository = new CharacterRepository();

		StarshipService starshipService = new StarshipService(starshipRepository);
		CharacterService characterService = new CharacterService(characterRepository);

		CharacterResolver characterResolver = new CharacterResolver(objectMapper, characterService);
		StarshipResolver starshipResolver = new StarshipResolver(objectMapper, starshipService);

		GraphQL graphQL = buildGraphQL(characterResolver, starshipResolver, objectMapper);
		GraphQLController graphQLController = new GraphQLController(objectMapper, graphQL);
		return graphQLController;
	}

	public static GraphQL buildGraphQL(CharacterResolver characterResolver, StarshipResolver starshipResolver,
			ObjectMapper objectMapper) {
		String stringSchema = readGraphQLSchema();
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(stringSchema);

		RuntimeWiring runtimeWiring = buildGraphQLWiring(characterResolver, starshipResolver, objectMapper);
		GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);

		return GraphQL.newGraphQL(graphQLSchema).build();
	}

	public static String readGraphQLSchema() {
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("graphql/schema.graphqls");
			System.out.println(url);
			return Files.readString(Paths.get(url.toURI()), Charsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Error Reading GraphQL Shcema", e);
		}
	}

	// Mapping
	public static RuntimeWiring buildGraphQLWiring(CharacterResolver characterResolver, StarshipResolver starshipResolver, ObjectMapper objectMapper) {
		return RuntimeWiring.newRuntimeWiring()
				.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getCharacterById", characterResolver::queryGetCharacterById))
				.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getStarshipById", starshipResolver::queryGetStarshipById))
				.build();
	}

}
