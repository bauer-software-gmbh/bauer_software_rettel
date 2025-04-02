package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.services.AllergenService;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.OrderComponent;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;

public class AllergenComponent extends VerticalLayout
{
    private final OrderManager orderManager;
    private final OrderComponent orderComponent;
    private final Order order;

    private final AllergenMapContainer allergenMapContainer;

    private final InstitutionField institutionField;
    private final Institution institution;
    private final Field field;

    private final AllergenService allergenService;

    private final List<AllergenRow> allergenRows;

    private final Button addButton;

    public AllergenComponent(OrderManager orderManager, OrderComponent orderComponent, Order order, AllergenMapContainer allergenMapContainer)
    {
        this.orderManager = orderManager;
        this.orderComponent = orderComponent;
        this.order = order;

        this.allergenMapContainer = allergenMapContainer;

        institutionField = orderComponent.getInstitutionField();
        institution = institutionField.getInstitution();
        field = institutionField.getField();

        allergenService = orderManager.getAllergenService();

        allergenRows = new ArrayList<>();

        addButton = new Button("Allergen hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);

        addButton.addClickListener(event ->
        {
            AllergenContainer allergenContainer = (AllergenContainer) allergenMapContainer.addIfAbsent(allergenMapContainer.nextMapper(), () ->
            {
                OrderAllergen orderAllergen = new OrderAllergen();
                orderAllergen.set_order(order);

                return orderAllergen;

            }, ContainerState.SHOW);

            AllergenRow allergenRow = new AllergenRow(allergenContainer);
            allergenRows.add(allergenRow);

            this.add(allergenRow);
        });

        for(Container<OrderAllergen, Long> container : allergenMapContainer.getContainers())
        {
            AllergenRow allergenRow = new AllergenRow((AllergenContainer) container);
            allergenRows.add(allergenRow);

            this.add(allergenRow);
        }

        this.getStyle()
                .setWidth("calc(100em/5)")
                .setMaxWidth("calc(100em/5)")
                .setPadding("0px")
                .setMarginBottom("20em");
    }

    private class AllergenRow extends HorizontalLayout
    {
        private final AllergenContainer container;

        private Button removeButton;
        private MultiSelectComboBox<Allergen> comboBox;

        public AllergenRow(AllergenContainer container)
        {
            this.container = container;

            removeButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());
            removeButton.addClickListener(event ->
            {
                AllergenComponent.this.remove(this);
                allergenRows.remove(this);

                container.setTempState((container.getState() == ContainerState.NEW) ? ContainerState.NEW : ContainerState.DELETE);
            });

            comboBox = new MultiSelectComboBox<>();
            comboBox.setWidthFull();
            comboBox.setItemLabelGenerator(Allergen::getName);
            comboBox.setItems(query ->
            {
                return FilterDataProvider.lazyFilteredStream(allergenService, query, "name");
            });

            comboBox.setValue(container.getEntity().getAllergens());

            comboBox.addValueChangeListener(event ->
            {
                container.setTempState(ContainerState.UPDATE);
                container.setTempAllergens(event.getValue());
            });


            this.add(removeButton, comboBox);
            this.setWidthFull();
        }

    }
}
