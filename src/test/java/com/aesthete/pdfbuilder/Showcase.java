package com.aesthete.pdfbuilder;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.util.Map;

import static com.aesthete.pdfbuilder.PDFTableBuilder.newTable;
import static com.aesthete.pdfbuilder.ParagraphBuilder.newParagraph;

/**
 * Created by sethu on 20/04/14.
 */
public class Showcase {

    public static void main(String[] args) {
        PDFBuilder pdfBuilder=null;
        try {
            File temp = File.createTempFile("temp", ".pdf");

            pdfBuilder = new PDFBuilder(temp.getAbsolutePath(), false)

                .addParagraph(newParagraph("Test PDF").asH1Heading().withCenterAlignment().withSpacingAfter(40).withMarker("Test PDF"))

                .addParagraph(newParagraph("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                        "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit " +
                        "anim id est laborum.").withBoldFont().withSpacingAfter(20).withJustifyAlignment())

                .newPage()

                .addParagraph(newParagraph("Test Table").asH2Heading().withCenterAlignment().withSpacingAfter(20).withMarker("Test Table"));

            PDFTableBuilder tableBuilder = newTable(1, 8, 5)
                    .addCell(newParagraph("Column 1").withBoldFont())
                    .addCell(newParagraph("Column 2").withBoldFont())
                    .addCell(newParagraph("Column 3").withBoldFont());

            for(int i=0;i<10;i++){
                int counter = i + 1;
                tableBuilder.addCell(newParagraph(String.valueOf(counter)))
                        .addCell(newParagraph("This is a test on the "+counter+" row"))
                        .addCell(newParagraph("Last column on the "+counter+" row"));
            }

            pdfBuilder.addTable(tableBuilder)
                        .newPage();

            pdfBuilder.buildTOC(1, new TOCBuilder.TocPageBuilder() {
                @Override
                public void createToc(PDFBuilder pdfBuilder, Map<String, Integer> toc) throws DocumentException {
                    pdfBuilder.addParagraph(newParagraph("Table of Contents").asH1Heading().withCenterAlignment().withSpacingAfter(40));

                    PDFTableBuilder tocTable=new PDFTableBuilder(8,1);
                    tocTable.addCell(newParagraph("Details").withBoldFont()).addCell(newParagraph("Page Number").withBoldFont());
                    for(Map.Entry<String,Integer> entry : toc.entrySet()){
                        tocTable.addCell(entry.getKey()).addCell(String.valueOf(entry.getValue()));
                    }
                    pdfBuilder.addTable(tocTable);
                }
            });



            System.out.println("pdf stored here : "+temp.getAbsolutePath());

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(pdfBuilder!=null){
                try {
                    pdfBuilder.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
