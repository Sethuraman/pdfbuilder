package com.aesthete.pdfbuilder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
* Created by Sethuraman on 10/04/2014.
*/
public class PDFTableBuilder{

    private int columns;
    private final PdfPTable pdfPTable;

    private int colCounter;
    private PdfPCell lastCellUsed;

    public PDFTableBuilder(int columns) {
        this.columns = columns;
        pdfPTable = new PdfPTable(columns);
    }

    public PDFTableBuilder(float... columnWidths) {
        this.columns = columnWidths.length;
        pdfPTable = new PdfPTable(columnWidths);

    }

    public PDFTableBuilder withSpacingBefore(float spacingBefore){
        pdfPTable.setSpacingBefore(spacingBefore);
        return this;
    }

    public PDFTableBuilder addCell(String text){
        return addCell(ParagraphBuilder.newParagraph(text));
    }

    public PDFTableBuilder addCell(ParagraphBuilder paragraphBuilder){
        addLastCell();
        lastCellUsed = new PdfPCell(paragraphBuilder.getParagraph());
        colCounter++;
        return this;
    }

    public PDFTableBuilder withColSpan(int colSpan){
        colCounter+=colSpan-1;
        lastCellUsed.setColspan(colSpan);
        return this;
    }

    public PDFTableBuilder withLeftAlignment(){
        lastCellUsed.setHorizontalAlignment(Element.ALIGN_LEFT);
        return this;
    }

    public PDFTableBuilder withRightAlignment(){
        lastCellUsed.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return this;
    }

    public PDFTableBuilder withCenterAlignment(){
        lastCellUsed.setHorizontalAlignment(Element.ALIGN_CENTER);
        return this;
    }

    public PDFTableBuilder withNoBorder(){
        pdfPTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        pdfPTable.getDefaultCell().setBackgroundColor(new BaseColor(255,255,255));
        return this;
    }

    public PDFTableBuilder forceNewRow(){
        addLastCell();
        while(colCounter<columns){
            pdfPTable.addCell("");
        }
        colCounter=0;
        return this;
    }

    private void addLastCell() {
        if(lastCellUsed !=null){
            pdfPTable.addCell(lastCellUsed);
            if(colCounter>=columns){
                colCounter=0;
            }
        }
    }

    public PdfPTable getTable() {
        addLastCell();
        return pdfPTable;
    }

    public PDFTableBuilder withTableWidth(float tableWidth){
        pdfPTable.setWidthPercentage(tableWidth);
        return this;
    }

    public PDFTableBuilder withSpacingAfter(int spacingAfter){
        pdfPTable.setSpacingAfter(spacingAfter);
        return this;
    }

    public PDFTableBuilder withHeaderRow(int headerRow){
        pdfPTable.setHeaderRows(1);
        return this;
    }

    public static PDFTableBuilder newTable(int columns){
        return new PDFTableBuilder(columns);
    }

    public static PDFTableBuilder newTable(float... columnWidths){
        return new PDFTableBuilder(columnWidths);
    }

}
