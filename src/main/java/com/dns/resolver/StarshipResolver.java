package com.dns.resolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import com.dns.dto.in.StarshipInDTO;
import com.dns.dto.out.BiologicOutDTO;
import com.dns.dto.out.StarshipOutDTO;
import com.dns.service.StarshipService;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

public class StarshipResolver {

	private static final String ID_ARG = "id";
	
	private static final String STARSHIP_ARG = "starship";

	private static final String REPLACE_NULL_ARG = "replaceNull";

	private static final String STARSHIP_REGISTRY = "starship";

	private final ObjectMapper objectMapper;

	private final StarshipService starshipService;

	public StarshipResolver(ObjectMapper objectMapper, StarshipService starshipService) {
		super();
		this.objectMapper = objectMapper;
		this.starshipService = starshipService;
	}

	public CompletableFuture<List<StarshipOutDTO>> starshipBatchLoader(List<Integer> ids) {
		return CompletableFuture.supplyAsync(() -> starshipService.findAllById(ids));
	}

	public CompletableFuture<StarshipOutDTO> queryGetStarshipById(DataFetchingEnvironment dataFetchingEnvironment) {
		DataLoader<Integer, StarshipOutDTO> starshipLoader = dataFetchingEnvironment.getDataLoader(STARSHIP_REGISTRY);
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument(ID_ARG));
		return starshipLoader.load(id);
	}

	public CompletableFuture<StarshipOutDTO> characterStarship(DataFetchingEnvironment dataFetchingEnvironment) {
		DataLoader<Integer, StarshipOutDTO> starshipLoader = dataFetchingEnvironment.getDataLoader(STARSHIP_REGISTRY);
		BiologicOutDTO biologicDTO = dataFetchingEnvironment.getSource();
		Integer id = biologicDTO.getStarshipID();
		if (id != null) {
			return starshipLoader.load(id);
		} else {
			return null;
		}
	}

	public StarshipOutDTO mutationSaveStarship(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument(STARSHIP_ARG);
		Boolean replaceNull = dataFetchingEnvironment.getArgument(REPLACE_NULL_ARG);
		StarshipInDTO starshipInDTO = objectMapper.convertValue(inMap, StarshipInDTO.class);
		return starshipService.save(starshipInDTO, replaceNull);
	}

	public Boolean mutationDeleteStarshipById(DataFetchingEnvironment dataFetchingEnvironment) { 
		Integer id = dataFetchingEnvironment.getArgument(ID_ARG);
		return starshipService.deleteById(id);
	}

	public Boolean mutationDeleteAllStarships(DataFetchingEnvironment dataFetchingEnvironment) {
		return starshipService.deleteAllStarships();
	}
}
