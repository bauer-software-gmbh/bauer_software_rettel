package de.bauersoft.views.institution.institutionFields.components.pattern;

import de.bauersoft.data.entities.pattern.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatternContainer
{
    private Pattern pattern;
    private int amount;
}
