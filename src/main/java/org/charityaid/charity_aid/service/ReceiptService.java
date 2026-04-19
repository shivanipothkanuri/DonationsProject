package org.charityaid.charity_aid.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.charityaid.charity_aid.entity.Donation;
import org.springframework.stereotype.Service;

/**
 * FR-22: Generates a downloadable PDF receipt for a completed donation.
 */
@Service
public class ReceiptService {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm");

    /**
     * Builds a PDF receipt as a byte array.
     *
     * @param donation the persisted Donation (with donor and campaign eagerly loaded)
     * @return PDF bytes ready to stream as application/pdf
     */
    public byte[] generateReceipt(Donation donation) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin = 60;
            float pageWidth = page.getMediaBox().getWidth();
            float yStart = page.getMediaBox().getHeight() - margin;

            PDType1Font bold   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // ── Header bar ────────────────────────────────────────────
                cs.setNonStrokingColor(new Color(33, 102, 172));
                cs.addRect(0, yStart - 10, pageWidth, 50);
                cs.fill();

                cs.beginText();
                cs.setFont(bold, 22);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(margin, yStart + 12);
                cs.showText("CharityAid – Donation Receipt");
                cs.endText();

                float y = yStart - 40;

                // ── Organisation note ─────────────────────────────────────
                cs.beginText();
                cs.setFont(regular, 10);
                cs.setNonStrokingColor(Color.GRAY);
                cs.newLineAtOffset(margin, y);
                cs.showText("This receipt confirms your charitable donation. Please retain for your records.");
                cs.endText();
                y -= 30;

                // ── Divider ───────────────────────────────────────────────
                drawLine(cs, margin, y, pageWidth - margin, y);
                y -= 20;

                // ── Receipt details ───────────────────────────────────────
                String donorName = donation.getDonor() != null ? donation.getDonor().getFullName() : "—";
                String donorEmail = donation.getDonor() != null ? donation.getDonor().getEmailAddress() : "—";
                String campaign = donation.getCampaign() != null ? donation.getCampaign().getCampaignTitle() : "—";
                String txn = donation.getTransactionId() != null ? donation.getTransactionId() : "—";
                String type = donation.getDonationType() != null ? donation.getDonationType().name() : "—";
                String amount = donation.getDonationAmount() != null
                        ? "$" + donation.getDonationAmount().toPlainString() : "—";
                String item = donation.getItemDescription() != null ? donation.getItemDescription() : "—";
                String date = donation.getDonationDate() != null
                        ? donation.getDonationDate().format(DATE_FMT) : "—";
                String payment = donation.getPaymentMethod() != null
                        ? donation.getPaymentMethod().name().replace("_", " ") : "—";

                y = writeRow(cs, bold, regular, margin, y, "Transaction ID", txn);
                y = writeRow(cs, bold, regular, margin, y, "Date", date);
                y = writeRow(cs, bold, regular, margin, y, "Donor Name", donorName);
                y = writeRow(cs, bold, regular, margin, y, "Donor Email", donorEmail);
                y = writeRow(cs, bold, regular, margin, y, "Campaign", campaign);
                y = writeRow(cs, bold, regular, margin, y, "Donation Type", type);

                if ("MONETARY".equals(type)) {
                    y = writeRow(cs, bold, regular, margin, y, "Amount", amount);
                    y = writeRow(cs, bold, regular, margin, y, "Payment Method", payment);
                } else {
                    y = writeRow(cs, bold, regular, margin, y, "Item Description", item);
                }

                y -= 10;
                drawLine(cs, margin, y, pageWidth - margin, y);
                y -= 20;

                // ── Thank-you note ────────────────────────────────────────
                cs.beginText();
                cs.setFont(bold, 12);
                cs.setNonStrokingColor(new Color(33, 102, 172));
                cs.newLineAtOffset(margin, y);
                cs.showText("Thank you for your generous contribution!");
                cs.endText();
                y -= 18;

                cs.beginText();
                cs.setFont(regular, 10);
                cs.setNonStrokingColor(Color.DARK_GRAY);
                cs.newLineAtOffset(margin, y);
                cs.showText("CharityAid is dedicated to helping those in need. Your support makes a difference.");
                cs.endText();

                // ── Footer ────────────────────────────────────────────────
                cs.beginText();
                cs.setFont(regular, 8);
                cs.setNonStrokingColor(Color.GRAY);
                cs.newLineAtOffset(margin, 30);
                cs.showText("Generated by CharityAid Management System  |  " + txn);
                cs.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private float writeRow(PDPageContentStream cs,
                            PDType1Font labelFont, PDType1Font valueFont,
                            float x, float y,
                            String label, String value) throws IOException {
        cs.beginText();
        cs.setFont(labelFont, 11);
        cs.setNonStrokingColor(Color.BLACK);
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        cs.beginText();
        cs.setFont(valueFont, 11);
        cs.setNonStrokingColor(Color.DARK_GRAY);
        cs.newLineAtOffset(x + 160, y);
        cs.showText(value != null ? value : "—");
        cs.endText();

        return y - 22;
    }

    private void drawLine(PDPageContentStream cs, float x1, float y,
                           float x2, float y2) throws IOException {
        cs.setStrokingColor(Color.LIGHT_GRAY);
        cs.moveTo(x1, y);
        cs.lineTo(x2, y2);
        cs.stroke();
    }
}
