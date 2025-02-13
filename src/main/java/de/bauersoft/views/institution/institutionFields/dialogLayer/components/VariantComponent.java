package de.bauersoft.views.institution.institutionFields.dialogLayer.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.dialogLayer.InstitutionFieldDialog;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class VariantComponent extends HorizontalLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final HashMap<Pattern, VariantBox> variantBoxes;

    public VariantComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, InstitutionField institutionField)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionField;

        variantBoxes = new HashMap<>();
    }

    public class VariantBox extends Div
    {
        private final InstitutionDialog institutionDialog;
        private final InstitutionFieldDialog institutionFieldDialog;
        private final InstitutionField institutionField;
        private final Pattern pattern;

        private final Div nameDiv;
        private final NumberField amountField;

        private final Binder<NumberField> amountBinder;

        public VariantBox(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, InstitutionField institutionField, Pattern pattern)
        {
            this.institutionDialog = institutionDialog;
            this.institutionFieldDialog = institutionFieldDialog;
            this.institutionField = institutionField;
            this.pattern = pattern;

            nameDiv = new Div();
            nameDiv.setText(pattern.getName());
            nameDiv.getElement().setAttribute("tabindex", "-1");
            nameDiv.getStyle()
                    .set("margin", "2px")
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("background-color", "var(--lumo-base-color)")
                    .set("height", "var(--lumo-text-field-size)")
                    .set("box-shadow", "var(--lumo-box-shadow-xs)")
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("line-height", "var(--lumo-line-height-m)")
                    .set("color", "var(--lumo-body-text-color)")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("padding-left", "var(--lumo-space-m)")
                    .set("padding-right", "var(--lumo-space-m)")
                    .set("justify-content", "center")
                    .set("overflow-x", "clip");

            amountField = new NumberField();
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);
            amountField.setValue(0.0);
            amountField.setWidthFull();

            amountBinder = new Binder<>();
            amountBinder.forField(amountField)
                    .asRequired()
                    .bind(NumberField::getValue, NumberField::setValue);

            this.add(nameDiv, amountField);
            this.setWidth("calc(100em/3)");
            this.getStyle()
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("transform", "scale(1)");
        }
    }

}
