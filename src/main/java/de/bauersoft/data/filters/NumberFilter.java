package de.bauersoft.data.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.lang.reflect.Field;

public class NumberFilter<T, V extends Comparable<V>> extends AbstractSerializableTwoValueFilter<T, V> {
	private NumericOperations matchType;

	public NumericOperations getMatchType() {
		return matchType;
	}

	public void setMatchType(NumericOperations matchType) {
		this.matchType = matchType;
	}

	public NumberFilter(Class<T> beanType, String fieldName) {
		this(beanType, fieldName, NumericOperations.Equals);
	}

	public NumberFilter(Class<T> beanType, String fieldName, NumericOperations matchType) {
		super(beanType, fieldName);
		this.matchType = matchType;
	}

	@Override
	public Predicate getPredicate(CriteriaBuilder criteriaBuilder, Path<?> externalPath) {
		if (getFirstValue() == null)
			return null;
		if (getFirstValue() instanceof Integer integerValue) {
			@SuppressWarnings("unchecked")
			Path<Integer> path = (Path<Integer>) externalPath;
			switch (matchType) {
			case Equals:
				return criteriaBuilder.equal(path, integerValue);
			case doesNotEqual:
				return criteriaBuilder.notEqual(path, integerValue);
			case greaterThan:
				return criteriaBuilder.greaterThan(path, integerValue);
			case greaterThanOrEqualTo:
				return criteriaBuilder.greaterThanOrEqualTo(path, integerValue);
			case lessThan:
				return criteriaBuilder.lessThan(path, integerValue);
			case lessThanOrEqualTo:
				return criteriaBuilder.lessThanOrEqualTo(path, integerValue);
			case Between:
				return getSecondValue() == null ? null
						: criteriaBuilder.between(path, integerValue, (Integer) getSecondValue());
			default:
				break;
			}
		}
		if (getValue() instanceof Long longValue) {
			@SuppressWarnings("unchecked")
			Path<Long> path = (Path<Long>) externalPath;
			switch (matchType) {
			case Equals:
				return criteriaBuilder.equal(path, longValue);
			case doesNotEqual:
				return criteriaBuilder.notEqual(path, longValue);
			case greaterThan:
				return criteriaBuilder.greaterThan(path, longValue);
			case greaterThanOrEqualTo:
				return criteriaBuilder.greaterThanOrEqualTo(path, longValue);
			case lessThan:
				return criteriaBuilder.lessThan(path, longValue);
			case lessThanOrEqualTo:
				return criteriaBuilder.lessThanOrEqualTo(path, longValue);
			case Between:
				return getSecondValue() == null ? null
						: criteriaBuilder.between(path, longValue, (Long) getSecondValue());
			default:
				break;
			}
		}
		if (getValue() instanceof Float floatValue) {
			@SuppressWarnings("unchecked")
			Path<Float> path = (Path<Float>) externalPath;
			switch (matchType) {
			case Equals:
				return criteriaBuilder.equal(path, floatValue);
			case doesNotEqual:
				return criteriaBuilder.notEqual(path, floatValue);
			case greaterThan:
				return criteriaBuilder.greaterThan(path, floatValue);
			case greaterThanOrEqualTo:
				return criteriaBuilder.greaterThanOrEqualTo(path, floatValue);
			case lessThan:
				return criteriaBuilder.lessThan(path, floatValue);
			case lessThanOrEqualTo:
				return criteriaBuilder.lessThanOrEqualTo(path, floatValue);
			case Between:
				return getSecondValue() == null ? null
						: criteriaBuilder.between(path, floatValue, (Float) getSecondValue());
			default:
				break;
			}
		}
		if (getValue() instanceof Double doubleValue) {
			@SuppressWarnings("unchecked")
			Path<Double> path = (Path<Double>) externalPath;
			switch (matchType) {
			case Equals:
				return criteriaBuilder.equal(path, doubleValue);
			case doesNotEqual:
				return criteriaBuilder.notEqual(path, doubleValue);
			case greaterThan:
				return criteriaBuilder.greaterThan(path, doubleValue);
			case greaterThanOrEqualTo:
				return criteriaBuilder.greaterThanOrEqualTo(path, doubleValue);
			case lessThan:
				return criteriaBuilder.lessThan(path, doubleValue);
			case lessThanOrEqualTo:
				return criteriaBuilder.lessThanOrEqualTo(path, doubleValue);
			case Between:
				return getSecondValue() == null ? null
						: criteriaBuilder.between(path, doubleValue, (Double) getSecondValue());
			default:
				break;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean test(T t) {
		V fieldValue = null;
		try {
			Field field = t.getClass().getDeclaredField(getFieldName());
			field.setAccessible(true);
			fieldValue = (V) field.get(t);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (getFirstValue() == null)
			return true;
		switch (matchType) {
		case Equals:
			return getFirstValue().compareTo(fieldValue) == 0;
		case doesNotEqual:
			return getFirstValue().compareTo(fieldValue) != 0;
		case greaterThan:
			return getFirstValue().compareTo(fieldValue) == 1;
		case lessThan:
			return getFirstValue().compareTo(fieldValue) == -1;
		case lessThanOrEqualTo:
			return getFirstValue().compareTo(fieldValue) <= 0;
		case greaterThanOrEqualTo:
			return getFirstValue().compareTo(fieldValue) >= 0;
		case Between:
			return getFirstValue().compareTo(fieldValue) <= 0 && getSecondValue().compareTo(fieldValue) >= 0;
		}
		return true;
	}
}
