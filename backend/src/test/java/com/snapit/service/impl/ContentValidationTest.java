package com.snapit.service.impl;

import com.snapit.exception.InvalidImageException;
import com.snapit.exception.PdfExtractionException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentValidationTest {
    private final WebPageExtractionServiceImpl webPageExtractionService =
            new WebPageExtractionServiceImpl();
    private final ImageOcrServiceImpl imageOcrService = new ImageOcrServiceImpl();
    private final PdfExtractionServiceImpl pdfExtractionService =
            new PdfExtractionServiceImpl();

    @Test
    void rejectsLoopbackWebpageAddressBeforeConnecting() {
        assertThrows(IllegalArgumentException.class,
                () -> webPageExtractionService.fetchWebPage("http://127.0.0.1/"));
    }

    @Test
    void rejectsUnsupportedWebpageScheme() {
        assertThrows(IllegalArgumentException.class,
                () -> webPageExtractionService.fetchWebPage("file:///etc/passwd"));
    }

    @Test
    void rejectsUnsupportedImageBytes() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "not an image".getBytes(StandardCharsets.UTF_8)
        );
        assertThrows(InvalidImageException.class, () -> imageOcrService.extractText(file));
    }

    @Test
    void rejectsNonPdfBytesRegardlessOfDeclaredMimeType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "not a pdf".getBytes(StandardCharsets.UTF_8)
        );
        assertThrows(PdfExtractionException.class, () -> pdfExtractionService.extractText(file));
    }
}
