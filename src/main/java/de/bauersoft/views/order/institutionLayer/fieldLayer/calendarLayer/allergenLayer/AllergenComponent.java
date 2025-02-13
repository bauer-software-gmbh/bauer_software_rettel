package de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.allergenLayer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.order.OrderAllergenKey;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class AllergenComponent extends VerticalLayout
{
    private final OrderManager orderManager;
    private final InstitutionField institutionField;
    private final CalendarCluster calendarCluster;

    private final Order order;
    private final List<AllergenRow> allergenRows;

    private final List<Allergen> allergensPool;
    private final ListDataProvider<Allergen> allergenListDataProvider;

    private final Button addButton;

    public AllergenComponent(OrderManager orderManager, InstitutionField institutionField, CalendarCluster calendarCluster, Order order)
    {

        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");
        Objects.requireNonNull(institutionField, "InstitutionField cannot be null!");
        Objects.requireNonNull(calendarCluster, "CalendarCluster cannot be null!");
        Objects.requireNonNull(order, "Order cannot be null!");

        this.orderManager = orderManager;
        this.calendarCluster = calendarCluster;
        this.institutionField = institutionField;
        this.order = order;

        allergenRows = new ArrayList<>();

        allergensPool = new ArrayList<>(orderManager.getAllergenService().findAll());
        allergenListDataProvider = new ListDataProvider<>(allergensPool);

        addButton = new Button("Allergen hinzufügen");
        addButton.setIcon(LineAwesomeIcon.PLUS_SOLID.create());
        addButton.setTooltipText("Allergen hinzufügen");

        addButton.addClickListener(event ->
        {
            OrderAllergenKey key = new OrderAllergenKey();

            OrderAllergen orderAllergen = new OrderAllergen();
            orderAllergen.setId(key);
            orderAllergen.set_order(order);

            addAllergenRow(orderAllergen);
        });

        this.add(addButton);

        for(OrderAllergen orderAllergen : order.getOrderAllergens())
        {
            allergensPool.remove(orderAllergen.getAllergen());
            allergenListDataProvider.refreshAll();

            addAllergenRow(orderAllergen);
        }

        this.setWidthFull();
        this.getStyle().setPadding("0px");
    }

    private void addAllergenRow(OrderAllergen orderAllergen)
    {
        AllergenRow allergenRow = new AllergenRow(orderManager, institutionField, allergenListDataProvider, orderAllergen);
        if(orderAllergen.getAllergen() != null)
        {
            allergenRow.getAllergenComboBox().setValue(orderAllergen.getAllergen());
            allergenRow.getAmountField().setValue(Integer.valueOf(orderAllergen.getAmount()).doubleValue());
        }

        allergenRow.getRemoveButton().addClickListener(event ->
        {
            ComboBox<Allergen> allergenComboBox = allergenRow.getAllergenComboBox();
            if(allergenComboBox.getValue() != null)
            {
                allergensPool.add(allergenComboBox.getValue());
                allergenListDataProvider.refreshAll();
            }

            allergenRows.remove(allergenRow);
            this.remove(allergenRow);
        });

        allergenRows.add(allergenRow);

        this.add(allergenRow);
    }

    public boolean validate()
    {
        boolean allValid = true;
        for(AllergenRow allergenRow : allergenRows)
        {
            if(!allergenRow.validate())
                allValid = false;
        }

        return allValid;
    }

    public void save()
    {
        if(order.getId() != null)
            orderManager.getOrderAllergenService().deleteAllByOrderId(order.getId());

        allergenRows.forEach(AllergenRow::save);
    }


    @Getter
    public class AllergenRow extends HorizontalLayout
    {
        private final OrderManager orderManager;
        private final InstitutionField institutionField;
        private final ListDataProvider<Allergen> allergenListDataProvider;

        private final OrderAllergen orderAllergen;

        private final Button removeButton;
        private final ComboBox<Allergen> allergenComboBox;
        private final NumberField amountField;

        private final Binder<ComboBox<Allergen>> allergenBinder;
        private final Binder<NumberField> amountBinder;

        public AllergenRow(OrderManager orderManager, InstitutionField institutionField, ListDataProvider<Allergen> allergenListDataProvider, OrderAllergen orderAllergen)
        {
            this.orderManager = orderManager;
            this.institutionField = institutionField;
            this.allergenListDataProvider = allergenListDataProvider;
            this.orderAllergen = orderAllergen;

            removeButton = new Button();
            removeButton.setIcon(LineAwesomeIcon.MINUS_SOLID.create());

            allergenComboBox = new ComboBox<>();
            allergenComboBox.setPlaceholder("Allergen auswählen");
            allergenComboBox.setItems(allergenListDataProvider);
            allergenComboBox.setClearButtonVisible(true);
            allergenComboBox.setItemLabelGenerator(Allergen::getName);

            allergenComboBox.addValueChangeListener(event ->
            {
                if(event.getValue() != null)
                {
                    allergensPool.remove(event.getValue());
                    allergenComboBox.setTooltipText(event.getValue().getName());

                }else allergenComboBox.setTooltipText("");

                if(event.getOldValue() != null)
                    allergensPool.add(event.getOldValue());

                allergenListDataProvider.refreshAll();
            });

            allergenBinder = new Binder<>();
            allergenBinder.forField(allergenComboBox)
                    .asRequired()
                    .bind(ComboBox::getValue, ComboBox::setValue);

            amountField = new NumberField();
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMaxWidth("3em");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);

            amountBinder = new Binder<>();
            amountBinder.forField(amountField)
                    .asRequired()
                    .bind(NumberField::getValue, NumberField::setValue);

            this.add(removeButton, allergenComboBox, amountField);
        }

        public boolean validate()
        {
            amountBinder.validate();
            allergenBinder.validate();

            return amountBinder.isValid() && allergenBinder.isValid();
        }

        public void save()
        {
            orderAllergen.getId().setOrderId(order.getId());
            orderAllergen.getId().setAllergenId(allergenComboBox.getValue().getId());
            orderAllergen.setAllergen(allergenComboBox.getValue());

            orderAllergen.setAmount(amountField.getValue().intValue());

            orderManager.getOrderAllergenService().update(orderAllergen);
        }
    }
}
