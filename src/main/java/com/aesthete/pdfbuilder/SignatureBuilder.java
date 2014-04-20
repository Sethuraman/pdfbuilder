package com.aesthete.pdfbuilder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by sethu on 14/04/14.
 */
public class SignatureBuilder {

    private PdfWriter pdfWriter;
    private Document document;
    private PDFBuilder pdfBuilder;
    private Date signDate;
    private PdfPTable table;

    public SignatureBuilder(PDFBuilder pdfBuilder) {
        this.pdfBuilder = pdfBuilder;
        pdfWriter=pdfBuilder.getWriter();
        document=pdfBuilder.getDocument();
        table=new PdfPTable(1);
    }

    public SignatureBuilder withSpacingBefore(float spacingBefore){
        table.setSpacingBefore(spacingBefore);
        return this;
    }

    public SignatureBuilder withSignatureFieldHeading(ParagraphBuilder signatureFieldHeading) throws DocumentException {
        table.setWidthPercentage(95);

        table.addCell(new PdfPCell(signatureFieldHeading.getParagraph()));
        table.addCell(createSignatureFieldCell());

        document.add(table);

        return this;
    }

    public SignatureBuilder withSignDate(Date signDate){
        this.signDate = signDate;
        return this;
    }

    private PdfPCell createSignatureFieldCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new BaseColor(223, 228, 254));
        cell.setMinimumHeight(50);
        final PdfFormField field = PdfFormField.createSignature(pdfWriter);
        field.setFieldName("sig1");
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        cell.setCellEvent(new PdfPCellEvent() {
            @Override
            public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
                PdfWriter writer = canvases[0].getPdfWriter();
                field.setPage();
                field.setWidget(position, PdfAnnotation.HIGHLIGHT_INVERT);
                writer.addAnnotation(field);
            }
        });
        return cell;
    }

    void sign(String alias) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("Windows-MY");
        keyStore.load(null, null);



        KeyStore.PasswordProtection clave2 = new KeyStore.PasswordProtection("123456".toCharArray());
        KeyStore.PrivateKeyEntry pkEntry=(KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, clave2);

        PrivateKey myPrivateKey = pkEntry.getPrivateKey();
        Certificate[] chain = pkEntry.getCertificateChain();
        OcspClient ocspClient = new OcspClientBouncyCastle();
        TSAClient tsaClient = null;
        X509Certificate lastCertificate=null;
        for (int i = 0; i < chain.length; i++) {
            if(chain[i] instanceof X509Certificate){
                X509Certificate cert = (X509Certificate)chain[i];
                String tsaUrl = CertificateUtil.getTSAURL(cert);
                if (tsaUrl != null) {
                    tsaClient = new TSAClientBouncyCastle(tsaUrl);
                    break;
                }
                lastCertificate=cert;
            }
        }
        if(lastCertificate!=null){
            checkValidityOfCertificates(lastCertificate);
        }
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(new CrlClientOnline(chain));


        PdfReader reader = new PdfReader(pdfBuilder.getOutputFile());
        File signed = File.createTempFile("signed", ".pdf");
        FileOutputStream fout = new FileOutputStream(signed);
        PdfStamper stamper = PdfStamper.createSignature(reader, fout, '\0', File.createTempFile("itext", "temp.pdf"));

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature("sig1");
        appearance.setLayer2Text("Signed On "+new SimpleDateFormat("dd/MM/yyyy").format(signDate));
        Calendar instance = Calendar.getInstance();
        instance.setTime(signDate);
        appearance.setSignDate(instance);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);

        // Creating the signature
        ExternalSignature pks = new PrivateKeySignature(myPrivateKey, DigestAlgorithms.SHA256, keyStore.getProvider().getName());
        ExternalDigest digest = new BouncyCastleDigest();

        MakeSignature.signDetached(appearance, digest, pks, chain, crlList, ocspClient, tsaClient, 0, MakeSignature.CryptoStandard.CMS);


        Files.move(signed.toPath(), Paths.get(pdfBuilder.getOutputFile()), StandardCopyOption.REPLACE_EXISTING);

    }

    private void checkValidityOfCertificates(X509Certificate cert) throws Exception {
        try{
            cert.checkValidity(signDate);
        }catch (CertificateExpiredException e){
            Files.delete(Paths.get(pdfBuilder.getOutputFile()));
            throw new IllegalArgumentException("The certificate has expired on the date of signing. Please renew the certificate as the certificate's expiry date is "
                    +new SimpleDateFormat("dd/MM/yyyy").format(cert.getNotAfter()));
        }catch (CertificateNotYetValidException e){
            Files.delete(Paths.get(pdfBuilder.getOutputFile()));
            throw new IllegalArgumentException("The certificate is not valid on the date of signing. Please change the date of signing as the certificate only becomes valid on "
                    +new SimpleDateFormat("dd/MM/yyyy").format(cert.getNotBefore()));
        }
    }
}
