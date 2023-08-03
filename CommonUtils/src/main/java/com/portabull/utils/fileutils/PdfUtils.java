package com.portabull.utils.fileutils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PdfUtils {

    public String extractRawData(String completeFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(completeFilePath))) {
            return new PDFTextStripper().getText(document);
        }
    }

    public File getPdfPagesAsImage(int pageNumber, String completeFilePath) throws IOException {
        File file;
        try (PDDocument document = PDDocument.load(new File(completeFilePath))) {
            file = FileHandling.prepareTempFile("pdfPageImage", "jpg");
            ImageIO.write(new PDFRenderer(document).renderImage(pageNumber), "JPEG", file);
        }
        return file;
    }

}
