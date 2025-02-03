package de.bauersoft.views.menue;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import de.bauersoft.data.entities.field.DefaultField;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.FieldService;
import de.bauersoft.views.MainLayout;
import de.bauersoft.views.field.FieldView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("menue")
@Route(value = "menue", layout = MainLayout.class)
@AnonymousAllowed
public class MenueView extends Div
{

    private final FieldService fieldService;

    private ComboBox<Field> fieldsComboBox;

    private H2 header;
    private Paragraph description;
    private Paragraph error;
    private Div pdfContainer;

    public MenueView(FieldService fieldService)
    {
        this.fieldService = fieldService;

        // Root layout styles
        setClassName("content");
        setSizeFull();
        getStyle().set("display", "flex");
        getStyle().set("flex-direction", "column");
        getStyle().set("align-items", "center");
        getStyle().set("justify-content", "flex-start");

        List<Field> fields = fieldService.getRepository().findAll().stream()
                .filter(field ->
                {
                    return DefaultField.KINDERGARTEN.equalsDefault(field)
                            || DefaultField.KINDERTAGESSTÄTTE.equalsDefault(field)
                            || DefaultField.GRUNDSCHULE.equalsDefault(field);

                }).collect(Collectors.toList());

        fieldsComboBox = new ComboBox<>();
        fieldsComboBox.setItems(fields);
        fieldsComboBox.setItemLabelGenerator(Field::getName);

        this.add(fieldsComboBox);

        fieldsComboBox.addValueChangeListener(event ->
        {
            if(header != null)
                this.remove(header);

            if(description != null)
                this.remove(description);

            if(error != null)
                this.remove(error);

            if(pdfContainer != null)
                this.remove(pdfContainer);

            Field field = event.getValue();

            header = new H2("Speiseplan der " + field.getName());
            header.addClassNames(Margin.Top.NONE, Margin.Bottom.MEDIUM);
            add(header);

            description = new Paragraph("Hier finden Sie den aktuellen Speiseplan der " + field.getName());
            description.getStyle().set("margin-bottom", "20px");
            add(description);

            File pdf = new File(CreateMenuPdf.pdfOutputPath.replace("%field%", field.getName()));
            if(!pdf.exists())
            {
                error = new Paragraph("Es wurde leider noch kein Speiseplan erstellt!");
                description.getStyle().set("margin-bottom", "20px");
                add(error);

                return;
            }

            StreamResource streamResource = new StreamResource(pdf.getName(), () ->
            {
                try
                {
                    return new FileInputStream(pdf);

                }catch(Exception e)
                {
                    e.printStackTrace();

                    error = new Paragraph("Es ist ein Fehler aufgetreten!");
                    error.add(new HtmlComponent("br"), new Text(e.getMessage()));
                    description.getStyle().set("margin-bottom", "20px");
                    add(error);

                    return null;
                }
            });

            PdfViewer pdfViewer = new PdfViewer();
            pdfViewer.setSrc(streamResource);
            pdfViewer.setHeight("100%");
            pdfViewer.setWidth("100%");
            pdfViewer.getElement().setAttribute("type", "application/pdf");

            pdfContainer  = new Div(pdfViewer);
            pdfContainer.setWidth("90%"); // Verhindert horizontales Scrollen
            pdfContainer.setHeight("calc(100% - 100px)"); // Passt Höhe an (reduziert um Header-Bereich)
            pdfContainer.getStyle().set("overflow", "hidden");
            add(pdfContainer);
        });

        if(fields.size() > 0)
            fieldsComboBox.setValue(fields.get(0));
    }
}
