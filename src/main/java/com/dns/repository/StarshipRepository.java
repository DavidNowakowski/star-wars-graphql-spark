package com.dns.repository;

import java.util.ArrayList;
import java.util.List;

import com.dns.entity.StarshipEntity;

public class StarshipRepository {

	private List<StarshipEntity> storedItems;

	public StarshipRepository() {
		mockData();
	}

	private void mockData() {
		storedItems = new ArrayList<>();
		storedItems.add(new StarshipEntity(1, "Milenarian Falcon", 100));
		storedItems.add(new StarshipEntity(2, "Death Star", 1000000));
	}

	public StarshipEntity findById(Integer id) {
		System.out.println("[StarshipRepository]: Trying to found starship with id: " + id);
		return storedItems.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

}
