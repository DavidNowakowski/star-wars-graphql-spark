package com.dns.entity;

import java.util.List;

public class CharacterEntity {

	private Integer id;

	private String name;

	private List<EpisodeEntity> appearsIn;

	public CharacterEntity() {
	}

	public CharacterEntity(Integer id, String name, List<EpisodeEntity> appearsIn) {
		super();
		this.id = id;
		this.name = name;
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

	public List<EpisodeEntity> getAppearsIn() {
		return appearsIn;
	}

	public void setAppearsIn(List<EpisodeEntity> appearsIn) {
		this.appearsIn = appearsIn;
	}

}
