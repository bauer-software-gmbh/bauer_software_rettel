package de.bauersoft.views;

import java.util.Objects;

public enum DialogState
{
	NEW("Neu anlegen"),
	EDIT("Editieren"),
	COPY("Kopieren"),
	VIEW("Anschauen"),
	CUSTOM("Custom");

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

	public String titleCreator(String onNew, String onEdit)
	{
		return titleCreator(onNew, onEdit, null, null, null, "N/A");
	}

	public String titleCreator(String onNew, String onEdit, String def)
	{
		return titleCreator(onNew, onEdit, null, null, null, def);
	}

	public String titleCreator(String onNew, String onEdit, String onCopy, String onView, String onCustom)
	{
		return titleCreator(onNew, onEdit, onCopy, onView, onCustom, "N/A");
	}

	public String titleCreator(String onNew, String onEdit, String onCopy, String onView, String onCustom, String def)
	{
		switch(this)
		{
			case NEW:
				return Objects.requireNonNullElse(onNew, def);
			case EDIT:
				return Objects.requireNonNullElse(onEdit, def);
			case COPY:
				return Objects.requireNonNullElse(onCopy, def);
			case VIEW:
				return Objects.requireNonNullElse(onView, def);
			case CUSTOM:
				return Objects.requireNonNullElse(onCustom, def);
		}

		return "";
	}
}
