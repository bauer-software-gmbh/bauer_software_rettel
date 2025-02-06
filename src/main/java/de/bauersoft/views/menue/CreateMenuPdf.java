package de.bauersoft.views.menue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;

public class CreateMenuPdf
{

    public static final String resourceFileName = "SpeiseplanTemplate.pdf";
    public static final String pdfTemplatePath = "C:/Rettel/PDF/" + resourceFileName;
    public final static String pdfOutputPath = "C:/Rettel/PDF/%field%.pdf";

    public static void generatePdf(List<Map<String, Object>> menuList, String fieldName)
    {
        Objects.requireNonNull(fieldName, "fieldName cannot be null");
        if(fieldName.isBlank())
            throw new IllegalArgumentException("fieldName cannot be empty");

        String outputPath = pdfOutputPath.replace("%field%", fieldName);

        File pdfTemplate = new File(pdfTemplatePath);
        if(!pdfTemplate.exists())
        {
            //File resource = new File(CreateMenuPdf.class.getClassLoader().getResource(resourceFileName).getFile(), "UTF-8");

            pdfTemplate.getParentFile().mkdirs();
            try (InputStream inputStream = CreateMenuPdf.class.getClassLoader().getResourceAsStream(resourceFileName)) {
                if (inputStream == null) {
                    throw new IOException("Vorlagendatei " + resourceFileName + " nicht gefunden.");
                }
                Files.copy(inputStream, Path.of(pdfTemplatePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }

        if(!pdfTemplate.exists())
        {
            System.err.println("Die Quelldatei wurde nicht gefunden.");
            return;
        }

        try(PDDocument document = Loader.loadPDF(pdfTemplate))
        {
            fillPlan(document, menuList);
            document.save(outputPath);
            System.out.println("PDF gespeichert unter: " + outputPath);

        }catch(IOException e)
        {
            e.printStackTrace();
            System.err.println("Fehler beim Bearbeiten der PDF!");
        }
    }

    private static void fillPlan(PDDocument document, List<Map<String, Object>> entries) throws IOException
    {
        int maxEntriesPerPage = 10; // 10 Sätze pro Seite
        int maxEntries = 20; // Maximal 20 Sätze in der PDF

        if(entries.size() > maxEntries)
        {
            System.out.println("Warnung: Es werden nur die ersten " + maxEntries + " Einträge berücksichtigt.");
        }

        int entryCounter = 0;
        int[] baseXPositions = {75, 296}; // Zwei mögliche X-Positionen (erste und zweite Spalte)
        int[] baseYPositions = {185, 306, 428, 549, 670}; // Positionen für Zeilen

        for(int pageIndex = 0; pageIndex < 2; pageIndex++)
        { // Genau 2 vorhandene Seiten verwenden
            if(entryCounter >= entries.size())
            {
                break; // Keine weiteren Einträge mehr
            }

            PDPage page = document.getPage(pageIndex);
            try(PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false))
            {
                contentStream.setNonStrokingColor(0, 0, 0); // Schriftfarbe auf Schwarz setzen

                for(int i = 0; i < maxEntriesPerPage; i++)
                { // Maximal 10 Einträge pro Seite
                    if(entryCounter >= entries.size())
                    {
                        break; // Keine weiteren Einträge mehr
                    }

                    int column = entryCounter % 10 < 5 ? 0 : 1; // Erste oder zweite Spalte
                    int row = entryCounter % 5; // Zeilenindex (0 bis 4)

                    int baseX = baseXPositions[column];
                    int baseY = baseYPositions[row];

                    Map<String, Object> entry = entries.get(entryCounter);

                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

                    // Datum (day)
                    contentStream.beginText();
                    contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(90), baseX - 1, baseY + 29));
                    contentStream.showText(truncateText(entry.get("day")));
                    contentStream.endText();

                    // Menu (menu)
                    Object menu = entry.get("menu");
                    String menuText = (menu == null || menu.toString().isEmpty()) ? "kein Menu" : menu.toString();
                    // Zeilen umbrechen
                    String[] menuWrT = WordUtils.wrap(menuText, 25).split("\r?\n");
                    // Text um 90 Grad gedreht anzeigen
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                    contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(90), baseX + 22, baseY));

                    for (String line : menuWrT) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -10); // Abstand zwischen den Zeilen
                    }

                    contentStream.endText();


                    // Typ (type) - "×" in passendes Kästchen setzen
                    String type = entry.get("type").toString();
                    switch(type)
                    {
                        case "Vegetarisch" -> drawCheckmark(contentStream, baseX + 115, baseY + 9);
                        case "Rindfleisch" ->
                        {
                            if(entryCounter == 4 || entryCounter == 9 || entryCounter == 14 || entryCounter == 19)
                            {
                                drawCheckmark(contentStream, baseX + 115, baseY + 95);
                            }else
                            {
                                drawCheckmark(contentStream, baseX + 115, baseY + 94);
                            }
                        }
                        case "Fisch" -> drawCheckmark(contentStream, baseX + 115, baseY + 66);
                        case "Hähnchen" ->
                        {
                            if(entryCounter == 2 || entryCounter == 7 || entryCounter == 12 || entryCounter == 17)
                            {
                                drawCheckmark(contentStream, baseX + 115, baseY + 38);
                            }else
                            {
                                drawCheckmark(contentStream, baseX + 115, baseY + 38);
                            }
                        }
                    }

                    // Alternative (alternative)
                    Object alternative = entry.get("alternative");
                    String[] altWrt = (alternative == null || alternative.equals(""))
                            ? WordUtils.wrap("siehe oben", 29).split("\r?\n")
                            : WordUtils.wrap(alternative.toString(), 30).split("\r?\n");
                    // Text um 90 Grad gedreht anzeigen
                    for(int j = 0; j < altWrt.length; j++)
                    {
                        contentStream.beginText();
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
                        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(90), baseX + 144 + (j * 12), baseY - 2));
                        contentStream.showText(altWrt[j]);
                        contentStream.endText();
                    }
                    entryCounter++;
                }
            }
        }
        if(entryCounter < entries.size())
        {
            System.out.println("Einige Einträge konnten nicht platziert werden, da die PDF nur Platz für 20 Einträge hat.");
        }
    }


    // Funktion zum Zeichnen eines "×"
    private static void drawCheckmark(PDPageContentStream contentStream, float x, float y) throws IOException
    {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText("×");
        contentStream.endText();
    }

    // Funktion zum Trimmen langer Texte
    private static String truncateText(Object text)
    {
        if(text == null)
        {
            return "";
        }
        String str = text.toString();
        return str.length() > 10 ? str.substring(0, 10) + "..." : str;
    }
}
