package de.bauersoft.views.closingTime;

import com.vaadin.flow.component.html.Div;
import de.bauersoft.data.entities.user.User;
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

    private final InstitutionTabSheet institutionTabSheet;

    public ClosingTimeManager(ClosingTimeView closingTimeView, AuthenticatedUser authenticatedUser, User user, InstitutionClosingTimeService closingTimeService, InstitutionService institutionService)
    {
        this.closingTimeView = closingTimeView;
        this.authenticatedUser = authenticatedUser;
        this.user = user;
        this.closingTimeService = closingTimeService;
        this.institutionService = institutionService;

        institutionTabSheet = new InstitutionTabSheet(this);

        this.add(institutionTabSheet);

        this.setWidthFull();
        this.setHeightFull();
    }
}
