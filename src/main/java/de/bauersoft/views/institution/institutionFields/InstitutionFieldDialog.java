package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.services.AllergenService;
import de.bauersoft.services.CourseService;
import de.bauersoft.services.FieldMultiplierService;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenComponent;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenMapContainer;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierComponent;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierMapContainer;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternComponent;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternMapContainer;
import lombok.Getter;

@Getter
public class InstitutionFieldDialog extends Dialog
{
    private final PatternService patternService;
    private final FieldMultiplierService fieldMultiplierService;
    private final CourseService courseService;
    private final AllergenService allergenService;

    private final InstitutionField item;

    private final PatternMapContainer patternMapContainer;
    private final MultiplierMapContainer multiplierMapContainer;
    private final AllergenMapContainer allergenMapContainer;

    private final PatternComponent patternComponent;
    private final MultiplierComponent multiplierComponent;
    private final AllergenComponent allergenComponent;

    private final Button okButton;
    private final Button cancelButton;

    public InstitutionFieldDialog(PatternService patternService, FieldMultiplierService fieldMultiplierService, CourseService courseService, AllergenService allergenService, InstitutionField item, PatternMapContainer patternMapContainer, MultiplierMapContainer multiplierMapContainer, AllergenMapContainer allergenMapContainer)
    {
        this.patternService = patternService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.courseService = courseService;
        this.allergenService = allergenService;
        this.item = item;
        this.patternMapContainer = patternMapContainer;
        this.multiplierMapContainer = multiplierMapContainer;
        this.allergenMapContainer = allergenMapContainer;

        setHeaderTitle("Prognosen für " + item.getInstitution().getName() + " - " + item.getField().getName());

        patternComponent = new PatternComponent(patternService, item, patternMapContainer);
        patternComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        multiplierComponent = new MultiplierComponent(fieldMultiplierService, courseService, item, multiplierMapContainer);
        multiplierComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        allergenComponent = new AllergenComponent(allergenService, item, allergenMapContainer);

        okButton = new Button("Ok");
        okButton.setMinWidth("150px");
        okButton.setMaxWidth("180px");
        okButton.addClickListener(event ->
        {
            if(!patternComponent.isValid())
                return;

            patternMapContainer.acceptTemporaries();
            multiplierMapContainer.acceptTemporaries();
            allergenMapContainer.acceptTemporaries();;
            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addClickListener(event ->
        {
            patternMapContainer.loadTemporaries();
            multiplierMapContainer.loadTemporaries();
            allergenMapContainer.loadTemporaries();
            this.close();
        });

        this.setWidth("40rem");
        this.setHeight("55rem");

        this.getFooter().add(okButton, cancelButton);
        this.add(patternComponent, multiplierComponent, allergenComponent);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}
