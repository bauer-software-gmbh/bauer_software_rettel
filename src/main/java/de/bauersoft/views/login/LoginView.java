package de.bauersoft.views.login;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.security.AuthenticatedUser;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
@CssImport("./themes/rettels/views/login.css")
public class LoginView extends Div implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final LoginForm loginForm = new LoginForm();
    
    public LoginView(AuthenticatedUser authenticatedUser) {
        this.setClassName("login_view");
        
    	this.authenticatedUser = authenticatedUser;
    	LoginI18n i18n = LoginI18n.createDefault();
    	LoginI18n.Form i18nForm = i18n.getForm();
    	i18nForm.setTitle("Rettel's");
    	i18n.setForm(i18nForm);
        i18n.setAdditionalInformation(null);
        
    	loginForm.setI18n(i18n);
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        
        loginForm.setClassName("login-form");
        this.add(loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
        	loginForm.setEnabled(false);
            event.forwardTo("");
        }

        loginForm.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
