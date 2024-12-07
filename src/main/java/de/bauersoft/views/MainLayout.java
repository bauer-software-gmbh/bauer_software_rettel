package de.bauersoft.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.bauersoft.data.entities.User;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.views.additive.AdditiveView;
import de.bauersoft.views.address.AddressView;
import de.bauersoft.views.allergen.AllergenView;
import de.bauersoft.views.component.ComponentView;
import de.bauersoft.views.course.CourseView;
import de.bauersoft.views.dashboard.DashboardView;
import de.bauersoft.views.field.FieldView;
import de.bauersoft.views.incredient.IngredientView;
import de.bauersoft.views.institution.InstitutionView;
import de.bauersoft.views.menuBuilder.MenuBuilderView;
import de.bauersoft.views.menue.MenueView;
import de.bauersoft.views.pattern.PatternView;
import de.bauersoft.views.recipe.RecipeView;
import de.bauersoft.views.unit.UnitView;
import de.bauersoft.views.users.UsersView;
import de.bauersoft.views.welcome.WelcomeView;

import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./themes/rettels/views/main.css")
public class MainLayout extends AppLayout
{
	private H1 viewTitle;
	private AuthenticatedUser authenticatedUser;
	private AccessAnnotationChecker accessChecker;

	public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker)
	{
		this.authenticatedUser = authenticatedUser;
		this.accessChecker = accessChecker;
		this.setClassName("main-view");
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
		this.setDrawerOpened(false);
	}

	private void addHeaderContent()
	{
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");
		viewTitle = new H1();
		viewTitle.setSizeFull();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		Optional<User> maybeUser = authenticatedUser.get();
		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().setAlignItems(AlignItems.CENTER);
		layout.setWidthFull();
		layout.add(viewTitle);
		if (maybeUser.isPresent()) {
			User user = maybeUser.get();
			Avatar avatar = new Avatar(user.getName() + " " + user.getSurname());
			avatar.setThemeName("xsmall");
			avatar.getElement().setAttribute("tabindex", "-1");
			MenuBar userMenu = new MenuBar();
			userMenu.setThemeName("tertiary-inline contrast");
			MenuItem userName = userMenu.addItem("");
			Div div = new Div();
			div.add(avatar);
			div.add(user.getName());
			div.add(new Icon("lumo", "dropdown"));
			div.getElement().getStyle().set("display", "flex");
			div.getElement().getStyle().set("align-items", "center");
			div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
			userName.add(div);
			userName.getSubMenu().addItem("Sign out", e -> {
				authenticatedUser.logout();
			});
			layout.add(userMenu);
		} else {
			Avatar avatar = new Avatar(" ");
			avatar.setThemeName("xsmall");
			avatar.getElement().setAttribute("tabindex", "-1");
			Span login = new Span("login");
			login.getStyle().setPadding("0 0 0 1rem");
			Anchor anchor = new Anchor("login", avatar, login);
			anchor.getStyle().setDisplay(Display.FLEX);
			anchor.getStyle().setPadding("0 1rem 0 0");
			layout.add(anchor);
		}
		addToNavbar(true, toggle, layout);
	}

	private void addDrawerContent() {
		Image image = new Image("./icons/icon.png", "Rettels");
		Span appName = new Span("Rettel's");
		appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
		Header header = new Header(image, appName);
		Scroller scroller = new Scroller(createNavigation());
		addToDrawer(header, scroller, createFooter());
	}

	private SideNav createNavigation() {
		SideNav nav = new SideNav();
		if (accessChecker.hasAccess(WelcomeView.class)) {
			nav.addItem(new SideNavItem("welcome", WelcomeView.class, LineAwesomeIcon.HOME_SOLID.create()));
		}
		if (accessChecker.hasAccess(MenueView.class)) {
			nav.addItem(new SideNavItem("menue", MenueView.class, LineAwesomeIcon.UTENSILS_SOLID.create()));
		}
		if (accessChecker.hasAccess(DashboardView.class)) {
			nav.addItem(new SideNavItem("dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));
		}
		if (accessChecker.hasAccess(UsersView.class) || accessChecker.hasAccess(FieldView.class)
				|| accessChecker.hasAccess(InstitutionView.class) || accessChecker.hasAccess(AddressView.class))
		{
			SideNavItem accounting = new SideNavItem("accounting");
			if (accessChecker.hasAccess(UsersView.class)) {
				accounting.addItem(new SideNavItem("users", UsersView.class, LineAwesomeIcon.USERS_SOLID.create()));
			}
			if (accessChecker.hasAccess(InstitutionView.class)) {
				accounting.addItem(
						new SideNavItem("institution", InstitutionView.class, LineAwesomeIcon.USERS_SOLID.create()));
			}
			if (accessChecker.hasAccess(AddressView.class)) {
				accounting.addItem(new SideNavItem("address", AddressView.class, LineAwesomeIcon.USERS_SOLID.create()));
			}
			if (accessChecker.hasAccess(FieldView.class)) {
				accounting.addItem(new SideNavItem("field", FieldView.class, LineAwesomeIcon.USERS_SOLID.create()));
			}
			nav.addItem(accounting);
		}
		if (accessChecker.hasAccess(ComponentView.class) || accessChecker.hasAccess(RecipeView.class)
				|| accessChecker.hasAccess(IngredientView.class) || accessChecker.hasAccess(CourseView.class)
				|| accessChecker.hasAccess(AllergenView.class) || accessChecker.hasAccess(AdditiveView.class)
				|| accessChecker.hasAccess(UnitView.class) || accessChecker.hasAccess(PatternView.class)
				|| accessChecker.hasAccess(MenuBuilderView.class)) {
			SideNavItem backend = new SideNavItem("backend");

			if(accessChecker.hasAccess(MenuBuilderView.class))
				backend.addItem(new SideNavItem("menue builder", MenuBuilderView.class, LineAwesomeIcon.BACON_SOLID.create()));

			if (accessChecker.hasAccess(ComponentView.class)) {
				backend.addItem(
						new SideNavItem("component", ComponentView.class, LineAwesomeIcon.CARROT_SOLID.create()));
			}
			if (accessChecker.hasAccess(RecipeView.class)) {
				backend.addItem(new SideNavItem("recipe", RecipeView.class, LineAwesomeIcon.EDIT.create()));
			}
			if (accessChecker.hasAccess(IngredientView.class)) {
				backend.addItem(
						new SideNavItem("incredient", IngredientView.class, LineAwesomeIcon.CARROT_SOLID.create()));
			}
			
			if (accessChecker.hasAccess(CourseView.class) || accessChecker.hasAccess(AllergenView.class)
					|| accessChecker.hasAccess(AdditiveView.class) || accessChecker.hasAccess(UnitView.class)
					|| accessChecker.hasAccess(PatternView.class)) {
				SideNavItem parameters = new SideNavItem("parameters");
				if (accessChecker.hasAccess(CourseView.class)) {
					parameters.addItem(
							new SideNavItem("course", CourseView.class, LineAwesomeIcon.STREAM_SOLID.create()));
				}
				if (accessChecker.hasAccess(PatternView.class)) {
					parameters.addItem(new SideNavItem("pattern", PatternView.class, LineAwesomeIcon.CARROT_SOLID.create()));
				}
				if (accessChecker.hasAccess(AllergenView.class)) {
					parameters.addItem(new SideNavItem("allergen", AllergenView.class,
							LineAwesomeIcon.CLOUD_MEATBALL_SOLID.create()));
				}
				if (accessChecker.hasAccess(AdditiveView.class)) {
					parameters.addItem(new SideNavItem("additive", AdditiveView.class,
							LineAwesomeIcon.FOLDER_PLUS_SOLID.create()));
				}
				if (accessChecker.hasAccess(UnitView.class)) {
					parameters.addItem(new SideNavItem("unit", UnitView.class, LineAwesomeIcon.RULER_SOLID.create()));
				}
				backend.addItem(parameters);
			}
			
			nav.addItem(backend);
			
			nav.addItem(new SideNavItem("offer", OffersView.class, LineAwesomeIcon.COFFEE_SOLID.create()));
		}
		return nav;
	}

	private Footer createFooter() {
		Footer layout = new Footer();
		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}
}
