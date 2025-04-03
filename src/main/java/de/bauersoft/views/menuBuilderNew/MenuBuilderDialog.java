package de.bauersoft.views.menuBuilderNew;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilderNew.components.ClusterManager;

public class MenuBuilderDialog extends Dialog
{
    private final FilterDataProvider<Menu, Long> filterDataProvider;

    private final MenuService menuService;
    private final CourseService courseService;
    private final ComponentService componentService;
    private final PatternService patternService;
    private final RecipeService recipeService;
    private final VariantService variantService;
    private final FleshService fleshService;
    private final OfferService offerService;
    private final OrderDataService orderDataService;

    private final Menu item;
    private final DialogState state;

    public MenuBuilderDialog(FilterDataProvider<Menu, Long> filterDataProvider, MenuService menuService, CourseService courseService, ComponentService componentService, PatternService patternService, RecipeService recipeService, VariantService variantService, FleshService fleshService, OfferService offerService, OrderDataService orderDataService, Menu item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.menuService = menuService;
        this.courseService = courseService;
        this.componentService = componentService;
        this.patternService = patternService;
        this.recipeService = recipeService;
        this.variantService = variantService;
        this.fleshService = fleshService;
        this.offerService = offerService;
        this.orderDataService = orderDataService;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

        Binder<Menu> binder = new Binder<>(Menu.class);

        binder.readBean(item);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50rem");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameField = new TextField();
        nameField.setMaxLength(64);
        nameField.setRequired(true);
        nameField.setWidth("20rem");

        ComboBox<Flesh> fleshComboBox = new ComboBox<>();
        fleshComboBox.setClearButtonVisible(true);
        fleshComboBox.setItemLabelGenerator(Flesh::getName);
        fleshComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyFilteredStream(fleshService, query, "name");
        });

        formLayout.setColspan(formLayout.addFormItem(nameField, "Name"), 1);
        formLayout.setColspan(formLayout.addFormItem(fleshComboBox, "Fleischsorte"), 1);

        binder.forField(nameField).asRequired().bind(Menu::getName, Menu::setName);
        binder.forField(fleshComboBox).bind(Menu::getFlesh, Menu::setFlesh);

        ClusterManager clusterManager = new ClusterManager(menuService, courseService, componentService, patternService, recipeService, variantService, fleshService, offerService, orderDataService, item);

        binder.readBean(item);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
            binder.writeBeanIfValid(item);
            if(binder.isValid())
            {
                menuService.update(item);

                clusterManager.getVariantMapContainer().acceptTemporaries().run(variantService);

                filterDataProvider.refreshAll();
                this.close();
            }
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            filterDataProvider.refreshAll();
            this.close();
        });

        this.add(formLayout, clusterManager);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}
