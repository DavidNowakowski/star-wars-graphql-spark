package com.dns.dto.in;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class BiologicalInDTO {

	private Integer id;

	private String name;

	private List<EpisodeDTO> appearsIn;

	private Integer totalCredits;

	public BiologicalInDTO() {
	}

	public BiologicalInDTO(Integer id, String name, List<EpisodeDTO> appearsIn, Integer totalCredits) {
		super();
		this.id = id;
		this.name = name;
		this.appearsIn = appearsIn;
		this.totalCredits = totalCredits;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EpisodeDTO> getAppearsIn() {
		return appearsIn;
	}

	public void setAppearsIn(List<EpisodeDTO> appearsIn) {
		this.appearsIn = appearsIn;
	}

	public Integer getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(Integer totalCredits) {
		this.totalCredits = totalCredits;
	}

}
