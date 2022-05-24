package com.dns.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.dns.entity.StarshipEntity;

public class StarshipRepository {

	private List<StarshipEntity> storedItems;

	private Integer count;

	public StarshipRepository() {
		mockData();
	}

	private void mockData() {
		storedItems = new ArrayList<>();
		storedItems.add(new StarshipEntity(1, "Milenarian Falcon", 100));
		storedItems.add(new StarshipEntity(2, "Death Star", 1000000));
		count = storedItems.size() + 1;
	}

	public List<StarshipEntity> findByAllByIds(List<Integer> ids) {
		System.out.println("[StarshipRepository]: Tring to found startships with ids: " + ids.toString());
		HashSet<Integer> hashIds = new HashSet<>(ids);
		List<StarshipEntity> starships = storedItems.stream()
				.filter(e -> hashIds.contains(e.getId()))
				.collect(Collectors.toList());
		if (ids.size() == 1 && starships.size() == 0) {
			throw new IndexOutOfBoundsException("Not found sharship for index: " + ids);
		} else if (ids.size() != starships.size()) {
			throw new IndexOutOfBoundsException("Unexpected error when fetching sharships for indexes: " + ids);
		}
		return starships;
	}

	public StarshipEntity findById(Integer id) {
		System.out.println("[StarshipRepository]: Trying to found starship with id: " + id);
		return storedItems.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public StarshipEntity save(StarshipEntity starshipEntity, Boolean replaceNull) {
		System.out.println("[StarshipRepository]: Saving starship, replaceNull: " + replaceNull.toString());
		StarshipEntity storedStarshipEntity = edit(starshipEntity, replaceNull);
		if (storedStarshipEntity == null) {
			storedStarshipEntity = add(starshipEntity);
		}
		return storedStarshipEntity;
	}

	private StarshipEntity add(StarshipEntity starshipEntity) {
		System.out.println("[StarshipRepository]: Creating new starship");
		starshipEntity.setId(count);
		storedItems.add(starshipEntity);
		count++;
		return starshipEntity;
	}

	private StarshipEntity edit(StarshipEntity starshipEntity, Boolean replaceNull) {
		StarshipEntity storedStarshipEntity = findById(starshipEntity.getId());
		if (storedStarshipEntity != null) {
			System.out.println("[StarshipRepository]: Starship found in DB, replacing values");
			if (replaceNull) {
				replaceData(storedStarshipEntity, starshipEntity);
			} else {
				replaceDataIfNotNull(storedStarshipEntity, storedStarshipEntity);
			}
		}
		return storedStarshipEntity;
	}

	private void replaceData(StarshipEntity storedStarshipEntity, StarshipEntity starshipEntity) {
		storedStarshipEntity.setName(starshipEntity.getName());
		storedStarshipEntity.setLenght(starshipEntity.getLenght());
	}

	private void replaceDataIfNotNull(StarshipEntity storedStarshipEntity, StarshipEntity starshipEntity) {
		replaceIfNotNull(starshipEntity::getName, storedStarshipEntity::setName);
		replaceIfNotNull(starshipEntity::getLenght, storedStarshipEntity::setLenght);
	}

	private <T> void replaceIfNotNull(Supplier<T> supplier, Consumer<T> consumer) {
		T value = supplier.get();
		if (value != null) {
			consumer.accept(value);
		}
	}

	public Boolean deleteById(Integer id) {
		System.out.println("[StarshipRepository]: Trying to delete starhip with id: " + id);
		return storedItems.removeIf(e -> e.getId().equals(id));
	}

	public void deleteAll() {
		System.out.println("[StarshipRepository]: Deleting all stored items");
		storedItems.clear();
	}
}
