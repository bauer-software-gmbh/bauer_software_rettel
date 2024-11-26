package de.bauersoft.data.filters;

import com.vaadin.flow.function.SerializablePredicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public interface SerializableFilter<T,V> extends SerializablePredicate<T> {
	public String getFieldName();
	public void setValue(V value);
	public V getValue();
	public void clear();
	public Predicate getPredicate(CriteriaBuilder criteriaBuilder,Path<?> path);
	
}
