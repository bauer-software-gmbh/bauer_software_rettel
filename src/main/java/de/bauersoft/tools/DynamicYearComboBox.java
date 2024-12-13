package de.bauersoft.tools;

import com.vaadin.flow.component.combobox.ComboBox;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DynamicYearComboBox extends ComboBox<Integer> {

    private List<Integer> currentYearList; // Speichert die aktuellen Jahre in der ComboBox

    public DynamicYearComboBox(String label) {
        super(label); // Aufruf des ComboBox-Konstruktors mit Label
        updateYearComboBox(); // Initialisiere die Jahre
    }

    // Methode zum Aktualisieren der ComboBox-Inhalte
    private void updateYearComboBox() {
        int currentYear = Year.now().getValue();
        // Liste der nächsten 4 Jahre erstellen
        currentYearList = IntStream.range(currentYear, currentYear + 4)
                .boxed()
                .collect(Collectors.toList());
        setItems(currentYearList); // Setze die Jahre in die ComboBox
        setValue(currentYear); // Standardwert: Aktuelles Jahr
    }

    // Methode zum Überprüfen und Aktualisieren der Jahre (z. B. regelmäßig aufrufen)
    public void checkAndUpdateYears() {
        int currentYear = Year.now().getValue();
        if (!currentYearList.contains(currentYear)) {
            updateYearComboBox(); // Aktualisiere die ComboBox, wenn ein Jahr verstrichen ist
        }
    }
}
