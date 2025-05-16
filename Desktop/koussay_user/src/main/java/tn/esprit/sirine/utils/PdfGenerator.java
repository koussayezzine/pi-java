package tn.esprit.sirine.utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import tn.esprit.sirine.models.Revenu;


import java.io.File;

public class PdfGenerator {
    public static void generateRevenuPdf(Revenu revenu, File file) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
        content.setLeading(20f);
        content.newLineAtOffset(50, 750);

        content.showText("FACTURE");
        content.newLine();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.showText("Client : " + revenu.getClient());
        content.newLine();
        content.showText("Date : " + revenu.getDate());
        content.newLine();
        content.showText("Montant : " + revenu.getMontant() + " TND");
        content.newLine();
        content.showText("Source : " + revenu.getSource());
        content.newLine();
        content.showText("Détails : " + revenu.getFactureDetails());
        content.newLine();

        double montantHT = revenu.getMontant();
        double tva = montantHT * 0.19;
        double ttc = montantHT + tva;

        content.newLine();
        content.showText("Sous-total : " + String.format("%.2f", montantHT));
        content.newLine();
        content.showText("TVA (19%) : " + String.format("%.2f", tva));
        content.newLine();
        content.showText("Total TTC : " + String.format("%.2f", ttc));

        content.endText();
        content.close();

        document.save(file);
        document.close();
    }
}
