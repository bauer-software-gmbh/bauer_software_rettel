package de.bauersoft.views.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.UserDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.UserService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("user")
@Route(value = "user", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Div
{
    private final AutoFilterGrid<User> grid = new AutoFilterGrid<>(User.class, false, true);

    private UserService userService;
    private UserDataProvider userDataProvider;
    private PasswordEncoder passwordEncoder;
    private AuthenticatedUser authenticatedUser;

    public UsersView(UserService userService, UserDataProvider userDataProvider, PasswordEncoder passwordEncoder, AuthenticatedUser authenticatedUser)
    {
        this.userService = userService;
        this.userDataProvider = userDataProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;

        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(userDataProvider);

        grid.addColumn("name");
        grid.addColumn("surname");
        grid.addColumn("email");

        grid.addItemDoubleClickListener(event ->
		{
			new UserDialog(userService, userDataProvider, event.getItem(), DialogState.EDIT, passwordEncoder, authenticatedUser);
		});

        GridContextMenu<User> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new", event ->
        {
            new UserDialog(userService, userDataProvider, new User(), DialogState.NEW, passwordEncoder, authenticatedUser);
        });

        contextMenu.addItem("delete", event -> event.getItem().ifPresent(item ->
        {
            userService.deleteById(item.getId());
            userDataProvider.refreshAll();
        }));

        this.add(grid);
    }

    private Dialog createDialog(UserService userService, UserDataProvider dataProvider, User item, DialogState state)
    {
        Binder<User> binder = new Binder<>(User.class);
        Dialog inputDialog = new Dialog();
        inputDialog.setHeaderTitle(DialogState.NEW.equals(state) ? "new" : "edit" + " user");
        FormLayout inputLayout = new FormLayout();
        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(50);
        nameTextField.setRequired(true);
        nameTextField.setMinWidth("20em");
        TextField surnameTextField = new TextField();
        surnameTextField.setMaxLength(50);
        surnameTextField.setRequired(true);
        surnameTextField.setMinWidth("20em");
        EmailField emailField = new EmailField();
        emailField.setMaxLength(128);
        emailField.setRequired(true);
        emailField.setMinWidth("20em");
        MultiSelectComboBox<Role> roleMultiSelectComboBox = new MultiSelectComboBox<Role>();
        roleMultiSelectComboBox.setItems(Role.values());
        roleMultiSelectComboBox.setSizeFull();
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxLength(60);
        passwordField.setRequired(true);
        passwordField.setMinWidth("20em");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setMaxLength(60);
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setMinWidth("20em");
        Button changePasswordButton = new Button("change Password");
        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(surnameTextField, "surname"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(emailField, "email"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(roleMultiSelectComboBox, "role"), 1);
        FormItem passwordColumn = inputLayout.addFormItem(passwordField, "password");
        inputLayout.setColspan(passwordColumn, 1);
        FormItem confirmPasswordColumn = inputLayout.addFormItem(confirmPasswordField, "password");
        inputLayout.setColspan(confirmPasswordColumn, 1);
        FormItem changePasswordColumn = inputLayout.addFormItem(changePasswordButton, "");
        inputLayout.setColspan(changePasswordColumn, 1);
        passwordColumn.setVisible(DialogState.NEW.equals(state));
        confirmPasswordColumn.setVisible(DialogState.NEW.equals(state));
        changePasswordColumn.setVisible(!DialogState.NEW.equals(state));
        changePasswordButton.addClickListener(event ->
        {
            passwordColumn.setVisible(true);
            confirmPasswordColumn.setVisible(true);
            changePasswordColumn.setVisible(false);
        });
        binder.bind(nameTextField, "name");
        binder.bind(surnameTextField, "surname");
        binder.bind(emailField, "email");
        binder.bind(roleMultiSelectComboBox, "roles");
        binder.forField(passwordField).bind((user) -> DialogState.NEW.equals(state) ? "" : "123456",
                (user, value) -> user.setPassword(value != "123456" ? passwordEncoder.encode(value) : user.getPassword()));
        passwordField.setRevealButtonVisible(DialogState.NEW.equals(state));
        binder.forField(confirmPasswordField)
                .withValidator(value -> value.equals(passwordField.getValue()), "password not matching!")
                .bind((user) -> DialogState.NEW.equals(state) ? "" : "123456", (user, value) -> user
                        .setPassword(value != "123456" ? passwordEncoder.encode(value) : user.getPassword()));
        confirmPasswordField.setRevealButtonVisible(DialogState.NEW.equals(state));
        binder.setBean(item);
        Button saveButton = new Button("save");
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            if(binder.isValid())
            {
                userService.update(binder.getBean());
                dataProvider.refreshAll();
                Notification.show("Data updated");
                inputDialog.close();
            }
        });
        Button cancelButton = new Button("cancel");
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            inputDialog.close();
        });
        inputLayout.setWidth("50vw");
        inputLayout.setMaxWidth("50em");
        inputLayout.setHeight("50vh");
        inputLayout.setMaxHeight("18em");
        Span spacer = new Span();
        spacer.setWidthFull();
        inputDialog.add(inputLayout);
        inputDialog.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
        inputDialog.setCloseOnEsc(false);
        inputDialog.setCloseOnOutsideClick(false);
        inputDialog.setModal(true);
        inputDialog.open();
        return inputDialog;
    }
}
