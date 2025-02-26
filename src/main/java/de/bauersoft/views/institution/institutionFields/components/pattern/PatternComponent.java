package de.bauersoft.views.institution.institutionFields.components.pattern;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPatternKey;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class PatternComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final PatternMapContainer patternMapContainer;

    private final Map<Pattern, PatternBox> patternBoxMap;

    public PatternComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, PatternMapContainer patternMapContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionFieldDialog.getInstitutionField();
        this.patternMapContainer = patternMapContainer;

        patternBoxMap = new HashMap<>();

        for(Pattern pattern : institutionDialog.getPatternService().findAll())
        {
            PatternContainer patternContainer = (PatternContainer) patternMapContainer.addIfAbsent(pattern, () ->
            {
                InstitutionPattern institutionPattern = new InstitutionPattern();
                institutionPattern.setId(new InstitutionPatternKey(null, pattern.getId()));
                institutionPattern.setInstitutionField(institutionField);
                institutionPattern.setPattern(pattern);

                return institutionPattern;
            }, ContainerState.SHOW);

            PatternBox patternBox = new PatternBox(patternContainer);

            patternBoxMap.put(pattern, patternBox);
            this.add(patternBox);
        }

        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setDisplay(Style.Display.FLEX)
                .set("gap", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    public boolean isValid()
    {
        boolean allValid = true;
        for(PatternBox patternBox : patternBoxMap.values())
        {
            if(!patternBox.isValid())
                allValid = false;
        }

        return allValid;
    }

    public class PatternBox extends Div
    {
        private final PatternContainer patternContainer;

        private final Pattern pattern;

        private final Div nameDiv;
        private final NumberField amountField;

        private final Binder<NumberField> amountBinder;

        public PatternBox(PatternContainer patternContainer)
        {
            this.patternContainer = patternContainer;

            pattern = patternContainer.getEntity().getPattern();

            nameDiv = new Div();
            nameDiv.getElement().setAttribute("tabindex", "-1");
            nameDiv.getStyle()
                    .set("margin-bottom", "2px")
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

            nameDiv.setText(pattern.getName());

            amountField = new NumberField();
            amountField.setWidthFull();
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);

            amountField.setValue(Objects.requireNonNullElse(patternContainer.getEntity().getAmount(), 0).doubleValue());

            amountField.addValueChangeListener(event ->
            {
                patternContainer.setTempAmount(Objects.requireNonNullElse(event.getValue(), 0).intValue());
                patternContainer.setTempState(ContainerState.UPDATE);
                Notification.show("UPDATE");
            });

            amountBinder = new Binder<>();
            amountBinder.forField(amountField)
                    .asRequired()
                    .bind(NumberField::getValue, NumberField::setValue);

            this.add(nameDiv, amountField);
            this.setWidth("calc(100% / 3.3)");
            this.getStyle()
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)");
        }

        public boolean isValid()
        {
            amountBinder.validate();

            return amountBinder.isValid();
        }
    }

}
