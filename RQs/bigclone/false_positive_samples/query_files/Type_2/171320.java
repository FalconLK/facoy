package br.net.woodstock.rockframework.core.test.office;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import junit.framework.TestCase;
import br.net.woodstock.rockframework.office.pdf.PDFManager;
import br.net.woodstock.rockframework.office.pdf.impl.ITextManager;
import br.net.woodstock.rockframework.office.pdf.impl.PDFBoxManager;
import br.net.woodstock.rockframework.utils.IOUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class TestPDF extends TestCase {

    public void xtest1() throws Exception {
        InputStream input = new FileInputStream("C:/Documentos/j931_01.pdf");
        InputStream tmp = new ITextManager().cut(input, 3, 8);
        FileOutputStream output = new FileOutputStream("C:/temp/split.pdf");
        IOUtils.copy(tmp, output);
        input.close();
        tmp.close();
        output.close();
    }

    public void xtest2() throws Exception {
        InputStream input1 = new FileInputStream("C:/Documentos/j931_01.pdf");
        InputStream input2 = new FileInputStream("C:/Documentos/j931_02.pdf");
        InputStream tmp = new ITextManager().merge(new InputStream[] { input1, input2 });
        FileOutputStream output = new FileOutputStream("C:/temp/split.pdf");
        IOUtils.copy(tmp, output);
        input1.close();
        input2.close();
        tmp.close();
        output.close();
    }

    public void xtest3() throws Exception {
        InputStream inputStream = new FileInputStream("C:/temp/split.pdf");
        OutputStream outputStream = new FileOutputStream("C:/temp/split-barcode.pdf");
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        PdfContentByte contentByte = pdfStamper.getUnderContent(1);
        BarcodeEAN barcode = new BarcodeEAN();
        barcode.setCodeType(Barcode.EAN13);
        barcode.setCode("9780201615883");
        Image image = barcode.createImageWithBarcode(contentByte, null, null);
        image.setAbsolutePosition(400, 800);
        contentByte.addImage(image);
        pdfStamper.close();
    }

    public void xtest7() throws Exception {
        System.out.println("Lowagie");
        FileInputStream inputStream = new FileInputStream("C:/Temp/arquivo.pdf");
        PDFBoxManager manager = new PDFBoxManager();
        InputStream[] images = manager.toImage(inputStream, "jpeg");
        int count = 0;
        for (InputStream image : images) {
            FileOutputStream outputStream = new FileOutputStream("C:/Temp/arquivo_" + count + ".jpg");
            IOUtils.copy(image, outputStream);
            count++;
            outputStream.close();
        }
        inputStream.close();
    }

    public void xtest8() throws Exception {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            System.out.println(font.getFontName());
        }
    }

    public void xtest9() throws Exception {
        InputStream inputStream = new FileInputStream("/tmp/visualizarPDF.pdf");
        OutputStream outputStream = new FileOutputStream("/tmp/visualizarPDF-2.pdf");
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        PdfContentByte contentByte = pdfStamper.getUnderContent(1);
        Rectangle rectangle = pdfReader.getPageSize(1);
        PdfGState gsState = new PdfGState();
        gsState.setFillOpacity(0.7f);
        BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        float fontSize = 12;
        float marginLeft = 20;
        float marginTop = 20;
        BufferedImage bufferedImage = ImageIO.read(new File("/tmp/carimbo.png"));
        Image image = Image.getInstance(contentByte, bufferedImage, 1);
        float imageLeft = rectangle.getWidth() - marginLeft - image.getScaledWidth();
        float imageTop = rectangle.getTop() - marginTop - image.getScaledHeight();
        float textLeft = rectangle.getWidth() - marginLeft - (image.getScaledWidth() / 1.95f);
        float textTop = rectangle.getTop() - marginTop - (image.getScaledHeight() / 2.05f);
        image.setAbsolutePosition(imageLeft, imageTop);
        contentByte.setGState(gsState);
        contentByte.addImage(image);
        contentByte.beginText();
        contentByte.setFontAndSize(font, fontSize);
        contentByte.showTextAligned(Element.ALIGN_LEFT, Long.toString(1), textLeft, textTop, 0);
        contentByte.endText();
        contentByte.stroke();
        pdfStamper.close();
        inputStream.close();
        outputStream.close();
    }

    public void xtest10() throws Exception {
        File file = new File("/tmp/carimbo.png");
        URL resource = file.toURI().toURL();
        InputStream inputStream = new FileInputStream("/tmp/visualizarPDF.pdf");
        OutputStream outputStream = new FileOutputStream("/tmp/visualizarPDF-3.pdf");
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        PdfContentByte conteudo = pdfStamper.getUnderContent(1);
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.8f);
        Image img = Image.getInstance(resource);
        Document doc = new Document();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        float f = doc.getPageSize().getWidth() - img.getScaledWidth();
        float g = doc.getPageSize().getHeight() - img.getScaledHeight();
        img.setAbsolutePosition((f / 100) + 10, g - 60);
        conteudo.beginText();
        conteudo.setGState(gs);
        conteudo.setTextMatrix(doc.top() - 150, doc.bottom() - 20);
        conteudo.setFontAndSize(bf, 12);
        float width = doc.getPageSize().getWidth() - 65;
        float top = doc.getPageSize().getTop() - 110;
        conteudo.showTextAligned(Element.ALIGN_LEFT, Long.toString(1), width, top + 6, 0);
        conteudo.addImage(img, true);
        conteudo.endText();
        conteudo.stroke();
        pdfStamper.close();
        inputStream.close();
        outputStream.close();
    }

    public void xtest11() throws Exception {
        PDFManager manager = new ITextManager();
        InputStream pdf = new FileInputStream("/tmp/UML2.pdf");
        InputStream page1 = manager.cut(pdf, 1, 1);
        OutputStream outputStream = new FileOutputStream("/tmp/page.pdf");
        IOUtils.copy(page1, outputStream);
        outputStream.close();
        pdf.close();
    }

    public void xtest12() throws Exception {
        PDFManager manager = new ITextManager();
        InputStream pdf = new FileInputStream("/tmp/090237098008f637.pdf");
        InputStream page1 = manager.cut(pdf, 36, 36);
        OutputStream outputStream = new FileOutputStream("/tmp/090237098008f637-1.pdf");
        IOUtils.copy(page1, outputStream);
        outputStream.close();
        pdf.close();
    }

    public void test13() throws Exception {
        PDFManager manager = new ITextManager();
        InputStream pdf = new FileInputStream("/tmp/090237098008f637.pdf");
        String str = manager.getText(pdf);
        System.out.println(str);
        pdf.close();
    }
}
