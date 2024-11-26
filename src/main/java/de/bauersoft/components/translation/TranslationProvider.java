package de.bauersoft.components.translation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.vaadin.flow.i18n.I18NProvider;

@Service
public class TranslationProvider implements I18NProvider {


	@Override
	public List<Locale> getProvidedLocales() {
		return Arrays.asList(new Locale[]{Locale.GERMAN,Locale.ENGLISH,Locale.FRENCH});
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		
		
		
		return key;
	}
}
