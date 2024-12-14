package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.MenuService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilder.bounds.BoundButton;
import de.bauersoft.views.menuBuilder.bounds.BoundComboBox;
import de.bauersoft.views.menuBuilder.bounds.BoundTextField;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MenuBuilderDialogOld1 extends Dialog
{
    private MenuService menuService;

    private MenuRepository menuRepository;
    private CourseRepository courseRepository;
    private ComponentRepository componentRepository;

    private MenuDataProvider menuDataProvider;

    private Menu menu;
    private DialogState dialogState;

    private Map<Course, Set<Component>> courseComponentMap;

    private Map<Course, FlexLayout> sectionLayouts;
    private Map<Course, List<Component>> sectionComponents;


    /**For Debug Purpose*/
    private RecipeRepository recipeRepository;

    public MenuBuilderDialogOld1(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                             ComponentRepository componentRepository, PatternRepository patternRepository,
                             MenuDataProvider menuDataProvider, Menu menu,
                             DialogState dialogState,
                             RecipeRepository recipeRepository)
    {
        this.menuService = menuService;
        this.menuRepository = menuRepository;
        this.courseRepository = courseRepository;
        this.menuDataProvider = menuDataProvider;
        this.menu = menu;
        this.dialogState = dialogState;

        this.recipeRepository = recipeRepository;

        courseComponentMap = new HashMap<>();
        sectionComponents = new HashMap<>();
        sectionLayouts = new HashMap<>();

        this.setHeaderTitle(dialogState.toString());

        String defaultWidth = "50vw";

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
            Set<de.bauersoft.data.entities.Component> vegans = componentRepository.findAll().stream()
                    .filter(component -> component.getRecipes().stream()
                            .allMatch(recipe -> recipe.getPatterns().stream()
                                    .anyMatch(pattern -> pattern.equalsDefault(DefaultPattern.VEGAN))))
                    .collect(Collectors.toSet());

            vegans.forEach(component -> System.out.println(component.getName()));

        });

        courseRepository.findAll().forEach(course ->
        {
            FlexLayout sectionLayout = new FlexLayout();
            sectionLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
            sectionLayout.setAlignContent(FlexLayout.ContentAlignment.SPACE_BETWEEN);

            sectionLayout.setWidth(defaultWidth);
            sectionLayout.setMaxWidth(defaultWidth);

            sectionLayouts.put(course, sectionLayout);

            sectionComponents.putIfAbsent(course, new ArrayList<>());

            BoundTextField<Course> textField = new BoundTextField(course);
            textField.getElement().getStyle().set("margin", "5px");
            textField.setWidth("calc(20% - 10px)");
            textField.setValue(course.getName());
            textField.setReadOnly(true);
            textField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

            sectionComponents.get(course).add(textField);

            BoundButton<Course> addButton = new BoundButton(course);
            addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());
            addButton.getElement().getStyle().set("margin", "5px");
            addButton.setHeight(textField.getHeight());

            int itemsPerRow = 5;

            addButton.addClickListener(event ->
            {
                BoundComboBox<Course, de.bauersoft.data.entities.Component> comboBox = new BoundComboBox<>(course);
                comboBox.setItems(componentRepository.findAll());

                comboBox.getElement().getStyle().set("margin", "5px");
                comboBox.setWidth("calc(" + 100/itemsPerRow + "% - 10px)");

                comboBox.setItemLabelGenerator(item -> item.getName());

                comboBox.addValueChangeListener(e ->
                {
                    de.bauersoft.data.entities.Component component = e.getValue();

                    AtomicBoolean isVegan = new AtomicBoolean(true);
                    component.getRecipes().forEach(recipe ->
                    {
                        isVegan.set(recipe.getPatterns().stream()
                                .allMatch(pattern -> pattern.equalsDefault(DefaultPattern.VEGAN)));

                        System.out.println("isVegan: " + isVegan.get());

                        if(!isVegan.get())
                        {
                            BoundComboBox<Course, de.bauersoft.data.entities.Component> comboBox2 = new BoundComboBox<>(course);
                            comboBox.setItems
                                    (
                                        componentRepository.findAll().stream()
                                                .filter(component1 -> component1.getRecipes().stream()
                                                        .flatMap(recipe1 -> recipe1.getPatterns().stream())
                                                        .anyMatch(pattern -> pattern.equalsDefault(DefaultPattern.VEGAN)))
                                                .collect(Collectors.toSet())
                                    );

                            comboBox.getElement().getStyle().set("margin", "5px");
                            comboBox.setWidth("calc(" + 100/itemsPerRow + "% - 10px)");

                            comboBox.setItemLabelGenerator(item -> item.getName());

                            int index = 1;
                            if(sectionComponents.get(course).size() > 2)
                            {
                                for(Component component2 : sectionComponents.get(course))
                                {
                                    if(component2 instanceof ComboBox<?>)
                                    {
                                        index = index + 1;
                                    }
                                }
                            }

                            sectionComponents.get(course).add(index, comboBox);
                        }

                        updateSectionComponents(course);

                    });

                });

                int index = 1;
                if(sectionComponents.get(course).size() > 2)
                {
                    for(Component component : sectionComponents.get(course))
                    {
                        if(component instanceof ComboBox<?>)
                        {
                            index = index + 1;
                        }
                    }
                }

                sectionComponents.get(course).add(index, comboBox);

                BoundButton<Course> removeButton = new BoundButton(course);
                removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());
                removeButton.getElement().getStyle().set("margin", "5px");
                removeButton.setHeight(textField.getHeight());

                removeButton.addClickListener(e ->
                {
                    sectionComponents.get(course).removeIf(component -> !(component instanceof TextField));
                    sectionComponents.get(course).add(addButton);

                    updateSectionComponents(course);
                });

                sectionComponents.get(course).set(sectionComponents.get(course).size() - 1, removeButton);

                updateSectionComponents();
            });

            sectionComponents.get(course).add(addButton);
        });

        updateSectionComponents();




        /*List<TextField> fields = new ArrayList<>();
        for(int i = 0; i < 8; i++)
        {
            TextField textField = new TextField("" + i);
            textField.getElement().getStyle().set("margin", "5px");
            textField.setWidth("calc(20% - 10px)");

            fields.add(textField);
        }

        FlexLayout layout = new FlexLayout();
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        layout.setWidth(defaultWidth);
        layout.setMaxWidth(defaultWidth);

        TextField courseField = new TextField("Vorspeise");
        courseField.getElement().getStyle().set("margin", "5px");
        courseField.setWidth("calc(20% - 10px)");

        layout.add(courseField);

        fields.forEach(textField -> layout.add(textField));*/




        /*VerticalLayout courseListLayout = new VerticalLayout();
        courseListLayout.setWidth("20%");
        courseListLayout.setHeight("10em * 5");

        courseRepository.findAll().stream().forEach(course ->
        {
            BoundButton<Course> button = new BoundButton<>(course);
            button.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());

            button.setMaxHeight("2em");
            button.setMaxWidth("2em");

            courseListLayout.add(button);
        });*/




        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);

        //saveButton.setMinWidth("2em * " + saveButton.getText().toCharArray().length);
        saveButton.addClickListener(event ->
        {
            //TODO bean save
            this.close();
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);

        //cancelButton.setWidth("calc(2em * 1000 + " + cancelButton.getText().toCharArray().length + ")");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            //TODO bean remove
            this.close();
        });

        this.add(inputLayout);

        sectionLayouts.values().forEach(flexLayout -> this.add(flexLayout));

        this.getFooter().add(new HorizontalLayout(cancelButton, saveButton));
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();

        /**code trash**/

        /*FormLayout layout = new FormLayout();
        layout.setWidth(defaultWidth);
        layout.setMaxWidth(defaultWidth);

        layout.setResponsiveSteps
                (
                        new FormLayout.ResponsiveStep("0", 6),
                        new FormLayout.ResponsiveStep("900px", 8)
                );*/
    }

    private void updateSectionComponents()
    {
        courseRepository.findAll().forEach(course -> updateSectionComponents(course));
    }

    private void updateSectionComponents(Course course)
    {
        Objects.requireNonNull(course, "course cannot be null.");

        FlexLayout sectionLayout = sectionLayouts.get(course);
        if(sectionLayout == null)

            return;

        sectionLayout.removeAll();
        sectionLayout.add(sectionComponents.get(course));
    }

}
