package de.bauersoft.views.welcome;

// import com.vaadin.flow.component.html.Image;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.views.MainLayout;

@PageTitle("Willkommen")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        setSpacing(false);
        setPadding(false);
        setMargin(false);
        

        setSizeFull();

    
    }
   
}
