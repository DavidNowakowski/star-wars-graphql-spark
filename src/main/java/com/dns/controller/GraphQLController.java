package com.dns.controller;

import java.util.Map;

import com.dns.GraphQLHttpPostInput;
import com.dns.resolver.CharacterResolver;
import com.dns.resolver.StarshipResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import spark.Request;
import spark.Response;

public class GraphQLController {

	private final ObjectMapper objectMapper;

	private final GraphQL graphQL;

	private final CharacterResolver characterResolver;

	private final StarshipResolver starshipResolver;

	public GraphQLController(CharacterResolver characterResolver, StarshipResolver starshipResolver,
			ObjectMapper objectMapper, GraphQL graphQL) {
		super();
		this.characterResolver = characterResolver;
		this.starshipResolver = starshipResolver;
		this.objectMapper = objectMapper;
		this.graphQL = graphQL;
	}

	public Map<String, Object> post(Request req, Response res) throws Exception {
		res.header("Content-Type", "application/json");
		GraphQLHttpPostInput graphQLInput = objectMapper.readValue(req.body(), GraphQLHttpPostInput.class);

		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(graphQLInput.getQuery()).build();

		ExecutionResult executionResult = graphQL.execute(executionInput);

		return executionResult.toSpecification();

	}

}
