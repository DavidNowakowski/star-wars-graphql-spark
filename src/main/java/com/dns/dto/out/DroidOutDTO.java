package com.dns.dto.out;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class DroidOutDTO extends CharacterOutDTO {

	private String primaryFunction;

	public DroidOutDTO() {
	}

	public DroidOutDTO(Integer id, String name, List<EpisodeDTO> appearsIn, String primaryFunction) {
		super(id, name, appearsIn);
		this.primaryFunction = primaryFunction;
	}

	public String getPrimaryFunction() {
		return primaryFunction;
	}

	public void setPrimaryFunction(String primaryFunction) {
		this.primaryFunction = primaryFunction;
	}
}
