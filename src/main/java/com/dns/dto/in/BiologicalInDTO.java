package com.dns.dto.in;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class BiologicalInDTO {

	private Integer id;

	private String name;

	private List<Integer> friendsIds;

	private List<EpisodeDTO> appearsIn;

	private StarshipInDTO starship;

	private Integer totalCredits;

	public BiologicalInDTO() {
	}

	public BiologicalInDTO(Integer id, String name, List<Integer> friendsIds, List<EpisodeDTO> appearsIn,
			StarshipInDTO starship, Integer totalCredits) {
		super();
		this.id = id;
		this.name = name;
		this.friendsIds = friendsIds;
		this.appearsIn = appearsIn;
		this.starship = starship;
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

	public List<Integer> getFriendsIds() {
		return friendsIds;
	}

	public void setFriendsIds(List<Integer> friendsIds) {
		this.friendsIds = friendsIds;
	}

	public List<EpisodeDTO> getAppearsIn() {
		return appearsIn;
	}

	public void setAppearsIn(List<EpisodeDTO> appearsIn) {
		this.appearsIn = appearsIn;
	}

	public StarshipInDTO getStarship() {
		return starship;
	}

	public void setStarship(StarshipInDTO starship) {
		this.starship = starship;
	}

	public Integer getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(Integer totalCredits) {
		this.totalCredits = totalCredits;
	}

}
