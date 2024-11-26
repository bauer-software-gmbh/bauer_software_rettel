package de.bauersoft.data.repositories.griddata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.bauersoft.data.filters.SerializableFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public abstract class AbstractGridDataRepository<T> implements GridDataRepository<T> {
	@PersistenceContext
	private EntityManager entityManager;
	private Class<T> beanType;

	public AbstractGridDataRepository(Class<T> beanType) {
		this.beanType = beanType;
	}

	@Override
	public long count(List<SerializableFilter<T, ?>> filters) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = null;
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList = filters.stream()
					.filter(filter -> filter.getPredicate(cb, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(cb, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList());
		} else {
			predicateList = new ArrayList<Predicate>();
		}
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		if (predicates.length > 0) {
			query.select(cb.count(from)).where(cb.and(predicates));
		} else {
			query.select(cb.count(from));
		}
		return entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public List<T> fetchAll(List<SerializableFilter<T, ?>> filters, List<QuerySortOrder> sortOrderList) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<T> query = criteriaBuilder.createQuery(beanType);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = null;
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList = filters.stream()
					.filter(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList());
		} else {
			predicateList = new ArrayList<Predicate>();
		}
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		List<Order> orderList = new ArrayList<>();
		for (QuerySortOrder querySortOrder : sortOrderList) {
			orderList.add(SortDirection.ASCENDING.equals(querySortOrder.getDirection())
					? criteriaBuilder.asc(getPathObject(from, querySortOrder.getSorted()))
					: criteriaBuilder.desc(getPathObject(from, querySortOrder.getSorted())));
		}
		if (predicates.length > 0) {
			query.select(from).where(criteriaBuilder.and(predicates)).orderBy(orderList);
		} else {
			query.select(from).orderBy(orderList);
		}
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public long count(List<SerializableFilter<T, ?>> filters, List<String> parentKeys, List<Object> parentValues) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList.addAll(filters.stream()
					.filter(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList()));
		}
		predicateList.addAll(parentKeys.stream().map(parentKey -> {
			Expression<Object> expression = null;
			if (parentKey.contains(".")) {
				String[] parts = parentKey.split("[.]");
				expression = from.get(parts[0]);
				for (int index = 1; index < parts.length; index++) {
					expression = ((Path<Object>) expression).get(parts[index]);
				}
			} else {
				expression = from.get(parentKey);
			}
			return criteriaBuilder.equal(expression, parentValues.get(parentKeys.indexOf(parentKey)));
		}).toList());
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		if (predicates.length > 0) {
			query.select(criteriaBuilder.countDistinct(from)).where(criteriaBuilder.and(predicates));
		} else {
			query.select(criteriaBuilder.countDistinct(from));
		}
		return entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public long count(List<SerializableFilter<T, ?>> filters, List<String> parentKeys, List<Object> parentValues,
			String groupBy) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList.addAll(filters.stream()
					.filter(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList()));
		}
		predicateList.addAll(parentKeys.stream().map(parentKey -> criteriaBuilder.equal(from.get(parentKey),
				parentValues.get(parentKeys.indexOf(parentKey)))).toList());
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		Expression<Object> expression = null;
		if (groupBy.contains(".")) {
			String[] parts = groupBy.split("[.]");
			expression = from.get(parts[0]);
			for (int index = 1; index < parts.length; index++) {
				expression = ((Path<Object>) expression).get(parts[index]);
			}
		} else {
			expression = from.get(groupBy);
		}
		if (predicates.length > 0) {
			query.select(criteriaBuilder.countDistinct(expression)).where(criteriaBuilder.and(predicates));
		} else {
			query.select(criteriaBuilder.countDistinct(expression));
		}
		return entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public List<T> fetchAll(List<SerializableFilter<T, ?>> filters, List<QuerySortOrder> sortOrderList,
			List<String> parentKeys, List<Object> parentValues) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<T> query = criteriaBuilder.createQuery(beanType);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList.addAll(filters.stream()
					.filter(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList()));
		}
		predicateList.addAll(parentKeys.stream().map(parentKey -> {
			Expression<Object> expression = null;
			if (parentKey.contains(".")) {
				String[] parts = parentKey.split("[.]");
				expression = from.get(parts[0]);
				for (int index = 1; index < parts.length; index++) {
					expression = ((Path<Object>) expression).get(parts[index]);
				}
			} else {
				expression = from.get(parentKey);
			}
			return criteriaBuilder.equal(expression, parentValues.get(parentKeys.indexOf(parentKey)));
		}).toList());
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		List<Order> orderList = new ArrayList<>();
		for (QuerySortOrder querySortOrder : sortOrderList) {
			orderList.add(SortDirection.ASCENDING.equals(querySortOrder.getDirection())
					? criteriaBuilder.asc(getPathObject(from, querySortOrder.getSorted()))
					: criteriaBuilder.desc(getPathObject(from, querySortOrder.getSorted())));
		}
		if (predicates.length > 0) {
			query.select(from).distinct(true).where(criteriaBuilder.and(predicates)).orderBy(orderList);
		} else {
			query.select(from).distinct(true).orderBy(orderList);
		}
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<Object> fetchAll(List<SerializableFilter<T, ?>> filters, List<QuerySortOrder> sortOrderList,
			List<String> parentKeys, List<Object> parentValues, String groupBy) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Map<String, Path<?>> pathMap = new HashMap<>();
		CriteriaQuery<Object> query = criteriaBuilder.createQuery(Object.class);
		Root<T> from = query.from(beanType);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		if (filters != null) {
			for (String name : filters.stream().map(filter -> filter.getFieldName()).collect(Collectors.toSet())) {
				pathMap.put(name, from.get(name));
			}
			predicateList.addAll(filters.stream()
					.filter(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())) != null)
					.map(filter -> filter.getPredicate(criteriaBuilder, pathMap.get(filter.getFieldName())))
					.collect(Collectors.toList()));
		}
		predicateList.addAll(parentKeys.stream().map(parentKey -> criteriaBuilder.equal(from.get(parentKey),
				parentValues.get(parentKeys.indexOf(parentKey)))).toList());
		Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
		List<Order> orderList = new ArrayList<>();
		for (QuerySortOrder querySortOrder : sortOrderList) {
			orderList.add(SortDirection.ASCENDING.equals(querySortOrder.getDirection())
					? criteriaBuilder.asc(getPathObject(from, querySortOrder.getSorted()))
					: criteriaBuilder.desc(getPathObject(from, querySortOrder.getSorted())));
		}
		Selection<Object> selection = null;
		if (groupBy.contains(".")) {
			String[] parts = groupBy.split("[.]");
			selection = from.get(parts[0]);
			for (int index = 1; index < parts.length; index++) {
				selection = ((Path<Object>) selection).get(parts[index]);
			}
		} else {
			selection = from.get(groupBy);
		}
		if (predicates.length > 0) {
			query.select(selection).distinct(true).where(criteriaBuilder.and(predicates)).orderBy(orderList);
		} else {
			query.select(selection).distinct(true).orderBy(orderList);
		}
		return entityManager.createQuery(query).getResultList();
	}

	private Path<Object> getPathObject(Root<T> from, String path) {
		Path<Object> returnValue = null;
		if (path.contains(".")) {
			String[] parts = path.split("[.]");
			returnValue = from.get(parts[0]);
			for (int i = 1; i < parts.length && parts.length != 1; i++) {
				returnValue = returnValue.get(parts[i]);
			}
		} else {
			returnValue = from.get(path);
		}
		return returnValue;
	}
}