package com.dns.resolver;

import java.util.Map;

import com.dns.dto.in.StarshipInDTO;
import com.dns.dto.out.BiologicOutDTO;
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

	public StarshipOutDTO characterStarship(DataFetchingEnvironment dataFetchingEnvironment) {
		BiologicOutDTO biologicDTO = dataFetchingEnvironment.getSource();
		Integer id = biologicDTO.getStarshipID();
		if (id != null) {
			return starshipService.findById(id);
		} else {
			return null;
		}
	}

	public StarshipOutDTO mutationSaveStarship(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument("starship");
		Boolean replaceNull = dataFetchingEnvironment.getArgument("replaceNull");
		StarshipInDTO starshipInDTO = objectMapper.convertValue(inMap, StarshipInDTO.class);
		return starshipService.save(starshipInDTO, replaceNull);
	}

	public Boolean mutationDeleteStarshipById(DataFetchingEnvironment dataFetchingEnvironment) { 
		Integer id = dataFetchingEnvironment.getArgument("id");
		return starshipService.deleteById(id);
	}

	public Boolean mutationDeleteAllStarships(DataFetchingEnvironment dataFetchingEnvironment) {
		return starshipService.deleteAllStarships();
	}
}
