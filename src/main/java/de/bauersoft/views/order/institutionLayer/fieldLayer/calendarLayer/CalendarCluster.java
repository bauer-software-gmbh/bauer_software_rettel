package de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTab;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.allergenLayer.AllergenComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.variantLayer.VariantBoxComponent;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@CssImport(value = "./themes/rettels/views/order.css")
public class CalendarCluster extends VerticalLayout
{
    private final OrderManager orderManager;
    private final FieldTab fieldTab;
    private final InstitutionField institutionField;

    private final Paragraph paragraph;

    private HorizontalLayout calendarLayout;

    private DatePicker.DatePickerI18n datePickerI18n;
    private DatePicker datePicker;

    private TimePicker orderStart;
    private TimePicker orderEnd;

    private final Map<LocalDate, VariantBoxComponent> variantBoxComponentMap;
    private final Map<LocalDate, AllergenComponent> allergenComponentMap;
    private final Set<Order> orders;

    private final Button saveButton;

    public CalendarCluster(OrderManager orderManager, FieldTab fieldTab)
    {
        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");

        this.orderManager = orderManager;
        this.fieldTab = fieldTab;
        this.institutionField = fieldTab.getInstitutionField();

        variantBoxComponentMap = new HashMap<>();
        allergenComponentMap = new HashMap<>();
        orders = new HashSet<>();

        paragraph = new Paragraph();

        initializeCalendar();

        orderStart = new TimePicker("Bestellzeitraum von", institutionField.getInstitution().getOrderStart());
        orderStart.setReadOnly(true);
        orderStart.getStyle()
                .setMaxWidth("10em")
                .set("tabindex", "-1");
        orderStart.getElement()
                .setAttribute("tabindex", "-1");

        orderEnd = new TimePicker("bis", institutionField.getInstitution().getOrderEnd());
        orderEnd.setReadOnly(true);
        orderEnd.getStyle()
                .setMaxWidth("10em");
        orderEnd.getElement()
                .setAttribute("tabindex", "-1");

        calendarLayout.add(orderStart, orderEnd);

        saveButton = new Button("Bestellung abschicken!");
        saveButton.getStyle().set("position", "fixed");
        saveButton.getStyle().set("bottom", "20px");
        saveButton.getStyle().set("right", "20px");
        saveButton.addClickListener(event ->
        {
            if(!this.validate()) return;

            try
            {
                this.save();

                fieldTab.getTab()
                        .getStyle()
                        .setColor("green")
                        .setFontWeight(Style.FontWeight.BOLD);

                Notification notification = new Notification();
                notification.setText("Die Bestellung wurde erfolgreich gespeichert!");
                notification.setPosition(Notification.Position.MIDDLE);
                notification.setDuration(5000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                notification.open();

            }catch(Exception e)
            {
                Notification.show("Es ist ein Fehler aufgetreten!", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

                e.printStackTrace();
            }
        });

        this.add(saveButton);

        this.setWidthFull();
        this.setHeightFull();
    }

    private void initializeCalendar()
    {
        datePickerI18n = new DatePicker.DatePickerI18n();
        datePickerI18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(0);

        datePicker = new DatePicker("Bestelldatum");
        datePicker.setI18n(datePickerI18n);

        calendarLayout = new HorizontalLayout(datePicker);

        this.add(calendarLayout);

        datePicker.addValueChangeListener(event ->
        {
            VariantBoxComponent variantBoxComponent = variantBoxComponentMap.get(event.getOldValue());
            if(variantBoxComponent != null)
                this.remove(variantBoxComponent);

            AllergenComponent allergenComponent = allergenComponentMap.get(event.getOldValue());
            if(allergenComponent != null)
                this.remove(allergenComponent);

            Optional<Offer> offerOptional = orderManager.getOfferService().findByLocalDateAndField(event.getValue(), institutionField.getField());
            Optional<Order> orderOptional = orderManager.getOrderService().findByLocalDateAndInstitutionAndField(event.getValue(), institutionField.getInstitution(), institutionField.getField());
            if(offerOptional.isEmpty())
            {
                String text = "Für den " + DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN).format(event.getValue()) + " ist kein Angebot vorhanden.";
                if(orderOptional.isPresent())
                    text = "Für den " + DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN).format(event.getValue()) + " ist kein Angebot vorhanden. Es wurde jedoch bereits eine Bestellung aufgegeben. Bitte kontaktieren Sie Rettel, dies ist ein Fehler!";

                paragraph.setText(text);

                this.add(paragraph);
                return;
            }

            Optional<Menu> menuOptional = offerOptional.get().getMenus().stream().findFirst();
            if(menuOptional.isEmpty())
            {
                String text = "Für den " + DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN).format(event.getValue()) + " ist kein Menü vorhanden.";
                paragraph.setText(text);

                this.add(paragraph);
                return;
            }

            this.remove(paragraph);

            Order order = orderOptional.orElseGet(() ->
            {
                Order newOrder = new Order();
                newOrder.setLocalDate(event.getValue());
                newOrder.setInstitution(institutionField.getInstitution());
                newOrder.setField(institutionField.getField());

                return newOrder;
            });

            orders.add(order);

            variantBoxComponent = variantBoxComponentMap.get(event.getValue());
            if(variantBoxComponent == null)
            {
                variantBoxComponent = new VariantBoxComponent(orderManager, institutionField, this, menuOptional.get(), order);
                variantBoxComponentMap.put(event.getValue(), variantBoxComponent);
            }

            allergenComponent = allergenComponentMap.get(event.getValue());
            if(allergenComponent == null)
            {
                allergenComponent = new AllergenComponent(orderManager, institutionField, this, order);
                allergenComponentMap.put(event.getValue(), allergenComponent);
            }

            this.add(variantBoxComponent, allergenComponent);
        });

        datePicker.setValue(LocalDate.now());
    }

    public boolean validate()
    {
        boolean allValid = true;
        for(VariantBoxComponent variantBoxComponent : variantBoxComponentMap.values())
        {
            if(!variantBoxComponent.validate())
                allValid = false;
        }

        for(AllergenComponent allergenComponent : allergenComponentMap.values())
        {
            if(!allergenComponent.validate())
                allValid = false;
        }

        return allValid;
    }

    public void save()
    {
        orderManager.getOrderService().updateAll(orders);
        variantBoxComponentMap.values().forEach(VariantBoxComponent::save);
        allergenComponentMap.values().forEach(AllergenComponent::save);
    }
}
