package com.dns.dto.out;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class BiologicOutDTO extends CharacterOutDTO {

	private Integer starshipID;

	private Integer totalCredits;

	public BiologicOutDTO(Integer id, String name, List<Integer> friends, List<EpisodeDTO> appearsIn, Integer starshipID,
			Integer totalCredits) {
		super(id, name, friends, appearsIn);
		this.starshipID = starshipID;
		this.totalCredits = totalCredits;
	}

	public Integer getStarshipID() {
		return starshipID;
	}

	public void setStarshipID(Integer starshipID) {
		this.starshipID = starshipID;
	}

	public Integer getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(Integer totalCredits) {
		this.totalCredits = totalCredits;
	}

}
