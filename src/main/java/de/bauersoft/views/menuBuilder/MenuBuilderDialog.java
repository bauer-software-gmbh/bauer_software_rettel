package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.flesh.DefaultFlesh;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBComponentRepository;
import de.bauersoft.data.repositories.menuBuilder.MBMenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBPatternRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilder.cluster.MenuBuilderClusterManager;
import de.bauersoft.views.menuBuilder.cluster.PatternCluster;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Menu menu;
    private final DialogState dialogState;

    private MenuBuilderClusterManager menuBuilderClusterManager;

    public MenuBuilderDialog(FilterDataProvider<Menu, Long> filterDataProvider, MenuService menuService, CourseService courseService, ComponentService componentService, PatternService patternService, RecipeService recipeService, VariantService variantService, FleshService fleshService, OfferService offerService, OrderDataService orderDataService, Menu menu, DialogState dialogState)
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

        this.menu = menu;
        this.dialogState = dialogState;


        this.setHeaderTitle(dialogState.toString());

        Binder<Menu> binder = new Binder<>(Menu.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        inputLayout.setWidth("50vw");
        inputLayout.setMaxWidth("50em");

        TextField nameTextField = new TextField();
        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
        nameTextField.setMaxLength(64);
        nameTextField.setRequired(true);
        nameTextField.setWidth("20em");

        ComboBox<Flesh> fleshComboBox = new ComboBox<>();
        inputLayout.setColspan(inputLayout.addFormItem(fleshComboBox, "Fleischsorte"), 1);
        fleshComboBox.setClearButtonVisible(true);
        fleshComboBox.setItems(fleshService.getRepository().findAll());
        fleshComboBox.setItemLabelGenerator(Flesh::getName);



        Div menuBuilderDiv = new Div();

        menuBuilderClusterManager = new MenuBuilderClusterManager(menu,
                menuService,
                variantService,
                componentService.getRepository(),
                courseService.getRepository(), patternService.getRepository(), orderDataService);

        menuBuilderDiv.add(menuBuilderClusterManager);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name is required");
        }).bind("name");

        binder.forField(fleshComboBox).bind("flesh");

        binder.readBean(menu);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
            binder.writeBeanIfValid(menu);
            if(!binder.isValid()) return;

            menuService.update(menu);

            menuBuilderClusterManager.getDefaultCluster().saveCertainlyExistingData();
            menuBuilderClusterManager.getPatternClusters().forEach(patternCluster -> patternCluster.saveCertainlyExistingData());

            variantService.updateVariants(
                    variantService.getRepository().findAllByMenuId(menu.getId()),
                    Stream.of(
                                    List.of(menuBuilderClusterManager.getDefaultCluster().getVariant()),
                                    menuBuilderClusterManager.getPatternClusters()
                                            .stream()
                                            .map(PatternCluster::getVariant)
                                            .collect(Collectors.toList())
                            ).flatMap(List::stream)
                            .collect(Collectors.toList())
            );

            filterDataProvider.refreshAll();
            this.close();
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            binder.removeBean();
            filterDataProvider.refreshAll();
            this.close();
        });

        inputLayout.add(menuBuilderDiv);
        this.add(inputLayout);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }

}

