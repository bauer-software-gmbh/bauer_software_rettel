package de.bauersoft.views.field;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.providers.FieldDataProvider;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Einrichtungsart")
@Route(value = "field", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "OFFICE", "OFFICE_ADMIN"})
public class FieldView extends Div
{
	private final FieldService fieldService;
	private final FieldDataProvider fieldDataProvider;
	private final OrderService orderService;
	private final InstitutionService institutionService;
	private final InstitutionFieldsService institutionFieldsService;
	private final FieldMultiplierService fieldMultiplierService;
	private final CourseService courseService;
	private final OfferService offerService;
	
    AutoFilterGrid<Field> grid = new AutoFilterGrid<>(Field.class, false, true);

    public FieldView(FieldService fieldService, FieldDataProvider fieldDataProvider, OrderService orderService, InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, FieldMultiplierService fieldMultiplierService, CourseService courseService, OfferService offerService)
    {
        this.fieldService = fieldService;
        this.fieldDataProvider = fieldDataProvider;
        this.orderService = orderService;
        this.institutionService = institutionService;
        this.institutionFieldsService = institutionFieldsService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.courseService = courseService;
        this.offerService = offerService;
		
        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(fieldDataProvider);

        grid.addColumn("name");
        grid.addItemDoubleClickListener(event ->
		{
			new FieldDialog(fieldService, fieldDataProvider, fieldMultiplierService, courseService, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Field> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neue Einrichtung", event ->
		{
			new FieldDialog(fieldService, fieldDataProvider, fieldMultiplierService, courseService, new Field(), DialogState.NEW);
		});

		GridMenuItem<Field> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				boolean cancel = false;
				if(orderService.getRepository().existsByFieldId(item.getId()))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Einrichtung \"" + item.getName() + "\" kann nicht gelöscht werden, da sie noch von einigen Bestellungen verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				//TODO institutionFields
//				if(institutionFieldsService.getRepository().existsByFieldId(item.getId()))
//				{
//					Div div = new Div();
//					div.setMaxWidth("33vw");
//					div.getStyle().set("white-space", "normal");
//					div.getStyle().set("word-wrap", "break-word");
//
//					div.add(new Text("Das Field \"" + item.getName() + "\" kann nicht gelöscht werden da es noch von einigen Institutionen verwendet wird."));
//
//					Notification notification = new Notification(div);
//					notification.setDuration(5000);
//					notification.setPosition(Notification.Position.MIDDLE);
//					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
//					notification.open();
//
//					cancel = true;
//				}

				if(offerService.existsByField(item))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Einrichtung \"" + item.getName() + "\" kann nicht gelöscht werden, da sie noch von einigen Institutionen verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				if(cancel) return;

				fieldMultiplierService.deleteAllByFieldId(item.getId());
				fieldService.deleteById(item.getId());
				fieldDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
