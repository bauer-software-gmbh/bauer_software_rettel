package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.menu.MenuPatternComponents;
import de.bauersoft.data.entities.menu.MenuPatternComponentsId;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuPatternComponentsRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.MenuService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilder.cluster.*;

import java.util.*;

public class MenuBuilderDialog extends Dialog
{
    private MenuService menuService;

    private MenuRepository menuRepository;
    private CourseRepository courseRepository;
    private ComponentRepository componentRepository;
    private PatternRepository patternRepository;
    private RecipeRepository recipeRepository;

    private MenuDataProvider menuDataProvider;

    private Menu menu;
    private DialogState dialogState;

    private MenuBuilderClusterManager menuBuilderClusterManager;

    public MenuBuilderDialog(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                             ComponentRepository componentRepository, PatternRepository patternRepository,
                             MenuDataProvider menuDataProvider, Menu menu,
                             DialogState dialogState,
                             RecipeRepository recipeRepository, MenuPatternComponentsRepository menuPatternComponentsRepository)
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

        this.setHeaderTitle(dialogState.toString());

        String defaultWidth = "50vw";

        Binder<Menu> binder = new Binder<>(Menu.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        inputLayout.setWidth(defaultWidth);
        inputLayout.setMaxWidth(defaultWidth);
        inputLayout.setHeight("15rem");

        TextField nameTextField = new TextField();
        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
        nameTextField.setMaxLength(50);
        nameTextField.setRequired(true);

        nameTextField.setWidth("20rem");

        TextArea descriptionTextArea = new TextArea();
        inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
        descriptionTextArea.setMaxLength(2048);

        descriptionTextArea.setWidth("25rem");
        descriptionTextArea.setMaxWidth("25rem");
        descriptionTextArea.setMinHeight("10rem");
        descriptionTextArea.setMaxHeight("10rem");

        Button debugButton = new Button("Debug");
        inputLayout.add(debugButton);
        debugButton.addClickListener(event ->
        {
//            List<Component> componentIds = menuPatternComponentsRepository.findComponentIdsByIds(1l, 5l);
//            componentIds.forEach(aLong ->
//            {
//                System.out.println(aLong.getName());
//            });
//
//            System.out.println("----------------------");
//
//            List<Component> components2 = menuPatternComponentsRepository.findComponentIdsByIds(1l, 5l, 1l);
//            components2.forEach(component ->
//            {
//                System.out.println(component.getName());
//            });

//            for(Map.Entry<Pattern, MenuPatternComponents> entry : menu.getPatternComponentsMap().entrySet())
//            {
//                System.out.println(entry.getKey().getName() + " - " + entry.getValue());
//            }

//            for(Map.Entry<Pattern, MenuPatternComponents> entry : menu.getMenuPatternComponents().get().entrySet())
//            {
//                System.out.println("key: " + entry.getKey().getName());
//                System.out.println("value pattern: " + entry.getValue().getPattern().getName());
//                entry.getValue().getComponents().forEach(component ->
//                {
//                    System.out.println("value component: " + component.getName());
//                });
//
//                System.out.println("---------------------------");;
//            }

//            List<MenuPatternComponents> menuPatternComponents = menuPatternComponentsRepository.findMenuPatternComponentsByIds(1l, null);
//            if(menuPatternComponents == null)
//            {
//                System.out.println("Nullllllllllllllll");
//
//            }else
//            {
//                menuPatternComponents.forEach(menuPatternComponent ->
//                {
//                    if(menuPatternComponent == null)
//                    {
//                        System.out.println("null 2");
//                    }else
//                    {
//                        menuPatternComponent.getComponents().forEach(component ->
//                        {
//                            System.out.println(component.getName());
//                        });
//                    }
//
//                });
//            }

            MenuPatternComponentsId id = new MenuPatternComponentsId();
            id.setMenuId(1l);
            id.setPatternId(null);

            MenuPatternComponents menuPatternComponent = new MenuPatternComponents();
            menuPatternComponent.setId(id);

            Set<Component> componentss = new HashSet<>();
            componentss.addAll(componentRepository.findAll());

            menuPatternComponent.setComponents(componentss);

            menuPatternComponentsRepository.save(menuPatternComponent);
        });

        Div menuBuilderDiv = new Div();

        menuBuilderClusterManager = new MenuBuilderClusterManager(menu, componentRepository, courseRepository, patternRepository);

        menuBuilderDiv.add(menuBuilderClusterManager);



        binder.forField(nameTextField).asRequired().bind("name");
        binder.bind(descriptionTextArea, "description");

        binder.setBean(menu);


        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);

        saveButton.addClickListener(event ->
        {
            //TODO bean save
            this.close();
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);

        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            //TODO bean remove
            this.close();
        });

        this.add(inputLayout);
        this.add(menuBuilderDiv);

        this.setWidth("65vw");
        this.setMaxWidth("65vw");

        this.getFooter().add(new HorizontalLayout(cancelButton, saveButton));
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }

}

