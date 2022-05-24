package com.dns.resolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

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

	private static final String DROID_TYPE = "Droid";

	private static final String BIOLOGICAL_TYPE = "Biological";

	private static final String ID_ARG = "id";

	private static final String REPLACE_NULL_ARG = "replaceNull";

	private static final String DROID_ARG = "droid";

	private static final String BIOLOGICAL_ARG = "biological";

	private static final String CHARACTER_REGISTRY = "character";

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

	public CompletableFuture<CharacterOutDTO> queryGetCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
		DataLoader<Integer, CharacterOutDTO> characterLoader = dataFetchingEnvironment.getDataLoader(CHARACTER_REGISTRY);
		Integer id = Integer.parseInt(dataFetchingEnvironment.getArgument(ID_ARG));
		return characterLoader.load(id);
	}

	public CompletableFuture<List<CharacterOutDTO>> characterFriends(DataFetchingEnvironment dataFetchingEnvironment) {
		CharacterOutDTO character = dataFetchingEnvironment.getSource();
		List<Integer> friendsIds = character.getFriends();
		if (friendsIds != null) {
			DataLoader<Integer, CharacterOutDTO> characterLoader = dataFetchingEnvironment
					.getDataLoader(CHARACTER_REGISTRY);
			return characterLoader.loadMany(friendsIds);
		} else {
			return null;
		}
	}

	public CharacterOutDTO mutationSaveDroidCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument(DROID_ARG);
		Boolean replaceNull = dataFetchingEnvironment.getArgument(REPLACE_NULL_ARG);
		DroidInDTO droidInDTO = objectMapper.convertValue(inMap, DroidInDTO.class);
		return characterService.saveDroidDTO(droidInDTO, replaceNull);
	}

	public CharacterOutDTO mutationSaveBiologicalCharacter(DataFetchingEnvironment dataFetchingEnvironment) {
		Map<String, Object> inMap = dataFetchingEnvironment.getArgument(BIOLOGICAL_ARG);
		Boolean replaceNull = dataFetchingEnvironment.getArgument(REPLACE_NULL_ARG);
		BiologicalInDTO biologicalInDTO = objectMapper.convertValue(inMap, BiologicalInDTO.class);
		return characterService.saveBiologicalDTO(biologicalInDTO, replaceNull);
	}

	public Boolean mutationDeleteCharacterById(DataFetchingEnvironment dataFetchingEnvironment) {
		Integer id = dataFetchingEnvironment.getArgument(ID_ARG);
		return characterService.deleteById(id);
	}

	public Boolean mutationDeleteAllCharacters(DataFetchingEnvironment dataFetchingEnvironment) {
		return characterService.deleteAllCharacters();
	}

	public GraphQLObjectType getCharacterTypeResolver(TypeResolutionEnvironment typeResolutionEnvironment) {
		Object javaObject = typeResolutionEnvironment.getObject();
		if (javaObject instanceof BiologicOutDTO) {
			return typeResolutionEnvironment.getSchema().getObjectType(BIOLOGICAL_TYPE);
		} else {
			return typeResolutionEnvironment.getSchema().getObjectType(DROID_TYPE);
		}
	}

}
