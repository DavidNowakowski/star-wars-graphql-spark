package com.dns.entity;

import java.util.List;

public class CharacterEntity {

	private Integer id;

	private CharacterKind characterKind;

	private String name;

	private List<Integer> friendsIds;

	private List<EpisodeEntity> appearsIn;

	private Integer starshipId;

	private Integer totalCredits;

	private String primaryFunction;

	public CharacterEntity() {
	}

	public CharacterEntity(Integer id, CharacterKind characterKind, String name, List<Integer> friendsIds,
			List<EpisodeEntity> appearsIn, Integer starshipId, Integer totalCredits, String primaryFunction) {
		super();
		this.id = id;
		this.characterKind = characterKind;
		this.name = name;
		this.friendsIds = friendsIds;
		this.appearsIn = appearsIn;
		this.starshipId = starshipId;
		this.totalCredits = totalCredits;
		this.primaryFunction = primaryFunction;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CharacterKind getCharacterKind() {
		return characterKind;
	}

	public void setCharacterKind(CharacterKind characterKind) {
		this.characterKind = characterKind;
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

	public void setFriendsIds(List<Integer> fiendsIds) {
		this.friendsIds = fiendsIds;
	}

	public List<EpisodeEntity> getAppearsIn() {
		return appearsIn;
	}

	public void setAppearsIn(List<EpisodeEntity> appearsIn) {
		this.appearsIn = appearsIn;
	}

	public Integer getStarshipId() {
		return starshipId;
	}

	public void setStarshipId(Integer starshipId) {
		this.starshipId = starshipId;
	}

	public Integer getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(Integer totalCredits) {
		this.totalCredits = totalCredits;
	}

	public String getPrimaryFunction() {
		return primaryFunction;
	}

	public void setPrimaryFunction(String primaryFunction) {
		this.primaryFunction = primaryFunction;
	}

}
