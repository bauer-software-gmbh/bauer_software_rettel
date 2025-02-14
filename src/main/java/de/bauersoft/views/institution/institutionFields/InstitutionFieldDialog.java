package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.components.InstitutionFieldContainer;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenComponent;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierComponent;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternComponent;
import lombok.Getter;

@Getter
public class InstitutionFieldDialog extends Dialog
{
    private final InstitutionDialog institutionDialog;
    private final FieldDragComponent fieldDragComponent;
    private final InstitutionField institutionField;

    private final InstitutionFieldContainer institutionFieldContainer;

    private final PatternComponent patternComponent;
    private final MultiplierComponent multiplierComponent;
    private final AllergenComponent allergenComponent;

    private final Button okButton;
    private final Button cancelButton;

    public InstitutionFieldDialog(InstitutionDialog institutionDialog, FieldDragComponent fieldDragComponent, InstitutionFieldContainer institutionFieldContainer)
    {
        this.institutionDialog = institutionDialog;
        this.fieldDragComponent = fieldDragComponent;
        this.institutionField = institutionFieldContainer.getInstitutionField();
        this.institutionFieldContainer = institutionFieldContainer;

        setHeaderTitle(institutionField.getInstitution().getName() + " - " + institutionField.getField().getName());

        patternComponent = new PatternComponent(institutionDialog, this, institutionFieldContainer);
        patternComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        multiplierComponent = new MultiplierComponent(institutionDialog, this, institutionFieldContainer);
        multiplierComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        allergenComponent = new AllergenComponent(institutionDialog, this, institutionFieldContainer);

        okButton = new Button("Ok");
        okButton.setMinWidth("150px");
        okButton.setMaxWidth("180px");
        okButton.addClickListener(event ->
        {
            patternComponent.saveToContainer();
            multiplierComponent.saveToContainer();
            allergenComponent.saveToContainer();
            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addClickListener(event ->
        {
//            fieldDragComponent.getInstitutionFieldContanerMap().remove(institutionField.getField());
            this.close();
        });

        this.getFooter().add(okButton, cancelButton);
        this.add(patternComponent, multiplierComponent, allergenComponent);

        this.setWidth("50vw");
        this.setMaxWidth("50em");
        this.setMaxHeight("90vh");
        this.setHeight("90vh");

        this.setCloseOnOutsideClick(false);
        this.setModal(true);
    }
}
