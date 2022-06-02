package com.dns.repository;

import java.util.ArrayList;
import java.util.List;

import com.dns.entity.CharacterEntity;
import com.dns.entity.EpisodeEntity;

public class CharacterRepository {

	private List<CharacterEntity> storedItems;

	public CharacterRepository() {
		mockData();
	}

	private void mockData() {
		storedItems = new ArrayList<>();
		storedItems.add(new CharacterEntity(1, "Luke Skywalker", EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(2, "Leia Skywalker", EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(3, "Han Solo", EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(4, "Chewbacca", EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(5, "R2-D2", EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(6, "C-3PO",EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(7, "Darth Vader",EpisodeEntity.getAll()));
		storedItems.add(new CharacterEntity(8, "Palpatine", EpisodeEntity.getAll()));
	}

	public CharacterEntity findById(Integer id) {
		System.out.println("[CharacterRepository]: Tying to found: " + id);
		return storedItems.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

}
