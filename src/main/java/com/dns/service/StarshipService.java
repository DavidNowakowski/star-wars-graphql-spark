package com.dns.service;

import java.util.ArrayList;
import java.util.List;

import com.dns.dto.in.StarshipInDTO;
import com.dns.dto.out.StarshipOutDTO;
import com.dns.entity.StarshipEntity;
import com.dns.repository.CharacterRepository;
import com.dns.repository.StarshipRepository;

public class StarshipService {

	private final StarshipRepository starshipRepository;

	private final CharacterRepository characterRepository;

	public StarshipService(StarshipRepository starshipRepository, CharacterRepository characterRepository) {
		super();
		this.starshipRepository = starshipRepository;
		this.characterRepository = characterRepository;
	}

	public List<StarshipOutDTO> findAllById(List<Integer> ids) {
		List<StarshipEntity> starshipEntities = starshipRepository.findByAllByIds(ids);
		return toStarshipOutDTOs(starshipEntities);
	}

	public StarshipOutDTO findById(Integer id) {
		StarshipEntity starshipEntity = starshipRepository.findById(id);
		return toOutStarshipDTO(starshipEntity);
	}

	public StarshipOutDTO save(StarshipInDTO startInDTO, Boolean replaceNull) {
		StarshipEntity starshipEntity = toEntity(startInDTO);
		return toOutStarshipDTO(starshipRepository.save(starshipEntity, replaceNull));
	}

	public Boolean deleteById(Integer id) {
		characterRepository.deleteStarshipRelations(id);
		return starshipRepository.deleteById(id);
	}

	public Boolean deleteAllStarships() {
		starshipRepository.deleteAll();
		characterRepository.deleteAllStartshipRelations();
		return true;
	}

	private List<StarshipOutDTO> toStarshipOutDTOs(List<StarshipEntity> starshipEntities) {
		List<StarshipOutDTO> startshipDTOs = new ArrayList<>();
		for (var starshipEntity : starshipEntities) {
			startshipDTOs.add(toOutStarshipDTO(starshipEntity));
		}
		return startshipDTOs;
	}

	public static StarshipOutDTO toOutStarshipDTO(StarshipEntity starshipEntity) {
		return new StarshipOutDTO(starshipEntity.getId(), starshipEntity.getName(), starshipEntity.getLenght());
	}

	public static StarshipEntity toEntity(StarshipInDTO startInDTO) {
		return new StarshipEntity(startInDTO.getId(), startInDTO.getName(), startInDTO.getLength());
	}
}
