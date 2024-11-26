package de.bauersoft.data.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Transient;

public abstract class AbstractGroupByEntity<T> extends AbstractEntity {
	@Transient
	private String key;
	@Transient
	private Object value;
	@Transient
	private List<String> parentKeys = new ArrayList<String>();
	@Transient
	private List<Object> parentValues = new ArrayList<Object>();
	@Transient
	private List<T> children;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		this.parentKeys.add(key);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		parentValues.add(value);
	}
	
	public List<String> getParentKeys() {
		return parentKeys;
	}

	public void setParentKeys(List<String> parentKeys) {
		this.parentKeys.addAll(parentKeys);
	}

	public List<Object> getParentValues() {
		return parentValues;
	}

	public void setParentValues(List<Object> parentValues) {
		this.parentValues.addAll(parentValues);
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}
}
