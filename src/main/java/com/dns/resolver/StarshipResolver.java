package com.dns.resolver;

import com.dns.dto.out.StarshipOutDTO;
import com.dns.service.StarshipService;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

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
		return starshipService.findById(id);
	}
}
