package com.dns;

public class GraphQLHttpPostInput {

	private String query;

	public GraphQLHttpPostInput() {
	}

	public GraphQLHttpPostInput(String query) {
		super();
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
