package com.dns.service;

import java.util.ArrayList;
import java.util.List;

import com.dns.dto.EpisodeDTO;
import com.dns.dto.in.BiologicalInDTO;
import com.dns.dto.in.DroidInDTO;
import com.dns.dto.out.BiologicOutDTO;
import com.dns.dto.out.CharacterOutDTO;
import com.dns.dto.out.DroidOutDTO;
import com.dns.entity.CharacterEntity;
import com.dns.entity.CharacterKind;
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

	public CharacterOutDTO saveBiologicalDTO(BiologicalInDTO biologicalInDTO, Boolean replaceNull) {
		CharacterEntity characterEntity = toEntity(biologicalInDTO);
		return toOrganicOutDTO(characterRepository.save(characterEntity, replaceNull));
	}

	public CharacterOutDTO saveDroidDTO(DroidInDTO droidInDTO, Boolean replaceNull) {
		CharacterEntity characterEntity = toEntity(droidInDTO);
		return toDroidOutDTO(characterRepository.save(characterEntity, replaceNull));
	}

	public Boolean deleteById(Integer id) {
		return characterRepository.deleteById(id);
	}

	public Boolean deleteAllCharacters() {
		characterRepository.deleteAll();
		return true;
	}

	private CharacterEntity toEntity(BiologicalInDTO biologicalInDTO) {
		return new CharacterEntity(biologicalInDTO.getId(), CharacterKind.ORGANIC, biologicalInDTO.getName(),
				toEpisodeEntity(biologicalInDTO.getAppearsIn()), biologicalInDTO.getTotalCredits(), null);
	}

	private CharacterEntity toEntity(DroidInDTO droidInDTO) {
		return new CharacterEntity(droidInDTO.getId(), CharacterKind.ORGANIC, droidInDTO.getName(),
				toEpisodeEntity(droidInDTO.getAppearsIn()), null, droidInDTO.getPrimaryFunction());
	}

	private List<EpisodeEntity> toEpisodeEntity(List<EpisodeDTO> appearsIn) {
		ArrayList<EpisodeEntity> episodesDTO = new ArrayList<>();
		for (EpisodeDTO episodeDTO : appearsIn) {
			if (EpisodeDTO.A_NEW_HOPE.equals(episodeDTO)) {
				episodesDTO.add(EpisodeEntity.A_NEW_HOPE);
			} else if (EpisodeDTO.THE_EMPIRE_STRIKES_BACK.equals(episodeDTO)) {
				episodesDTO.add(EpisodeEntity.THE_EMPIRE_STRIKES_BACK);
			} else {
				episodesDTO.add(EpisodeEntity.RETURN_OF_THE_JEDI);
			}
		}
		return episodesDTO;
	}

	private CharacterOutDTO toCharacterOutDTO(CharacterEntity entity) {
		if (entity.getCharacterKind().equals(CharacterKind.ORGANIC)) {
			return toOrganicOutDTO(entity);
		} else {
			return toDroidOutDTO(entity);
		}
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

	private DroidOutDTO toDroidOutDTO(CharacterEntity entity) {
		return new DroidOutDTO(entity.getId(), entity.getName(), toEpisodeDTO(entity.getAppearsIn()),
				entity.getPrimaryFunction());
	}

	private BiologicOutDTO toOrganicOutDTO(CharacterEntity characterEntity) {
		return new BiologicOutDTO(characterEntity.getId(), characterEntity.getName(),
				toEpisodeDTO(characterEntity.getAppearsIn()), characterEntity.getTotalCredits());
	}
}
