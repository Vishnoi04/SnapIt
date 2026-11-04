package com.snapit.service.impl;

import com.snapit.exception.ImageOcrException;
import com.snapit.exception.InvalidImageException;
import com.snapit.service.ImageOcrService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;

@Service
public class ImageOcrServiceImpl implements ImageOcrService {
    private static final long MAX_IMAGE_PIXELS = 40_000_000L;

    @Override
    public String extractText(MultipartFile file) throws IOException {
        BufferedImage bufferedImage;
        try (InputStream inputStream = file.getInputStream();
             ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
            if (imageInputStream == null) {
                throw invalidImage();
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw invalidImage();
            }
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals("png") && !format.equals("jpeg") && !format.equals("jpg")) {
                    throw invalidImage();
                }
                reader.setInput(imageInputStream, true, true);
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                if (pixels <= 0 || pixels > MAX_IMAGE_PIXELS) {
                    throw new InvalidImageException("Image dimensions exceed the supported limit.",
                            new IOException("Image pixel limit exceeded."));
                }
                bufferedImage = reader.read(0);
                if (bufferedImage == null) {
                    throw invalidImage();
                }
            } finally {
                reader.dispose();
            }
        }
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(Path.of("./tessdata").toAbsolutePath().normalize().toString());
        tesseract.setLanguage("eng");
        try {
            return tesseract.doOCR(bufferedImage);
        } catch (TesseractException tesseractException) {
            throw new ImageOcrException("Unable to extract text from image", tesseractException);
        }
    }

    private InvalidImageException invalidImage() {
        return new InvalidImageException("Unable to read a supported PNG or JPEG image.",
                new IOException("Unsupported or invalid image format."));
    }
}
