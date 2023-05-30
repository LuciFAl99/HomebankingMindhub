package com.mindhub.Homebanking.Models;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionPDF {
    private Account account;
    private List<Transaction> listTransactions;
    private static final String LOGO_PATH = new File("E:/Descargas/DeployHomebanking/src/main/resources/static/Web/Assets/Imágenes/logo.png").getAbsolutePath(); // Ruta del archivo de imagen del logo

    public TransactionPDF(List<Transaction> listTransactions, Account account) {
        this.listTransactions = listTransactions;
        this.account = account;
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(4);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        cell.setPhrase(new Phrase("Fecha", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Descrición", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Tipo de transacción", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Monto", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for(Transaction transaction : listTransactions) {
            table.addCell(transaction.getDate().format(formatter));
            table.addCell(transaction.getDescription());
            table.addCell(String.valueOf(transaction.getType()));
            table.addCell((String.format(String.valueOf(transaction.getAmount()), "$0,0.00")));
        }
    }

    public void usePDFExport(HttpServletResponse response) throws DocumentException, IOException {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Estilos de fuentes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font detailsFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        //Logo
        InputStream is = getClass().getResourceAsStream("/static/Web/Assets/logo.png");
        Image logo = Image.getInstance(IOUtils.toByteArray(is));
        logo.scaleToFit(200, 200);
        logo.setAlignment(Image.ALIGN_LEFT);

        doc.add(logo);

        // Título
        Paragraph title = new Paragraph("Lista de Transacciones", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(10);
        doc.add(title);

        // Información de la cuenta
        Paragraph accountInfo = new Paragraph("Información de la cuenta", headerFont);
        accountInfo.setAlignment(Paragraph.ALIGN_LEFT);
        doc.add(accountInfo);

        doc.add(new Paragraph("Número de cuenta: " + account.getNumber(), detailsFont));
        doc.add(new Paragraph("Balance: " + account.getBalance(), detailsFont));

        LocalDateTime fechaCreacion = account.getCreationDate(); // Reemplaza esto con la fecha de creación real
        String fechaFormateada = fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        doc.add(new Paragraph("Fecha de creación: " + fechaFormateada, detailsFont));

        // Tabla de transacciones
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        // Encabezado de la tabla
        writeTableHeader(table);

        // Contenido de la tabla
        writeTableData(table);

        doc.add(table);
        doc.close();
    }
}
