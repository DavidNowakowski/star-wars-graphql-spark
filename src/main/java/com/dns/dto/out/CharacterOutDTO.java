package com.dns.dto.out;

import java.util.List;

import com.dns.dto.EpisodeDTO;

public abstract class CharacterOutDTO {

	protected Integer id;

	protected String name;

	protected List<Integer> friends;

	protected List<EpisodeDTO> appearsIn;

	public CharacterOutDTO() {
	}

	public CharacterOutDTO(Integer id, String name, List<Integer> friends, List<EpisodeDTO> appearsIn) {
		super();
		this.id = id;
		this.name = name;
		this.friends = friends;
		this.appearsIn = appearsIn;
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

	public List<Integer> getFriends() {
		return friends;
	}

	public void setFriends(List<Integer> friends) {
		this.friends = friends;
	}

	public List<EpisodeDTO> getAppearsIn() {
		return appearsIn;
	}

	public void setAppearsIn(List<EpisodeDTO> appearsIn) {
		this.appearsIn = appearsIn;
	}
}
