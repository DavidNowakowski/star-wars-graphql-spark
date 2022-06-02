package com.dns.service;

import java.util.ArrayList;
import java.util.List;

import com.dns.dto.EpisodeDTO;
import com.dns.dto.in.BiologicalInDTO;
import com.dns.dto.in.DroidInDTO;
import com.dns.dto.in.StarshipInDTO;
import com.dns.dto.out.BiologicOutDTO;
import com.dns.dto.out.CharacterOutDTO;
import com.dns.dto.out.DroidOutDTO;
import com.dns.entity.CharacterEntity;
import com.dns.entity.CharacterKind;
import com.dns.entity.EpisodeEntity;
import com.dns.entity.StarshipEntity;
import com.dns.repository.CharacterRepository;
import com.dns.repository.StarshipRepository;

public class CharacterService {

	private final CharacterRepository characterRepository;

	private final StarshipRepository starshipRepository;

	public CharacterService(CharacterRepository characterRepository, StarshipRepository starshipRepository) {
		this.characterRepository = characterRepository;
		this.starshipRepository = starshipRepository;
	}

	public List<CharacterOutDTO> findAllByIds(List<Integer> ids) {
		List<CharacterEntity> characterEntities = characterRepository.findAllByIds(ids);
		return toCharacterOutDTOs(characterEntities);
	}

	public CharacterOutDTO findById(Integer id) {
		CharacterEntity characterEntity = characterRepository.findById(id);
		return toCharacterOutDTO(characterEntity);
	}

	public CharacterOutDTO saveBiologicalDTO(BiologicalInDTO biologicalInDTO, Boolean replaceNull) {
		checkStarshipRelation(biologicalInDTO);
		checkFriendsRelation(biologicalInDTO.getFriendsIds());
		StarshipEntity starshipEntity = saveStarship(biologicalInDTO, replaceNull);
		CharacterEntity characterEntity = toEntity(biologicalInDTO, starshipEntity);
		return toOrganicOutDTO(characterRepository.save(characterEntity, replaceNull));
	}

	private StarshipEntity saveStarship(BiologicalInDTO biologicalInDTO, Boolean replaceNull) {
		StarshipInDTO starshipInDTO = biologicalInDTO.getStarship();
		StarshipEntity starshipEntity = null;
		if (starshipInDTO != null) {
			starshipEntity = StarshipService.toEntity(starshipInDTO);
			starshipEntity = starshipRepository.save(starshipEntity, replaceNull);
		}
		return starshipEntity;
	}

	private void checkFriendsRelation(List<Integer> friendsIds) {
		if (friendsIds != null) {
			List<Integer> notFoundFriends = new ArrayList<>();
			for (Integer friendId : friendsIds) {
				if (characterRepository.findById(friendId) == null) {
					notFoundFriends.add(friendId);
				}
			}
			if (notFoundFriends.size() > 0) {
				throw new RuntimeException(
						"Friends IDs not found in DB: " + notFoundFriends.toString() + ", cannot write relation");
			}
		}
	}

	private void checkStarshipRelation(BiologicalInDTO biologicalInDTO) {
		StarshipInDTO starshipInDTO = biologicalInDTO.getStarship();
		if (starshipInDTO != null && starshipInDTO.getId() != null
				&& starshipRepository.findById(starshipInDTO.getId()) == null) {
			throw new RuntimeException(
					"Starship ID not found in DB: " + starshipInDTO.getId() + ", cannot write relation");
		}
	}

	public CharacterOutDTO saveDroidDTO(DroidInDTO droidInDTO, Boolean replaceNull) {
		checkFriendsRelation(droidInDTO.getFriendsIds());
		CharacterEntity characterEntity = toEntity(droidInDTO);
		return toDroidOutDTO(characterRepository.save(characterEntity, replaceNull));
	}

	public Boolean deleteById(Integer id) {
		characterRepository.deleteFriendRelations(id);
		return characterRepository.deleteById(id);
	}

	public Boolean deleteAllCharacters() {
		characterRepository.deleteAll();
		return true;
	}

	private CharacterEntity toEntity(BiologicalInDTO biologicalInDTO, StarshipEntity starshipEntity) {
		Integer starshipId = null;
		if (starshipEntity != null) {
			starshipId = starshipEntity.getId();
		}
		return new CharacterEntity(biologicalInDTO.getId(), CharacterKind.ORGANIC, biologicalInDTO.getName(),
				biologicalInDTO.getFriendsIds(), toEpisodeEntity(biologicalInDTO.getAppearsIn()), starshipId,
				biologicalInDTO.getTotalCredits(), null);
	}

	private CharacterEntity toEntity(DroidInDTO droidInDTO) {
		return new CharacterEntity(droidInDTO.getId(), CharacterKind.ORGANIC, droidInDTO.getName(),
				droidInDTO.getFriendsIds(), toEpisodeEntity(droidInDTO.getAppearsIn()), null, null,
				droidInDTO.getPrimaryFunction());
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
	
	private List<CharacterOutDTO> toCharacterOutDTOs(List<CharacterEntity> characterEntities) {
		List<CharacterOutDTO> characterOutDTOs = new ArrayList<>();
		for (var characterEntity : characterEntities) {
			characterOutDTOs.add(toCharacterOutDTO(characterEntity));
		}
		return characterOutDTOs;
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
		return new DroidOutDTO(entity.getId(), entity.getName(), entity.getFriendsIds(),
				toEpisodeDTO(entity.getAppearsIn()), entity.getPrimaryFunction());
	}

	private BiologicOutDTO toOrganicOutDTO(CharacterEntity characterEntity) {
		return new BiologicOutDTO(characterEntity.getId(), characterEntity.getName(), characterEntity.getFriendsIds(),
				toEpisodeDTO(characterEntity.getAppearsIn()), characterEntity.getStarshipId(),
				characterEntity.getTotalCredits());
	}
}
