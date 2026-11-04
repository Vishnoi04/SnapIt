package com.snapit.service.impl;

import com.snapit.exception.PdfExtractionException;
import com.snapit.service.PdfExtractionService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {
    private static final int MAX_PAGES = 1_000;

    @Override
    public String extractText(MultipartFile file) {
        try {
            byte[] content = file.getBytes();
            if (content.length < 5
                    || !new String(content, 0, 5, StandardCharsets.US_ASCII).equals("%PDF-")) {
                throw new PdfExtractionException(
                        "Unable to read PDF file. Please upload a valid PDF.",
                        new IOException("PDF signature is missing.")
                );
            }
            try (PDDocument document = Loader.loadPDF(content)) {
                if (document.getNumberOfPages() > MAX_PAGES) {
                    throw new PdfExtractionException(
                            "PDF exceeds the supported page limit.",
                            new IOException("PDF page limit exceeded.")
                    );
                }
                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                return pdfTextStripper.getText(document);
            }
        } catch (IOException e) {
            throw new PdfExtractionException(
                    "Unable to read PDF file. Please upload a valid PDF.",
                    e
            );
        }
    }
}
