package tn.esprit.sirine.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import tn.esprit.sirine.models.Depenses;

import java.io.File;
import java.io.IOException;

public class PdfGeneratorDepense {

    public static void generateDepensePdf(Depenses depense, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(180, 750);
                contentStream.showText("Facture de Dépense");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(18f);
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText("Fournisseur : " + depense.getFournisseur()); contentStream.newLine();
                contentStream.showText("Date : " + depense.getDate()); contentStream.newLine();
                contentStream.showText("Catégorie : " + depense.getCategorie()); contentStream.newLine();
                contentStream.showText("Montant : " + depense.getMontant() + " €"); contentStream.newLine();
                contentStream.showText("Statut : " + depense.getStatut()); contentStream.newLine();
                contentStream.showText("Description : " + depense.getDescription()); contentStream.newLine();
                if (depense.getJustificatif() != null) {
                    contentStream.showText("Justificatif : " + depense.getJustificatif()); contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(file);
        }
    }
}
