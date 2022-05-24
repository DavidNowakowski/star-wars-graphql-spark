package com.dns.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.dns.entity.CharacterEntity;
import com.dns.entity.CharacterKind;
import com.dns.entity.EpisodeEntity;

public class CharacterRepository {

	private List<CharacterEntity> storedItems;

	private Integer count;

	public CharacterRepository() {
		mockData();
	}

	private void mockData() {
		storedItems = new ArrayList<>();
		storedItems.add(new CharacterEntity(1, CharacterKind.ORGANIC, "Luke Skywalker", goodFriends(1), EpisodeEntity.getAll(), null, 100, null));
		storedItems.add(new CharacterEntity(2, CharacterKind.ORGANIC, "Leia Skywalker", goodFriends(2), EpisodeEntity.getAll(), null, Integer.MAX_VALUE, null));
		storedItems.add(new CharacterEntity(3, CharacterKind.ORGANIC, "Han Solo", friendsAlternative(3), EpisodeEntity.getAll(), 1, 1, null));
		storedItems.add(new CharacterEntity(4, CharacterKind.ORGANIC, "Chewbacca", goodFriends(4), EpisodeEntity.getAll(), 1, 2, null));
		storedItems.add(new CharacterEntity(5, CharacterKind.DROID, "R2-D2", goodFriends(5), EpisodeEntity.getAll(), null, null, "Hack & Repair"));
		storedItems.add(new CharacterEntity(6, CharacterKind.DROID, "C-3PO", goodFriends(6), EpisodeEntity.getAll(), null, null, "Translate"));
		storedItems.add(new CharacterEntity(7, CharacterKind.ORGANIC, "Darth Vader", evilFiends(7), EpisodeEntity.getAll(), 2, 666, null));
		storedItems.add(new CharacterEntity(8, CharacterKind.ORGANIC, "Palpatine", evilFiends(8), EpisodeEntity.getAll(), null, Integer.MAX_VALUE, null));
		count = storedItems.size() + 1;
	}

	private List<Integer> friendsAlternative(Integer id) {
		Integer positionToRemove = id - 1;
		List<Integer> goodFriends = goodFiends();
		goodFriends.remove(positionToRemove.intValue());
		goodFriends.remove(0);
		goodFriends.remove(goodFriends.size() - 1);
		return goodFriends;
	}

	private List<Integer> goodFriends(Integer id) {
		Integer positionToRemove = id - 1;
		List<Integer> goodFriends = goodFiends();
		goodFriends.remove(positionToRemove.intValue());
		return goodFriends;
	}

	private List<Integer> evilFiends(Integer id) {
		Integer positionToRemove = id - 7;
		List<Integer> evilFriends = evilFiends();
		evilFriends.remove(positionToRemove.intValue());
		return evilFriends;
	}

	private List<Integer> evilFiends() {
		List<Integer> evilFriends = new ArrayList<>();
		evilFriends.add(7);
		evilFriends.add(8);
		return evilFriends;
	}

	private List<Integer> goodFiends() {
		List<Integer> goodFriends = new ArrayList<>();
		goodFriends.add(1);
		goodFriends.add(2);
		goodFriends.add(3);
		goodFriends.add(4);
		goodFriends.add(5);
		goodFriends.add(6);
		return goodFriends;
	}

	public List<CharacterEntity> findAllByIds(List<Integer> ids) {
		System.out.println("[CharacterRepository]: Trying to found ids: " + ids.toString());
		HashSet<Integer> hashSetIds = new HashSet<>(ids);
		List<CharacterEntity> characters = storedItems.stream()
				.filter(e -> hashSetIds.contains(e.getId()))
				.collect(Collectors.toList());
		if(ids.size()==1 && characters.size()==0) {
			throw new IndexOutOfBoundsException("Not found character for index: "+ids);
		}else if(ids.size() != characters.size()) {
			throw new IndexOutOfBoundsException("Unexpected error when fetching characters for indexes: "+ids);
		}
		return characters;
	}

	public CharacterEntity findById(Integer id) {
		System.out.println("[CharacterRepository]: Tying to found: " + id);
		return storedItems.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public List<CharacterEntity> findAll() {
		System.out.println("[CharacterRepository]: Trying to found all characters");
		return storedItems;
	}

	public CharacterEntity save(CharacterEntity character, Boolean replaceNull) {
		System.out.println("[CharacterRepository]: Saving character, replaceNull: " + replaceNull.toString());
		CharacterEntity storedCharacted = edit(character, replaceNull);
		if (storedCharacted == null) {
			storedCharacted = add(character);
		}
		return storedCharacted;
	}

	private CharacterEntity add(CharacterEntity character) {
		System.out.println("[CharacterRepository]: Creating new character");
		character.setId(count);
		storedItems.add(character);
		count++;
		return character;
	}

	private CharacterEntity edit(CharacterEntity character, Boolean replaceNull) {
		Integer id = character.getId();
		CharacterEntity storedCharacted = findById(id);
		if (storedCharacted != null) {
			System.out.println("[CharacterRepository]: Character found in DB, replacing values");
			if (replaceNull) {
				replaceData(storedCharacted, character);
			} else {
				replaceDataIfNotNull(character, storedCharacted);
			}
		}
		return storedCharacted;
	}

	private void replaceData(CharacterEntity original, CharacterEntity newData) {
		original.setCharacterKind(newData.getCharacterKind());
		original.setName(newData.getName());
		original.setAppearsIn(newData.getAppearsIn());

		original.setFriendsIds(newData.getFriendsIds());
		original.setStarshipId(newData.getStarshipId());

		original.setPrimaryFunction(newData.getPrimaryFunction());
		original.setTotalCredits(newData.getTotalCredits());
	}

	private void replaceDataIfNotNull(CharacterEntity original, CharacterEntity newData) {
		replaceIfNotNull(newData::getCharacterKind, original::setCharacterKind);
		replaceIfNotNull(newData::getName, original::setName);
		replaceIfNotNull(newData::getAppearsIn, original::setAppearsIn);

		replaceIfNotNull(newData::getFriendsIds, original::setFriendsIds);
		replaceIfNotNull(newData::getStarshipId, original::setStarshipId);

		replaceIfNotNull(newData::getPrimaryFunction, original::setPrimaryFunction);
		replaceIfNotNull(newData::getTotalCredits, original::setTotalCredits);
	}

	private <T> void replaceIfNotNull(Supplier<T> supplier, Consumer<T> consumer) {
		T value = supplier.get();
		if (value != null) {
			consumer.accept(value);
		}
	}

	public Boolean deleteById(Integer id) {
		System.out.println("[CharacterRepository]: Tring to delete character with id: " + id);
		return storedItems.removeIf(e -> e.getId().equals(id));
	}

	public void deleteFriendRelations(Integer id) {
		System.out.println("[CharacterRepository]: Deleting friends relations for id: " + id);
		storedItems.forEach((e) ->{ 
			List<Integer> friendsIds=e.getFriendsIds();
			if(friendsIds!=null) {
				friendsIds.removeIf(friendId -> friendId.equals(id));
			}});
	}

	public void deleteAll() {
		System.out.println("[CharacterRepository]: Deleting all stored items");
		storedItems.clear();
	}

	public void deleteStarshipRelations(Integer id) {
		for (CharacterEntity characterEntity : storedItems) {
			if (id.equals(characterEntity.getStarshipId())) {
				characterEntity.setStarshipId(null);

			}
		}
	}

	public void deleteAllStartshipRelations() {
		for (CharacterEntity characterEntity : storedItems) {
			characterEntity.setStarshipId(null);
		}
	}

}
