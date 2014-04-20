package com.aesthete.pdfbuilder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sethu on 13/04/14.
 */
public class TOCBuilder {

    private PdfWriter writer;
    private ParagraphBuilder[] tocHeadings;
    private boolean isPortraitA4;
    private Map<String,Integer> toc;
    private PDFBuilder originalDocbuilder;
    private Document document;
    private float[] columnWidths;
    private ParagraphBuilder tocPageHeading;
    private int tocToBePlacedAtPageNumber;
    private TocPageBuilder tocPageBuilder;

    public TOCBuilder() {
        toc=new LinkedHashMap<>();
    }

    public TOCBuilder withTocPageBuilder(TocPageBuilder tocPageBuilder){
        this.tocPageBuilder=tocPageBuilder;
        return this;
    }

    TOCBuilder withIsPortraitA4(boolean isPortraitA4){
        this.isPortraitA4 = isPortraitA4;
        return this;
    }

    TOCBuilder withOriginalBuilder(PDFBuilder originalDocBuilder){
        this.originalDocbuilder = originalDocBuilder;
        document = originalDocbuilder.getDocument();
        writer = originalDocBuilder.getWriter();
        return this;
    }


    public void createTOC(int tocToBePlacedAtPageNumber) throws IOException, DocumentException {
        this.tocToBePlacedAtPageNumber = tocToBePlacedAtPageNumber;
        if(toc.isEmpty()) return;
        int noOfTocPages=createToCInANewDocumentToSeeHowManyPagesItTakes();
        document.newPage();
        int currentToCStartingPage=writer.getPageNumber();
        createTOC(originalDocbuilder, noOfTocPages);
        reorderPagesToBringTOCToTheFront(currentToCStartingPage);
    }

    private void reorderPagesToBringTOCToTheFront(int currentToCStartingPage) throws DocumentException {
        document.newPage();
        int total=writer.reorderPages(null);

        ArrayList<Integer> indexes=new ArrayList<>();
        for(int i=1;i<=total;i++){
            indexes.add(i);
        }
        for(int i=0;i<total-currentToCStartingPage+1;i++){
            indexes.add(tocToBePlacedAtPageNumber-1,indexes.remove(indexes.size()-1));
        }
        writer.reorderPages(ArrayUtils.toPrimitive(indexes.toArray(new Integer[indexes.size()])));
    }

    private int createToCInANewDocumentToSeeHowManyPagesItTakes() throws IOException, DocumentException {
        File tempFile = File.createTempFile("temp", "toc");
        PDFBuilder pdfBuilder = new PDFBuilder(tempFile.getAbsolutePath(), isPortraitA4);
        createTOC(pdfBuilder, 0);
        int currentPageNumber = pdfBuilder.getCurrentPageNumber();
        tempFile.delete();
        return currentPageNumber;
    }

    private void createTOC(PDFBuilder pdfBuilder, int offSetPageNumber) throws DocumentException {
        if(offSetPageNumber>0){
            for(Map.Entry<String,Integer> entry : toc.entrySet()){
                entry.setValue(entry.getValue()+offSetPageNumber);
            }
        }
        tocPageBuilder.createToc(pdfBuilder, toc);
//        pdfBuilder.addParagraph(tocPageHeading);
//
//        PDFTableBuilder tableBuilder=new PDFTableBuilder(columnWidths);
//        for(ParagraphBuilder tocHeading : tocHeadings){
//            tableBuilder.addCell(tocHeading);
//        }
//
//        for(Map.Entry<String,Integer> entry : toc.entrySet()){
//            if(entry.getKey().contains(",")){
//                String[] split=entry.getKey().split(",");
//                for (String s : split) {
//                    tableBuilder.addCell(newParagraph(s).withGoToMarker(entry.getKey()));
//                }
//            }else{
//                tableBuilder.addCell(newParagraph(entry.getKey()).withGoToMarker(entry.getKey()));
//            }
//            tableBuilder.addCell(newParagraph(String.valueOf(entry.getValue() + offSetPageNumber)).withGoToMarker(entry.getKey()));
//
//        }
//        pdfBuilder.addTable(tableBuilder);
    }

    Map<String, Integer> getToc() {
        return toc;
    }

    public static interface TocPageBuilder {
        void createToc(PDFBuilder pdfBuilder, Map<String, Integer> toc) throws DocumentException;
    }

}
