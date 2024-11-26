package de.bauersoft.data.filters;

import java.lang.reflect.Field;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class TextFilter<T> extends AbstractSerializableFilter<T, String>{

	public enum MatchType {STARTS_WITH,ENDS_WITH,CONTAINS,EXACT_MATCH}

	private MatchType matchType;
	
	public TextFilter(Class<T> beanType, String fieldName) {
		this(beanType, fieldName,MatchType.STARTS_WITH);
	}
	
	public TextFilter(Class<T> beanType, String fieldName,MatchType matchType) {
		super(beanType, fieldName);
		this.matchType= matchType;
	}

	@Override
	public Predicate getPredicate(CriteriaBuilder criteriaBuilder,Path<?> externalPath) {
		@SuppressWarnings("unchecked")
		Path<String> path = (Path<String>) externalPath ; // user.get(getFieldName());
		if(getValue() == null) return null;
		switch(matchType) {
			case EXACT_MATCH:
				return criteriaBuilder.like(path, getValue());
			case ENDS_WITH:
				return criteriaBuilder.like(path, "%"+getValue());
			case CONTAINS:
				return criteriaBuilder.like(path, "%"+getValue()+"%");
			case STARTS_WITH:	
			default:
				return criteriaBuilder.like(path, getValue()+"%");
		}
	}

	@Override
	public boolean test(T t) {
		String fieldValue = null;
		try {
			Field field = t.getClass().getDeclaredField(getFieldName());
			field.setAccessible(true);
			fieldValue = (String)field.get(t);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			try {
			Field field =	t.getClass().getSuperclass().getDeclaredField(getFieldName());
			field.setAccessible(true);
			fieldValue = (String)field.get(t);
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e2) {}
		}
		switch(matchType) {
			case EXACT_MATCH:
				return fieldValue == null || getValue() == null || fieldValue.equals(getValue());
			case ENDS_WITH:
				return fieldValue == null || getValue() == null || fieldValue.endsWith(getValue());
			case CONTAINS:
				return fieldValue == null || getValue() == null || fieldValue.contains(getValue());
			case STARTS_WITH:	
			default:
				return fieldValue == null || getValue() == null || fieldValue.startsWith(getValue());
		}
	}

}
