package de.bauersoft.views.institution.institutionFields.dialogLayer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.dialogLayer.components.AllergenComponent;
import de.bauersoft.views.institution.institutionFields.FieldDragComponent;
import de.bauersoft.views.institution.institutionFields.dialogLayer.components.MultiplierComponent;

public class InstitutionFieldDialog extends Dialog
{
    private final InstitutionDialog institutionDialog;
    private final FieldDragComponent fieldDragComponent;
    private final InstitutionField institutionField;

    private final AllergenComponent allergenComponent;
    private final MultiplierComponent multiplierComponent;

    private final Button okButton;
    private final Button cancelButton;

    public InstitutionFieldDialog(InstitutionDialog institutionDialog, FieldDragComponent fieldDragComponent, InstitutionField institutionField)
    {
        this.institutionDialog = institutionDialog;
        this.fieldDragComponent = fieldDragComponent;
        this.institutionField = institutionField;

        setHeaderTitle(institutionField.getInstitution().getName() + " - " + institutionField.getField().getName());

        allergenComponent = new AllergenComponent(institutionDialog, this, institutionField);

        multiplierComponent = new MultiplierComponent(institutionDialog, this, institutionField);

        okButton = new Button("Ok");
        okButton.addClickListener(event ->
        {
            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            fieldDragComponent.getInstitutionFieldDialogMap().remove(institutionField.getField());
            this.close();
        });

        this.getFooter().add(okButton, cancelButton);
        this.add(multiplierComponent, allergenComponent);

        this.setWidth("50vw");
        this.setMaxWidth("50em");
        this.setMaxHeight("90vh");
        this.setHeight("90vh");

        this.setCloseOnOutsideClick(false);
        this.setModal(true);
    }
}
