package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.institution.InstitutionField;
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

    private final PatternMapContainer patternListContainer;
    private final MultiplierMapContainer multiplierListContainer;
    private final AllergenMapContainer allergenListContainer;

    private final PatternComponent patternComponent;
    private final MultiplierComponent multiplierComponent;
    private final AllergenComponent allergenComponent;

    private final Button okButton;
    private final Button cancelButton;

    public InstitutionFieldDialog(InstitutionDialog institutionDialog, FieldDragComponent fieldDragComponent, InstitutionField institutionField, PatternMapContainer patternListContainer, MultiplierMapContainer multiplierListContainer, AllergenMapContainer allergenListContainer)
    {
        this.institutionDialog = institutionDialog;
        this.fieldDragComponent = fieldDragComponent;
        this.institutionField = institutionField;
        this.patternListContainer = patternListContainer;
        this.multiplierListContainer = multiplierListContainer;
        this.allergenListContainer = allergenListContainer;

        setHeaderTitle(institutionField.getInstitution().getName() + " - " + institutionField.getField().getName());

        patternComponent = new PatternComponent(institutionDialog, this, patternListContainer);
        patternComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        multiplierComponent = new MultiplierComponent(institutionDialog, this, multiplierListContainer);
        multiplierComponent.getStyle()
                .setMarginBottom("var(--lumo-space-s)");

        allergenComponent = new AllergenComponent(institutionDialog, this, allergenListContainer);

        okButton = new Button("Ok");
        okButton.setMinWidth("150px");
        okButton.setMaxWidth("180px");
        okButton.addClickListener(event ->
        {
            patternListContainer.acceptTemporaries();
            multiplierListContainer.acceptTemporaries();
            allergenListContainer.acceptTemporaries();
            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addClickListener(event ->
        {
            patternListContainer.loadTemporaries();
            multiplierListContainer.loadTemporaries();
            allergenListContainer.loadTemporaries();
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
