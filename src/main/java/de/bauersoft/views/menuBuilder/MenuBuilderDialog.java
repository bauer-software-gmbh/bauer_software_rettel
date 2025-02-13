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
import de.bauersoft.services.FleshService;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.OrderDataService;
import de.bauersoft.services.VariantService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilder.cluster.MenuBuilderClusterManager;
import de.bauersoft.views.menuBuilder.cluster.PatternCluster;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuBuilderDialog extends Dialog
{
    private MenuService menuService;

    private MenuRepository menuRepository;
    private CourseRepository courseRepository;
    private ComponentRepository componentRepository;
    private PatternRepository patternRepository;
    private RecipeRepository recipeRepository;
    private VariantService variantService;
    private FleshService fleshService;

    private MenuDataProvider menuDataProvider;

    private Menu menu;
    private DialogState dialogState;

    private MenuBuilderClusterManager menuBuilderClusterManager;

    public MenuBuilderDialog(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                             ComponentRepository componentRepository, PatternRepository patternRepository,
                             MenuDataProvider menuDataProvider, Menu menu,
                             DialogState dialogState,
                             RecipeRepository recipeRepository, VariantService variantService, FleshService fleshService,
                             OrderDataService orderDataService)
    {
        this.menuService = menuService;
        this.menuRepository = menuRepository;
        this.courseRepository = courseRepository;
        this.componentRepository = componentRepository;
        this.patternRepository = patternRepository;

        this.menuDataProvider = menuDataProvider;
        this.menu = menu;
        this.dialogState = dialogState;

        this.recipeRepository = recipeRepository;
        this.variantService = variantService;
        this.fleshService = fleshService;

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

        menuBuilderClusterManager = new MenuBuilderClusterManager(menu, menuService, variantService, componentRepository, courseRepository, patternRepository, orderDataService);

        menuBuilderDiv.add(menuBuilderClusterManager);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name is required");
        }).bind("name");

        binder.forField(fleshComboBox).bind("flesh");

        binder.setBean(menu);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
            binder.validate();
            if(!binder.isValid()) return;

            menuRepository.save(menu);

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

            //variantService.getRepository().deleteAllByMenuId(menu.getId());

            menuDataProvider.refreshAll();
            this.close();
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            menuDataProvider.refreshAll();
            this.close();
        });

        inputLayout.add(menuBuilderDiv);
        this.add(inputLayout);
        //this.add(menuBuilderDiv);

//        this.setWidth("50vw");
//        this.setMaxWidth("50vw");

        this.getFooter().add(new HorizontalLayout(saveButton, cancelButton));
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }

}

