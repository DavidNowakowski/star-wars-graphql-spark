package com.dns.service;

import com.dns.dto.in.StarshipInDTO;
import com.dns.dto.out.StarshipOutDTO;
import com.dns.entity.StarshipEntity;
import com.dns.repository.StarshipRepository;

public class StarshipService {

	private final StarshipRepository starshipRepository;

	public StarshipService(StarshipRepository starshipRepository) {
		super();
		this.starshipRepository = starshipRepository;
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
		return starshipRepository.deleteById(id);
	}

	public Boolean deleteAllStarships() {
		starshipRepository.deleteAll();
		return true;
	}

	public static StarshipOutDTO toOutStarshipDTO(StarshipEntity starshipEntity) {
		return new StarshipOutDTO(starshipEntity.getId(), starshipEntity.getName(), starshipEntity.getLenght());
	}

	public static StarshipEntity toEntity(StarshipInDTO startInDTO) {
		return new StarshipEntity(startInDTO.getId(), startInDTO.getName(), startInDTO.getLength());
	}
}
