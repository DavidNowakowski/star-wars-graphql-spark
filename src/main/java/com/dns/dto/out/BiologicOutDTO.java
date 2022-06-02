package com.dns.dto.out;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class BiologicOutDTO extends CharacterOutDTO {

	private Integer totalCredits;

	public BiologicOutDTO(Integer id, String name, List<EpisodeDTO> appearsIn, Integer totalCredits) {
		super(id, name, appearsIn);
		this.totalCredits = totalCredits;
	}

	public Integer getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(Integer totalCredits) {
		this.totalCredits = totalCredits;
	}

}
