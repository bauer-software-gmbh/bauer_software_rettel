package de.bauersoft.data.filters;

public abstract class AbstractSerializableTwoValueFilter<T,V> extends AbstractSerializableFilter<T, V>{

	private V secondValue;
	
	public AbstractSerializableTwoValueFilter(Class<T> beanType, String fieldName) {
		super(beanType, fieldName);
	}

	public void setFirstValue(V value) {
		super.setValue(value);
	}
	
	public V getFirstValue() {
		return super.getValue();
	}
	
	public void setSecondValue(V value) {
		this.secondValue = value;
	}
	
	public V getSecondValue() {
		return this.secondValue;
	}
	public void clear() {
		super.clear();
		this.secondValue = null;
	}
}
