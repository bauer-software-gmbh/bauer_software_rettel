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
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.RecipeService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.menuBuilder.bounds.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MenuBuilderDialog extends Dialog
{
    private MenuService menuService;

    private MenuRepository menuRepository;
    private CourseRepository courseRepository;
    private ComponentRepository componentRepository;
    private PatternRepository patternRepository;

    private MenuDataProvider menuDataProvider;

    private Menu menu;
    private DialogState dialogState;

    private Map<Course, Set<Component>> courseComponentMap;

    private Map<Course, FlexLayout> sectionLayouts;
    private Map<Course, List<Component>> sectionComponents;

    private List<RowCluster> rowClusters;


    /**For Debug Purpose*/
    private RecipeRepository recipeRepository;

    public MenuBuilderDialog(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                             ComponentRepository componentRepository, PatternRepository patternRepository,
                             MenuDataProvider menuDataProvider, Menu menu,
                             DialogState dialogState,
                             RecipeRepository recipeRepository)
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

        courseComponentMap = new HashMap<>();
        sectionComponents = new HashMap<>();
        sectionLayouts = new HashMap<>();

        rowClusters = new ArrayList<>();

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
            Collection<de.bauersoft.data.entities.Component> vegans = getPatternMatchingComponents(componentRepository.findAll(), DefaultPattern.VEGAN);

            vegans.forEach(component -> System.out.println(component.getName()));
        });


        courseRepository.findAll().forEach(course ->
        {
            RowCluster rowCluster = new RowCluster(course, componentRepository);
            rowClusters.add(rowCluster);
        });





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

        //sectionLayouts.values().forEach(flexLayout -> this.add(flexLayout));
        rowClusters.forEach(rowCluster -> this.add(rowCluster));

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

    /**
     * Returns a collection of {@link de.bauersoft.data.entities.Component} objects where all recipes in each component
     * have at least one pattern that matches the given {@link DefaultPattern}.
     *
     * The method filters the provided collection of components. For each component, it checks all of its recipes to ensure
     * that every recipe has at least one pattern that matches the provided {@link DefaultPattern}. Only components where
     * all recipes meet this condition will be included in the returned collection.
     *
     * @param components the collection of {@link de.bauersoft.data.entities.Component} objects to be filtered
     * @param toMatch the {@link DefaultPattern} that the component's recipe patterns should match
     * @return a collection of components where all recipes contain at least one pattern that matches the specified {@link DefaultPattern}
     * @throws NullPointerException if the {@code components} is {@code null}
     */
    private Collection<de.bauersoft.data.entities.Component> getPatternMatchingComponents(Collection<de.bauersoft.data.entities.Component> components, DefaultPattern toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null.");

        Collection<de.bauersoft.data.entities.Component> matching = components.stream()
                .filter(component -> component.getRecipes().stream()
                        .allMatch(recipe -> recipe.getPatterns().stream()
                                .anyMatch(pattern -> pattern.equalsDefault(toMatch))))
                .collect(Collectors.toList());

        return matching;
    }

    //INFORMATIONSE flexLayout.add(Components...) <- List: index in der list ist die reihenfolge der items im bound
    /**
     * Adds a {@link ComponentBox} containing a collection of components to a specific course row at the specified index.
     * If a component box already exists at the given index, it and all subsequent component boxes are shifted one position upwards to make space for the new one.
     *
     * @param course the {@link Course} object to which the component box will be bound
     * @param items the collection of {@link de.bauersoft.data.entities.Component} to be added to the component box
     * @param index the index in the course row at which the component box will be inserted
     * @return the newly created {@link ComponentBox} instance containing the provided components
     */
    private ComponentBox addComponentBox(Course course, Collection<de.bauersoft.data.entities.Component> items, int index)
    {
        Objects.requireNonNull(course , "course cannot be null.");
        Objects.requireNonNull(items , "items cannot be null.");

        ComponentBox componentBox = new ComponentBox(course);
        componentBox.setItems(items);

        addElseSet(sectionComponents.get(course), index, componentBox);
        return componentBox;
    }

    /**
     * Adds an element at the specified position in a copy of the given list.
     * If the index is equal to the size of the list, the element will be added at the end.
     * All subsequent elements will be shifted one position to the right.
     *
     * @param collection the collection to which the element will be added
     * @param index the position in the collection where the element will be added
     * @param element the element to add
     * @return a copy of the original collection with the added element
     * @param <E> the type of the element
     * @throws IndexOutOfBoundsException if index < 0 or index > size
     */
    private <E> List<E> addElseSet(List<E> collection, int index, E element)
    {
        List<E> copy = new ArrayList<>(collection);
        if(index < 0 || index > copy.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);

        if(index == copy.size())
        {
            copy.add(element);

        }else copy.add(index, element);

        return copy;
    }

}
