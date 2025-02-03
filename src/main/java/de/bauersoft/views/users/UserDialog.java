package de.bauersoft.views.users;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.UserDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.UserService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

public class UserDialog extends Dialog
{

    public static final Pattern passwortRegex;

    static
    {
        passwortRegex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\W]{8,}$");
    }

    private UserService userService;
    private UserDataProvider dataProvider;
    private User item;
    private DialogState state;
    private PasswordEncoder encoder;
    private AuthenticatedUser authenticatedUser;

    public UserDialog(UserService userService,
                      UserDataProvider dataProvider,
                      User item,
                      DialogState state,
                      PasswordEncoder encoder,
                      AuthenticatedUser authenticatedUser)
    {
        this.userService = userService;
        this.dataProvider = dataProvider;
        this.item = item;
        this.state = state;
        this.encoder = encoder;

        this.setHeaderTitle(state.toString());

        Binder<User> binder = new Binder<>(User.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setWidth("50vw");
        inputLayout.setMaxWidth("50em");
        inputLayout.setHeight("50vh");
        inputLayout.setMaxHeight("18em");

        inputLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

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

        FormLayout.FormItem passwordFieldItem = inputLayout.addFormItem(passwordField, "Passwort");
        inputLayout.setColspan(passwordFieldItem, 1);

        FormLayout.FormItem confirmPasswordFieldItem = inputLayout.addFormItem(confirmPasswordField, "Passwort");
        inputLayout.setColspan(confirmPasswordFieldItem, 1);

        FormLayout.FormItem changePasswordButtonItem = inputLayout.addFormItem(changePasswordButton, "");
        inputLayout.setColspan(changePasswordButtonItem, 1);

        passwordFieldItem.setVisible(DialogState.NEW.equals(state));
        confirmPasswordFieldItem.setVisible(DialogState.NEW.equals(state));
        changePasswordButtonItem.setVisible(!DialogState.NEW.equals(state));

        changePasswordButtonItem.addClickListener(event ->
        {
            passwordFieldItem.setVisible(true);
            confirmPasswordFieldItem.setVisible(true);
            changePasswordButtonItem.setVisible(false);
        });

        //passwordField.setRevealButtonVisible(DialogState.NEW.equals(state) || (authenticatedUser.get().isPresent() && authenticatedUser.get().get().equals(item)));
        //confirmPasswordField.setRevealButtonVisible(DialogState.NEW.equals(state) || (authenticatedUser.get().isPresent() && authenticatedUser.get().get().equals(item)));
        passwordField.setRevealButtonVisible(true);
        confirmPasswordField.setRevealButtonVisible(true);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name is required");
        }).bind("name");

        binder.forField(surnameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Surname is required");
        }).bind("surname");

        binder.forField(emailField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Email is required");
        }).bind("email");

        binder.bind(roleMultiSelectComboBox, "roles");

        binder.forField(passwordField).asRequired((value, context) ->
        {
            return (passwortRegex.matcher(value).matches())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Das Passwort muss mindestens acht Zeichen lang sein, mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten.");

        }).bind((user) -> null,
                (user, value) -> user.setPassword(encoder.encode(value)));

        binder.forField(confirmPasswordField).asRequired((value, context) ->
        {
            if(!confirmPasswordField.getValue().equals(passwordField.getValue()))
                return ValidationResult.error("Die Passwörter stimmen nicht überein!");

            return (passwortRegex.matcher(value).matches())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Das Passwort muss mindestens acht Zeichen lang sein, mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten.");
        }).bind((user) -> null,
                (user, value) -> user.setPassword(encoder.encode(value)));

        binder.setBean(item);

        Button saveButton = new Button("save");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            binder.validate();
            if(binder.isValid())
            {
                try
                {
                    userService.update(binder.getBean());
                    dataProvider.refreshAll();
                    Notification.show("Data updated");
                    this.close();

                }catch(DataIntegrityViolationException error)
                {
                    Notification.show("Duplicate entry", 5000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        Button cancelButton = new Button("cancel");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            this.close();
        });

        Span spacer = new Span();
        spacer.setWidthFull();

        this.add(inputLayout);
        this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
        this.open();
    }
}
