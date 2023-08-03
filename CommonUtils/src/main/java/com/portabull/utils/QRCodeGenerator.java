package com.portabull.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.response.FileResponse;
import com.portabull.utils.fileutils.FileHandling;
import com.portabull.utils.validationutils.DMSFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeGenerator {

    private static final String charset = "UTF-8";

    Logger logger = LoggerFactory.getLogger(QRCodeGenerator.class);

    public String readQRCode(MultipartFile file) throws IOException {
        try {
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            return readQRCode(file.getInputStream(), hintMap);
        } catch (Exception e) {
            throw new IOException("Something went wrong please try after sometime");
        }
    }

    public FileResponse generateQRCodeImage(String text) {
        try {
            File tempFile = FileHandling.createTempFile("ORCodeImg.png");
            String fileName = tempFile.getName();
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();

            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            MatrixToImageWriter.writeToFile(new MultiFormatWriter().encode(
                    new String(text.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, 200, 200, hintMap), "png", tempFile);

            InputStream inputStream = FileHandling.getInputStream(tempFile);
            FileHandling.deleteFile(tempFile);

            return new FileResponse().setInputStream(inputStream).
                    setStatus(PortableConstants.SUCCESS).setFileName(fileName)
                    .setMediaType(MediaType.parseMediaType(DMSFileUtils.getContentType(fileName)));
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return new FileResponse().
                    setStatus(PortableConstants.FAILED).setMessage("Something went wrong please try after sometime");
        }
    }


    private static String readQRCode(InputStream inputStream, Map hintMap)
            throws IOException, NotFoundException {
        return new MultiFormatReader().decode(
                        new BinaryBitmap(new HybridBinarizer(
                                new BufferedImageLuminanceSource(
                                        ImageIO.read(inputStream)))),
                        hintMap).
                getText();
    }

}
