package com.dns.resolver;

import com.dns.dto.out.CharacterOutDTO;
import com.dns.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

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

}
