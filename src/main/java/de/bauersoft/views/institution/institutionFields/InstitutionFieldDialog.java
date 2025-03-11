package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
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
    private final InstitutionDialog institutionDialog;
    private final FieldDragComponent fieldDragComponent;
    private final InstitutionField institutionField;

    private final PatternMapContainer patternMapContainer;
    private final MultiplierMapContainer multiplierMapContainer;
    private final AllergenMapContainer allergenMapContainer;

    private final PatternComponent patternComponent;
    private final MultiplierComponent multiplierComponent;
    private final AllergenComponent allergenComponent;

    private final Button okButton;
    private final Button cancelButton;

    public InstitutionFieldDialog(InstitutionDialog institutionDialog, FieldDragComponent fieldDragComponent, InstitutionField institutionField, PatternMapContainer patternMapContainer, MultiplierMapContainer multiplierMapContainer, AllergenMapContainer allergenMapContainer)
    {
        this.institutionDialog = institutionDialog;
        this.fieldDragComponent = fieldDragComponent;
        this.institutionField = institutionField;
        this.patternMapContainer = patternMapContainer;
        this.multiplierMapContainer = multiplierMapContainer;
        this.allergenMapContainer = allergenMapContainer;

        setHeaderTitle(institutionField.getInstitution().getName() + " - " + institutionField.getField().getName());

        patternComponent = new PatternComponent(institutionDialog, this, patternMapContainer);
        patternComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        multiplierComponent = new MultiplierComponent(institutionDialog, this, multiplierMapContainer);
        multiplierComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        allergenComponent = new AllergenComponent(institutionDialog, this, allergenMapContainer);

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

        this.setWidth("50rem");
        this.setMaxWidth("65vw");
        this.setHeight("59.375rem");
        this.setMaxHeight("90vh");

        this.getFooter().add(okButton, cancelButton);
        this.add(patternComponent, multiplierComponent, allergenComponent);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
    }
}
