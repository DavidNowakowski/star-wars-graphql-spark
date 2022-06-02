package com.dns.service;

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

	public static StarshipOutDTO toOutStarshipDTO(StarshipEntity starshipEntity) {
		return new StarshipOutDTO(starshipEntity.getId(), starshipEntity.getName(), starshipEntity.getLenght());
	}
}
