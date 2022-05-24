package com.dns.entity;

public class StarshipEntity {

	private Integer id;

	private String name;

	private Integer lenght;

	public StarshipEntity() {
	}

	public StarshipEntity(Integer id, String name, Integer lenght) {
		super();
		this.id = id;
		this.name = name;
		this.lenght = lenght;
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

	public Integer getLenght() {
		return lenght;
	}

	public void setLenght(Integer lenght) {
		this.lenght = lenght;
	}
}
