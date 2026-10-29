package com.snapit.service.impl;

import com.snapit.exception.ImageOcrException;
import com.snapit.exception.InvalidImageException;
import com.snapit.service.ImageOcrService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ImageOcrServiceImpl implements ImageOcrService {
    @Override
    public String extractText(MultipartFile file) throws IOException {
        BufferedImage bufferedImage;
        try(InputStream inputStream = file.getInputStream()) {
           bufferedImage = ImageIO.read(inputStream);
        }
        if (bufferedImage == null){
            throw new InvalidImageException("Unable to read uploaded image.",
                    new IOException("Unsupported or invalid image format."));
        }
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(Path.of("./tessdata").toAbsolutePath().normalize().toString());
        tesseract.setLanguage("eng");
        try{
            return tesseract.doOCR(bufferedImage);
        }catch (TesseractException tesseractException){
            throw new ImageOcrException("Unable to extract text from image", tesseractException);
        }
    }
}
