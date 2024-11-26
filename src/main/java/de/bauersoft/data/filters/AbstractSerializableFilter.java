package de.bauersoft.data.filters;

import java.util.Objects;

public abstract class AbstractSerializableFilter<T,V> implements SerializableFilter<T, V> {

	private final Class<T> beanType;
	private final String fieldName;
	private V value;
	public AbstractSerializableFilter(Class<T> beanType,String fieldName) {
		Objects.requireNonNull(beanType, "The beanType can not be null !");
		Objects.requireNonNull(fieldName, "The fieldName can not be null !");
		if(fieldName.isBlank()) throw new IllegalArgumentException("The fieldName could not be blank!");
		try {
			beanType.getDeclaredField(fieldName); 
			
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				beanType.getSuperclass().getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e2) {
				throw new IllegalArgumentException("Could not find the field with fieldName :"+fieldName+"!");
			}
		}
		this.beanType=beanType;
		this.fieldName=fieldName;
	}

	public Class<T> getBeanType() {
		return beanType;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	@Override
	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public V getValue() {
		return this.value;
	}
	public void clear() {
		this.value = null;
	}
}
