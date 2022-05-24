package com.dns.entity;

import java.util.ArrayList;
import java.util.List;

public enum EpisodeEntity {

	A_NEW_HOPE,

	THE_EMPIRE_STRIKES_BACK,

	RETURN_OF_THE_JEDI;

	public static final List<EpisodeEntity> getAll() {
		ArrayList<EpisodeEntity> allEpisodes = new ArrayList<>();
		allEpisodes.add(A_NEW_HOPE);
		allEpisodes.add(RETURN_OF_THE_JEDI);
		allEpisodes.add(THE_EMPIRE_STRIKES_BACK);
		return allEpisodes;
	}
}
