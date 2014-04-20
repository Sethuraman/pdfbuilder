package com.aesthete.pdfbuilder;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
* Created by Sethuraman on 10/04/2014.
*/
public class ParagraphBuilder{

    static final Font smallBoldTableFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);
    static final Font smallNormalTableFont = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
    static final Font h1HeadingFont=new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
    static final Font h2HeadingFont=new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, BaseColor.BLUE);
    static final Font smallFont=new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    static final Font boldFont=new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    static final Font normalFont=new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
    static final Font normalLinkFont=new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLUE);

    private String text;
    private int alignment;
    private Font font;
    private float spacingAfterUnits;
    private String marker;
    private String goToMarker;
    private float spacingBeforeUnits;
    private String linkFilename;
    private int linkFilePageNumber;


    public ParagraphBuilder(String text) {
        this.text = text;
    }

    public ParagraphBuilder withMarker(String marker){
        this.marker = marker;
        return this;
    }

    public ParagraphBuilder withGoToMarker(String marker){
        this.goToMarker = marker;
        return this;
    }

    public ParagraphBuilder asH1Heading(){
        font=h1HeadingFont;
        return this;
    }

    public ParagraphBuilder asH2Heading(){
        font=h2HeadingFont;
        return this;
    }


    public ParagraphBuilder withLeftAlignment(){
        alignment= Element.ALIGN_LEFT;
        return this;
    }

    public ParagraphBuilder withRightAlignment(){
        alignment= Element.ALIGN_RIGHT;
        return this;
    }

    public ParagraphBuilder withCenterAlignment(){
        alignment= Element.ALIGN_CENTER;
        return this;
    }

    public ParagraphBuilder withJustifyAlignment(){
        alignment=Element.ALIGN_JUSTIFIED;
        return this;
    }

    public ParagraphBuilder withSmallTableFont(){
        font=smallNormalTableFont;
        return this;
    }

    public ParagraphBuilder withSmallTableBoldFont(){
        font=smallBoldTableFont;
        return this;
    }

    public ParagraphBuilder withSmallFont(){
        font=smallFont;
        return this;
    }

    public ParagraphBuilder withBoldFont(){
        font=boldFont;
        return this;
    }

    Paragraph getParagraph(Map<String, Integer> toc, PdfWriter writer) {
        Chunk chunk = new Chunk(StringUtils.defaultString(text), font == null ? normalFont : font);
        if(marker!=null && toc!=null){
            chunk.setLocalDestination(marker);
            toc.put(marker, writer.getPageNumber());
        }else if(goToMarker!=null){
            chunk.setLocalGoto(goToMarker);
        }else if(linkFilename!=null){
            chunk.setAction(new PdfAction(linkFilename, linkFilePageNumber));
        }
        Paragraph paragraph=new Paragraph(chunk);
        paragraph.setAlignment(alignment);
        if(spacingAfterUnits>0)  paragraph.setSpacingAfter(spacingAfterUnits);
        if(spacingBeforeUnits>0) paragraph.setSpacingBefore(spacingBeforeUnits);
        return paragraph;
    }

    Paragraph getParagraph() {
        return getParagraph(null, null);
    }

    public ParagraphBuilder withSpacingAfter(float units){
        spacingAfterUnits = units;
        return this;
    }


    public ParagraphBuilder withSpacingBefore(float units){
        spacingBeforeUnits = units;
        return this;
    }

    public ParagraphBuilder asLinkToAnotherFile(String linkFilename, int linkFilePageNumber){
        this.linkFilename = linkFilename;
        this.linkFilePageNumber = linkFilePageNumber==0?1:linkFilePageNumber;
        this.font=normalLinkFont;
        return this;
    }

    public static ParagraphBuilder newParagraph(String text){
        return new ParagraphBuilder(text);
    }


}
