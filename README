##PDFBuilder

This library is meant to be used not as a jar that you can add to your library (not saying you can't but you would rather tweak the end pdf than accept mine), but rather, you would use the source files.

There are only about 4 classes and it should be easy to understand. Like the library name suggests, these 4 classes help you build a pdf. I use [itext](http://itextpdf.com/), yeah,
so the AGPL license applies to this library as well.

I was using itext for my own work and had to write a wrapper on top of itext to make it easier to build pdfs, hence thought I would try an extract the code out into a library
 for everyone to use.

The code is really simple to use.. refer the below show case to understand how.

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

I have also added a signature builder, but left it out of the showcase on purpose, since it works only on a windows PC. I access the WINDOWS-MY keystore to obtain certificates.
If you need to digitally sign your pdf, the code is present in the SignatureBuilder class. You just need to tweak the sign() and the createSignatureFieldCell() methods. Otherwise, if you want
to just use it,


    pdfBuilder.addSignatureField(new SignatureBuilder(pdfBuilder)
                .withSpacingBefore(20)
                .withSignDate(<date>)
                .withSignatureFieldHeading(new ParagraphBuilder(<Signed by Person Designation>).withBoldFont()));
    pdfBuilder.signWithAlias(<alias from the windows-my keystore>);
