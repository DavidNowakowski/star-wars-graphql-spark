package com.dns;

import graphql.analysis.FieldComplexityCalculator;
import graphql.analysis.FieldComplexityEnvironment;
import graphql.language.Field;

public class StarWarsFieldComplexityCalculator implements FieldComplexityCalculator {

	@Override
	public int calculate(FieldComplexityEnvironment environment, int childComplexity) {
		Field field=environment.getField();
		String fieldName=field.getName();
		int newComplexity = 0;
		
		if("friends".equals(fieldName)) {
			newComplexity = childComplexity * 10;
		}else {
			newComplexity = childComplexity + 1;
		}
		return newComplexity;

	}

}
