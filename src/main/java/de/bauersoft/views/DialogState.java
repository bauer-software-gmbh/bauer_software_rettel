package de.bauersoft.views;

public enum DialogState {
	NEW("new"), EDIT("edit"), COPY("copy");

	private String value;

	DialogState(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return this.value;
	}
}
