package de.bauersoft.views.offers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.views.DialogState;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OffersDialog_OLD extends Dialog {

    public OffersDialog_OLD(DialogState state, MenuRepository menuRepository) {
        this.setHeaderTitle("Offer-Builder");
        // this.setHeaderTitle(state.toString());
        FormLayout inputLayout = new FormLayout();
        inputLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        setClassName("content");

        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        HorizontalLayout kwOptionsHorizontalLayout = new HorizontalLayout();
        kwOptionsHorizontalLayout.setClassName("kw-options");

        LocalDate today = LocalDate.now();
        Integer calenderWeek = today.get(WeekFields.of(Locale.GERMAN).weekOfWeekBasedYear());
        Span kw = new Span("KW: " + calenderWeek.toString());
        Button plus = new Button();
        plus.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());
        Button minus = new Button();
        minus.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());

        kwOptionsHorizontalLayout.add(minus, kw, plus);

        HorizontalLayout contentHorizontalLayout = new HorizontalLayout();
        contentHorizontalLayout.setSizeFull();
        List<Div> days = getDivs();

        contentHorizontalLayout.add(days.toArray(new Div[0]));

        VirtualList<Menu> virtualList = new VirtualList<>();
        virtualList.setRenderer(new ComponentRenderer<Div,Menu>(component->{
            OffersDialog_OLD.MenuDiv container = new OffersDialog_OLD.MenuDiv(component);
            DragSource.create(container);
            return container;
        }));
        List<Menu> list = menuRepository.findAll();
        ListDataProvider<Menu> provider = new ListDataProvider<>(list);

        DropTarget.create(virtualList).addDropListener(event-> event.getDragSourceComponent().ifPresent(source->{
                    if(source instanceof OffersDialog_OLD.MenuDiv menuDiv) {
                        System.out.println(menuDiv.getItem().getId());
                    }
                }
        ));
        virtualList.setDataProvider(provider);
        virtualList.setSizeFull();
        contentHorizontalLayout.add(virtualList);
        pageVerticalLayout.add(kwOptionsHorizontalLayout);
        pageVerticalLayout.add(contentHorizontalLayout);
        this.add(pageVerticalLayout);
    }

    /**
     * @return Die Hilfmethode gibt eine Liste mit 7 DropTarget-Elementen zurück (stellvertretend für die 7 Tage der Woche)
     */
    private static List<Div> getDivs() {
        List<Div> days = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Div day = new Div();
            day.setClassName("offer_target");
            days.add(day);
        }

        days.forEach(day -> {
            DropTarget<Div> dropTarget = DropTarget.create(day);
            dropTarget.addDropListener(event -> event.getDragSourceComponent().ifPresent(source -> {
                if (source instanceof OffersDialog_OLD.MenuDiv menuDiv) {
                    System.out.println(menuDiv.getItem().getName());
                    OffersDialog_OLD.MenuDiv copy = new OffersDialog_OLD.MenuDiv(menuDiv.getItem());
                    event.getComponent().add(copy);
                }
            }));
        });
        return days;
    }

    private static class MenuDiv extends Div {
        private final Menu item;

        public MenuDiv(Menu item ) {
            this.item = item;
            Span name = new Span(item.getName());
            this.add(name);
        }

        public Menu getItem() {
            return item;
        }
    }
}
