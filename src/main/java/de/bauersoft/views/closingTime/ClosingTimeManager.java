package de.bauersoft.views.closingTime;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.InstitutionClosingTimeDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.closingTime.institutionLayer.InstitutionTabSheet;
import lombok.Getter;

@Getter
public class ClosingTimeManager extends Div
{
    private final ClosingTimeView closingTimeView;
    private final AuthenticatedUser authenticatedUser;
    private final User user;

    private final InstitutionClosingTimeService closingTimeService;
    private final InstitutionService institutionService;

    private InstitutionTabSheet institutionTabSheet;

    public ClosingTimeManager(ClosingTimeView closingTimeView, AuthenticatedUser authenticatedUser, User user, InstitutionClosingTimeService closingTimeService, InstitutionService institutionService)
    {
        this.closingTimeView = closingTimeView;
        this.authenticatedUser = authenticatedUser;
        this.user = user;
        this.closingTimeService = closingTimeService;
        this.institutionService = institutionService;

        this.setWidthFull();
        this.setHeightFull();

        if(!user.getRoles().contains(Role.CLOSING_TIMES_SHOW_ALL_INSTITUTIONS) &&
                !user.getRoles().contains(Role.ADMIN) &&
                getInstitutionService().findAllByUsersId(getUser().getId()).size() < 1)
        {
            Span div = new Span("Ihr Account ist noch nicht mit einer Institution verknüpft!");
            div.getStyle()
                    .set("margin", "var(--lumo-space-l)");

            this.add(div);
            return;
        }

        institutionTabSheet = new InstitutionTabSheet(this);

        this.add(institutionTabSheet);
    }
}
