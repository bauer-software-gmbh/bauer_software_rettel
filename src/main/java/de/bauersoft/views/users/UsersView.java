package de.bauersoft.views.users;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.UserDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.UserService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("Benutzer")
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

        grid.addColumn("name").setHeader("Vorname");
        grid.addColumn("surname").setHeader("Nachname");
        grid.addColumn("email").setHeader("E-Mail");

        grid.addItemDoubleClickListener(event ->
		{
			new UserDialog(userService, userDataProvider, event.getItem(), DialogState.EDIT, passwordEncoder, authenticatedUser);
		});

        GridContextMenu<User> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neuer Benutzer", event ->
        {
            new UserDialog(userService, userDataProvider, new User(), DialogState.NEW, passwordEncoder, authenticatedUser);
        });

        GridMenuItem<User> deleteItem = contextMenu.addItem("Löschen", event -> {
            event.getItem().ifPresent(item -> {
                userService.delete(item.getId());
                userDataProvider.refreshAll();
            });
        });

        contextMenu.addGridContextMenuOpenedListener(event ->
        {
            deleteItem.setVisible(event.getItem().isPresent());
        });

        this.add(grid);
    }
}
