package com.dns.dto.in;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public class DroidInDTO {

	private Integer id;

	private String name;

	private List<Integer> friendsIds;

	private List<EpisodeDTO> appearsIn;

	private String primaryFunction;

	public DroidInDTO() {
	}

	public DroidInDTO(Integer id, String name, List<Integer> friendsIds, List<EpisodeDTO> appearsIn, String primaryFunction) {
		super();
		this.id = id;
		this.name = name;
		this.friendsIds = friendsIds;
		this.appearsIn = appearsIn;
		this.primaryFunction = primaryFunction;
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

	public String getPrimaryFunction() {
		return primaryFunction;
	}

	public void setPrimaryFunction(String primaryFunction) {
		this.primaryFunction = primaryFunction;
	}

}
