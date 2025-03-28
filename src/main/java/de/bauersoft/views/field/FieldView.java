package de.bauersoft.views.field;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.field.Field;
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
	private final OrderService orderService;
	private final InstitutionService institutionService;
	private final InstitutionFieldService institutionFieldService;
	private final FieldMultiplierService fieldMultiplierService;
	private final CourseService courseService;
	private final OfferService offerService;
	
    private final FilterDataProvider<Field, Long> filterDataProvider;
	private final AutofilterGrid<Field, Long> grid;

    public FieldView(FieldService fieldService, OrderService orderService, InstitutionService institutionService, InstitutionFieldService institutionFieldService, FieldMultiplierService fieldMultiplierService, CourseService courseService, OfferService offerService)
    {
        this.fieldService = fieldService;
        this.orderService = orderService;
        this.institutionService = institutionService;
        this.institutionFieldService = institutionFieldService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.courseService = courseService;
        this.offerService = offerService;
		
        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(fieldService);
		grid = new AutofilterGrid<>(filterDataProvider);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Field::getName, false);

		grid.AutofilterGridContextMenu()
						.enableGridContextMenu()
						.enableAddItem("Neue Einrichtungsart", event ->
						{
							new FieldDialog(filterDataProvider, fieldService, fieldMultiplierService, courseService, new Field(), DialogState.NEW);

						}).enableDeleteItem("Löschen", event ->
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

								filterDataProvider.refreshAll();
							});
						});


		grid.addItemDoubleClickListener(event ->
		{
			new FieldDialog(filterDataProvider, fieldService, fieldMultiplierService, courseService, event.getItem(), DialogState.EDIT);
		});

        this.add(grid);
    }
}
