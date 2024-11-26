package de.bauersoft.data.filters;

public enum NumericOperations {
	
	Equals("equals"), doesNotEqual("does not equal"), greaterThan("greater than"),
	greaterThanOrEqualTo("greater than or equal to"), lessThan("less than"),
	lessThanOrEqualTo("less than or equal to"), Between("between");

	private final String value;

	NumericOperations(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}