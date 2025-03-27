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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.UserDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.UserService;
import de.bauersoft.views.DialogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

public class UserDialog extends Dialog
{
    public static final Pattern passwortRegex;
    private static final Logger logger = LoggerFactory.getLogger(UserDialog.class);

    static
    {
        passwortRegex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\W]{8,}$");
    }

    private final UserService userService;
    private final UserDataProvider userDataProvider;
    private final User item;
    private final DialogState state;
    private final PasswordEncoder encoder;
    private AuthenticatedUser authenticatedUser;
    private boolean isChangingPassword = false;

    public UserDialog(UserService userService,
                      UserDataProvider userDataProvider,
                      User item,
                      DialogState state,
                      PasswordEncoder encoder,
                      AuthenticatedUser authenticatedUser)
    {
        this.userService = userService;
        this.userDataProvider = userDataProvider;
        this.item = item;
        this.state = state;
        this.encoder = encoder;

        this.setHeaderTitle(state.toString());

        Binder<User> binder = new Binder<>(User.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setWidth("30rem");
        inputLayout.getElement().setAttribute("autocomplete", "off");

        inputLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(50);
        nameTextField.setAutofocus(true);
        nameTextField.setRequired(true);
        nameTextField.setWidthFull();

        TextField surnameTextField = new TextField();
        surnameTextField.setMaxLength(50);
        surnameTextField.setRequired(true);
        surnameTextField.setWidthFull();

        EmailField emailField = new EmailField();
        emailField.setMaxLength(128);
        emailField.setRequired(true);
        emailField.setWidthFull();

        MultiSelectComboBox<Role> roleMultiSelectComboBox = new MultiSelectComboBox<>();
        roleMultiSelectComboBox.setWidthFull();
        roleMultiSelectComboBox.setItems(Role.values());

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxLength(60);
        passwordField.setRequired(true);
        passwordField.setWidthFull();

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setMaxLength(60);
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setWidthFull();

        passwordField.getElement().setAttribute("autocomplete", "new-password");
        confirmPasswordField.getElement().setAttribute("autocomplete", "new-password");


        Button changePasswordButton = new Button("Password ändern");

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Vorname"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(surnameTextField, "Nachname"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(emailField, "E-Mail"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(roleMultiSelectComboBox, "Rolle"), 1);

        FormLayout.FormItem passwordFieldItem = inputLayout.addFormItem(passwordField, "Passwort");
        inputLayout.setColspan(passwordFieldItem, 1);

        FormLayout.FormItem confirmPasswordFieldItem = inputLayout.addFormItem(confirmPasswordField, "Passwort wdh.");
        inputLayout.setColspan(confirmPasswordFieldItem, 1);

        FormLayout.FormItem changePasswordButtonItem = inputLayout.addFormItem(changePasswordButton, "");
        inputLayout.setColspan(changePasswordButtonItem, 1);

        passwordFieldItem.setVisible(DialogState.NEW.equals(state));
        confirmPasswordFieldItem.setVisible(DialogState.NEW.equals(state));
        changePasswordButtonItem.setVisible(!DialogState.NEW.equals(state));

        changePasswordButtonItem.addClickListener(event ->
        {
            isChangingPassword = true;
            passwordFieldItem.setVisible(true);
            confirmPasswordFieldItem.setVisible(true);
            changePasswordButtonItem.setVisible(false);
        });

        passwordField.setRevealButtonVisible(true);
        confirmPasswordField.setRevealButtonVisible(true);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Vorname ist erforderlich");
        }).bind("name");

        binder.forField(surnameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Nachname ist erforderlich");
        }).bind("surname");

        binder.forField(emailField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("E-Mail ist erforderlich");
        }).bind("email");

        binder.bind(roleMultiSelectComboBox, "roles");

        binder.forField(passwordField).asRequired((value, context) ->
        {
            if(!isChangingPassword && !DialogState.NEW.equals(state))
            {
                return ValidationResult.ok();
            }
            return (passwortRegex.matcher(value).matches())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Das Passwort muss mindestens acht Zeichen lang sein, mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten.");

        }).bind((user) -> null,
                (user, value) ->
                {
                    if(isChangingPassword || DialogState.NEW.equals(state))
                    {
                        user.setPassword(encoder.encode(value));
                    }
                });

        binder.forField(confirmPasswordField).asRequired((value, context) ->
        {
            if(!isChangingPassword)
            {
                return ValidationResult.ok();
            }
            if(!confirmPasswordField.getValue().equals(passwordField.getValue()))
            {
                return ValidationResult.error("Die Passwörter stimmen nicht überein!");
            }

            return (passwortRegex.matcher(value).matches())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Das Passwort muss mindestens acht Zeichen lang sein, mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten.");
        }).bind((user) -> null,
                (user, value) ->
                {
                    if(isChangingPassword || DialogState.NEW.equals(state))
                    {
                        user.setPassword(encoder.encode(value));
                    }
                });

        binder.setBean(item);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            logger.info("User Object before saving: {}", binder.getBean().toString());

            logger.info("Save button clicked!");
            binder.validate();
            if(binder.isValid())
            {
                try
                {
                    logger.info("Form is valid, saving...");
                    userService.update(binder.getBean());
                    userDataProvider.refreshAll();
                    Notification.show("Daten wurden aktualisiert");
                    this.close();

                }catch(DataIntegrityViolationException error)
                {
                    logger.error("Duplicate entry error!");
                    Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }else
            {
                logger.error("Form invalidation failed!");
            }
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            userDataProvider.refreshAll();
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
