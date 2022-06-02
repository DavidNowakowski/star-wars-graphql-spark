package com.dns.service;

import java.util.ArrayList;
import java.util.List;

import com.dns.dto.EpisodeDTO;
import com.dns.dto.out.CharacterOutDTO;
import com.dns.entity.CharacterEntity;
import com.dns.entity.EpisodeEntity;
import com.dns.repository.CharacterRepository;

public class CharacterService {

	private final CharacterRepository characterRepository;

	public CharacterService(CharacterRepository characterRepository) {
		this.characterRepository = characterRepository;
	}

	public CharacterOutDTO findById(Integer id) {
		CharacterEntity characterEntity = characterRepository.findById(id);
		return toCharacterOutDTO(characterEntity);
	}

	private CharacterOutDTO toCharacterOutDTO(CharacterEntity entity) {
		return new CharacterOutDTO(entity.getId(), entity.getName(), toEpisodeDTO(entity.getAppearsIn()));
	}

	private List<EpisodeDTO> toEpisodeDTO(List<EpisodeEntity> appearsIn) {
		ArrayList<EpisodeDTO> episodesDTO = new ArrayList<>();
		for (EpisodeEntity episodeEntity : appearsIn) {
			if (EpisodeEntity.A_NEW_HOPE.equals(episodeEntity)) {
				episodesDTO.add(EpisodeDTO.A_NEW_HOPE);
			} else if (EpisodeEntity.THE_EMPIRE_STRIKES_BACK.equals(episodeEntity)) {
				episodesDTO.add(EpisodeDTO.THE_EMPIRE_STRIKES_BACK);
			} else {
				episodesDTO.add(EpisodeDTO.RETURN_OF_THE_JEDI);
			}
		}
		return episodesDTO;
	}
}
