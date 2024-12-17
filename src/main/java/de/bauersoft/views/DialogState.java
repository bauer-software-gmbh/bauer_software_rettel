package de.bauersoft.views;

public enum DialogState
{
	NEW("Neu anlegen"),
	EDIT("Editieren"),
	COPY("Kopieren"),
	VIEW("Anschauen");

	private String value;

	DialogState(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return this.value;
	}
}
