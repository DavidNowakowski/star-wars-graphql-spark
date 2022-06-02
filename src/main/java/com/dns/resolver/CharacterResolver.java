package com.dns.resolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.dns.dto.in.BiologicalInDTO;
import com.dns.dto.in.DroidInDTO;
import com.dns.dto.out.BiologicOutDTO;
import com.dns.dto.out.CharacterOutDTO;
import com.dns.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.TypeResolutionEnvironment;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

public class CharacterResolver {

	private final ObjectMapper objectMapper;

	private final CharacterService characterService;

	public CharacterResolver(ObjectMapper objectMapper, CharacterService characterService) {
		super();
		this.objectMapper = objectMapper;
		this.characterService = characterService;
	}

	public CompletableFuture<List<CharacterOutDTO>> characterBatchLoader(List<Integer> ids) {
		return CompletableFuture.supplyAsync(() -> characterService.findAllByIds(ids));
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
