package de.bauersoft.views.pattern;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.providers.PatternDataProvider;
import de.bauersoft.services.PatternService;
import de.bauersoft.services.RecipeService;
import de.bauersoft.services.VariantService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;

@PageTitle("Ernährungsformen")
@Route(value = "pattern", layout = MainLayout.class)
@RolesAllowed(value = {"ADMIN", "ACCOUNTENT"})
@Getter
public class PatternView extends Div
{
	private final PatternService patternService;
	private final VariantService variantService;
	private final RecipeService recipeService;

	private final FilterDataProvider<Pattern, Long> filterDataProvider;
    private final AutofilterGrid<Pattern, Long> grid;

    public PatternView(PatternService patternService, VariantService variantService, RecipeService recipeService)
    {
        this.patternService = patternService;
        this.variantService = variantService;
        this.recipeService = recipeService;

        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(patternService);

		grid = new AutofilterGrid<>(filterDataProvider);
		grid.setSizeFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name", "Name", Pattern::getName, false);

        grid.AutofilterGridContextMenu()
				.enableGridContextMenu()
				.enableAddItem("Neue Ernährungsform", event ->
				{
					new PatternDialog(filterDataProvider, patternService, new Pattern(), DialogState.NEW);

				}).enableAddItem("Löschen", event ->
				{
					event.getItem().ifPresent(item ->
					{
						boolean cancel = false;

						if(variantService.getRepository().existsByPatternId(item.getId()))
						{
							Div div = new Div();
							div.setMaxWidth("33vw");
							div.getStyle().set("white-space", "normal");
							div.getStyle().set("word-wrap", "break-word");

							div.add(new Text("Die Ernährungsform \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch in einigen Menü-Varianten verwendet wird."));

							Notification notification = new Notification(div);
							notification.setDuration(5000);
							notification.setPosition(Notification.Position.MIDDLE);
							notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
							notification.open();

							cancel = true;
						}

						if(recipeService.getRepository().existsByPatternsId(item.getId()))
						{
							Div div = new Div();
							div.setMaxWidth("33vw");
							div.getStyle().set("white-space", "normal");
							div.getStyle().set("word-wrap", "break-word");

							div.add(new Text("Die Ernährungsform \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch in einigen Rezepten verwendet wird."));

							Notification notification = new Notification(div);
							notification.setDuration(5000);
							notification.setPosition(Notification.Position.MIDDLE);
							notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
							notification.open();

							cancel = true;
						}

						if(cancel) return;

						patternService.deleteById(item.getId());
						filterDataProvider.refreshAll();
					});
				});

		grid.addItemDoubleClickListener(event ->
		{
			new PatternDialog(filterDataProvider, patternService, event.getItem(), DialogState.EDIT);
		});

        this.add(grid);
    }
}
