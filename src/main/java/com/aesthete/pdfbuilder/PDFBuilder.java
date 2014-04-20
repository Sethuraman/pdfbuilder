package com.aesthete.pdfbuilder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sethuraman on 10/04/2014.
 */
public class PDFBuilder implements AutoCloseable{

    private final Document document;
    private final PdfWriter writer;
    private String outputFile;
    private boolean isPortraitA4;
    private final TOCBuilder tocBuilder;
    private SignatureBuilder signatureBuilder;
    private boolean isComplete;

    public PDFBuilder(String outputFile, boolean isPortraitA4) throws FileNotFoundException, DocumentException {
        this.outputFile = outputFile;
        this.isPortraitA4 = isPortraitA4;
        document = new Document(isPortraitA4 ? PageSize.A4 : PageSize.A4.rotate());
        writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        writer.setLinearPageMode();
        document.open();

        tocBuilder = new TOCBuilder();
        tocBuilder.withIsPortraitA4(isPortraitA4).withOriginalBuilder(this);
    }

    public PDFBuilder addSignatureField(SignatureBuilder signatureBuilder){
        this.signatureBuilder = signatureBuilder;
        return this;
    }

    public void signWithAlias(String alias) throws Exception {
        complete();
        this.signatureBuilder.sign(alias);
    }


    public PDFBuilder addParagraph(ParagraphBuilder paragraphBuilder) throws DocumentException {
        document.add(paragraphBuilder.getParagraph(tocBuilder.getToc(), writer));
        return this;
    }

    public PDFBuilder newPage() throws DocumentException {
        document.newPage();
        return this;
    }

    public PDFBuilder addTable(PDFTableBuilder tableBuilder) throws DocumentException {
        document.add(tableBuilder.getTable());
        return this;
    }

    public PDFBuilder buildTOC(int tocToBePlacedAtPageNumber, TOCBuilder.TocPageBuilder tocPageBuilder) throws IOException, DocumentException {
        tocBuilder.withTocPageBuilder(tocPageBuilder);
        tocBuilder.createTOC(tocToBePlacedAtPageNumber);
        return this;
    }

    public PDFBuilder withAuthor(String author){
        document.addAuthor(author);
        return this;
    }

    public void complete() throws DocumentException, IOException {
        if(!isComplete){
            document.close();
        }
    }

    public int getCurrentPageNumber(){
        return writer.getPageNumber();
    }


    Document getDocument() {
        return document;
    }

    PdfWriter getWriter() {
        return writer;
    }

    String getOutputFile() {
        return outputFile;
    }

    @Override
    public void close() throws Exception {
        complete();
    }
}
