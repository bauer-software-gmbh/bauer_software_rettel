package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.allergen.Allergen;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class AllergenDTO {
    private List<Allergen> allergens = new ArrayList<>();
    private String name;
    private String description;

    public AllergenDTO(Allergen allergen) {
        this.name = allergen.getName();
        this.description = allergen.getDescription();
        this.allergens.add(allergen);
    }

    public AllergenDTO(Collection<Allergen> allergenList) {
        this.allergens.addAll(allergenList);
    }

    public AllergenDTO(Map<String, Long> allergenMap) {
        this.name = null;
        this.description = null;
        this.allergens = allergenMap.entrySet().stream()
                .flatMap(entry -> {
                    Allergen mock = new Allergen();
                    mock.setName(entry.getKey());
                    return Collections.nCopies(entry.getValue().intValue(), mock).stream();
                })
                .collect(Collectors.toList());
    }

    public String getAllergeneText() {
        if (allergens == null || allergens.isEmpty()) return "";

        Map<String, Long> grouped = allergens.stream()
                .collect(Collectors.groupingBy(Allergen::getName, TreeMap::new, Collectors.counting()));

        return grouped.entrySet().stream()
                .map(entry -> entry.getValue() + "x " + entry.getKey())
                .collect(Collectors.joining(", "));
    }
}
