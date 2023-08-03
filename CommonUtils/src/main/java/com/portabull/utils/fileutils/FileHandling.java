package com.portabull.utils.fileutils;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.portabull.constants.*;
import com.portabull.payloads.MISPayload;
import com.portabull.response.FileResponse;
import com.portabull.response.MISTextResponse;
import com.portabull.utils.JsonUtils;
import com.portabull.utils.PdfEncriptionUtils;
import com.portabull.utils.dateutils.DateUtils;
import com.portabull.utils.scheduleutils.TaskScheduler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class FileHandling {


    private FileHandling() {
    }


    static Logger logger = LoggerFactory.getLogger(FileHandling.class);

    /**
     * It will write an object into a file class must and should be serializable
     *
     * @param data
     * @param absolutePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */

    public static File writeObjectIntoFile(Object data, String absolutePath) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(absolutePath)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(data);
            }
        }
        return new File(absolutePath);
    }

    /**
     * @param data
     * @param file
     * @return
     * @throws IOException
     */

    public static File writeObjectIntoFile(Object data, File file) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(data);
            }
        }
        return file;
    }


    /**
     * It will read an object from a file
     *
     * @param file
     * @param <T>
     * @return
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws IOException
     */

    public static <T> T readObjectFromFile(File file) throws ClassNotFoundException, IOException {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            try (ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
                Object obj = objectIn.readObject();
                return (T) obj;
            }
        }
    }

    /**
     * It will write bytes into file
     *
     * @param bytes
     * @param fileNameWithExection
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */

    public static File writeBytesIntoFile(byte[] bytes, String fileNameWithExection, String path) throws IOException {
        String absolutePath = new StringBuffer(path).append(fileNameWithExection).toString();
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        try (OutputStream outputStream = new FileOutputStream(absolutePath)) {
            outputStream.write(bytes);
        }
        return new File(absolutePath);
    }

    /**
     * it will write a inputStream into file
     *
     * @param inputStream
     * @param fileNameWithExection
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */

    public static File writeInputStreamIntoFile(InputStream inputStream, String fileNameWithExection, String path) throws IOException {
        return FileHandling.writeBytesIntoFile(IOUtils.toByteArray(inputStream), fileNameWithExection, path);
    }

    /**
     * It will generate a excel file based on the data
     *
     * @param data
     * @return
     * @throws Exception
     */

    public static FileResponse generateReportAsExcel(List<Map<String, Object>> data, MISPayload payload) throws IOException {

        if (CollectionUtils.isEmpty(data)) {
            return new FileResponse().setStatus(PortableConstants.FAILED).setMessage("No Data Found");
        }

        File file = prepareTempFile("MIS", FileConstants.XLSX);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet(payload.getMisName());
            Map<String, Object[]> customData = FileHandling.prepareCustomdata(data);

            AtomicReference<Integer> rowID = new AtomicReference<>(0);
            customData.keySet().forEach(key -> {
                XSSFRow row = spreadsheet.createRow(rowID.getAndSet(rowID.get() + 1));
                AtomicReference<Integer> cellid = new AtomicReference<>(0);
                Arrays.asList(customData.get(key)).forEach(obj -> {
                    Cell cell = row.createCell(cellid.getAndSet(cellid.get() + 1));
                    cell.setCellValue(obj != null ? obj.toString() : "");
                });
            });

            workbook.write(outputStream);
        }
        TaskScheduler.deleteTempFilesOnSchedule(file, DateUtils.addTimeInMins(1));
        return FileResponse.createFileResponse(file);
    }

    /**
     * It will prepare custom data for writing data into excel file
     *
     * @param data
     * @return
     */

    private static Map<String, Object[]> prepareCustomdata(List<Map<String, Object>> data) {
        Map<String, Object[]> customData = new LinkedHashMap<>();
        customData.put("1", data.get(0).keySet().toArray());
        AtomicReference<Integer> count = new AtomicReference<>(2);
        data.forEach(object -> {
            customData.put(count.get().toString(), object.values().toArray());
            count.getAndSet(count.get() + 1);
        });
        return customData;
    }

    /**
     * It will generate a pdf file based on the data
     *
     * @param data
     * @param payload
     * @param pdfPassword
     * @return
     */

    public static FileResponse generateReportAsPdf(List<Map<String, Object>> data, MISPayload payload, String pdfPassword) {
        File file = prepareTempFile("MIS", FileConstants.PDF);

        if (CollectionUtils.isEmpty(data))
            return generateEmptyPdf(file, payload);

        List<String> columnNames = new ArrayList<>(data.get(0).keySet());
        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));

            allocatePageSizeForDocument(columnNames, document);

            document.open();

            addLogoToPdf(document, payload.getTempLogoImagePath(), payload.isLogoRequired());

            addDocumentTitle(document, payload);

            createTable(document, columnNames, data, payload);

            addFooter(document, payload.getMisName());

            addThankYouLogo(document);

            document.close();

            preparePdfPassword(pdfPassword, file);
        } catch (FileNotFoundException e) {
            logger.error("File Not Found ==> Please make sure whether file contains in this path and please check the permissions for this directory", e);
            return new FileResponse().
                    setMessage(MessageConstants.SERVICE_FAILED).
                    setStatus(PortableConstants.FAILED);
        } catch (DocumentException ex) {
            logger.error(LoggerErrorConstants.MIS_REPORT_ERROR, ex);
            return new FileResponse().
                    setMessage(MessageConstants.SERVICE_FAILED).
                    setStatus(PortableConstants.FAILED);
        } catch (Exception exe) {
            logger.error(LoggerErrorConstants.MIS_REPORT_ERROR, exe);
            return new FileResponse().
                    setMessage(MessageConstants.SERVICE_FAILED).
                    setStatus(PortableConstants.FAILED);
        }
        TaskScheduler.deleteTempFilesOnSchedule(file, DateUtils.addTimeInMins(1));
        return FileResponse.createFileResponse(file);
    }

    /**
     * It prepares pdf password if pdfPassword non empty
     *
     * @param pdfPassword
     * @param file
     * @throws IOException
     */

    public static void preparePdfPassword(String pdfPassword, File file) throws IOException {
        if (StringUtils.isEmpty(pdfPassword))
            return;

        String absolutePath;
        try (InputStream inputStream = PdfEncriptionUtils.protectPdfPassword(PdfEncriptionUtils.getInputStream(file), pdfPassword)) {

            absolutePath = file.getAbsolutePath();

            Files.delete(file.toPath());

            convertInputStreamToFile(new File(absolutePath), inputStream);
        }

    }

    /**
     * Preparing empty pdf
     *
     * @param file
     * @param payload
     * @return
     */

    private static FileResponse generateEmptyPdf(File file, MISPayload payload) {
        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));

            document.open();

            addLogoToPdf(document, payload.getTempLogoImagePath(), payload.isLogoRequired());

            addDocumentTitle(document, payload);

            addEmptyMessageToPdf(payload, document);

            addFooter(document, payload.getMisName());

            addThankYouLogo(document);

            document.close();
        } catch (FileNotFoundException e) {
            logger.error("File Not Found ==> Please make sure whether file contains in this path and please check the permissions for this directory", e);
        } catch (DocumentException ex) {
            logger.error(LoggerErrorConstants.MIS_REPORT_ERROR, ex);
        } catch (IOException ex) {
            logger.info("While preparing the logo it throws error", ex);
        } catch (Exception exe) {
            logger.error(LoggerErrorConstants.MIS_REPORT_ERROR, exe);
        }
        TaskScheduler.deleteTempFilesOnSchedule(file, DateUtils.addTimeInMins(1));
        return FileResponse.createFileResponse(file);
    }

    private static void addThankYouLogo(Document document) throws DocumentException, IOException {
        Image img = Image.getInstance(getStaticFileAsBytes("portabull.png"));
        img.scalePercent(7, 7);
        img.setAlignment(Element.ALIGN_RIGHT);
        document.add(img);
    }

    /**
     * Adding empty message for pdf
     *
     * @param payload
     * @param document
     * @throws DocumentException
     */

    private static void addEmptyMessageToPdf(MISPayload payload, Document document) throws DocumentException {

        Paragraph paragraph = new Paragraph();

        String reportName = payload.getMisName() != null ? payload.getMisName().trim() : "";
        Paragraph space = new Paragraph();
        leaveEmptyLine(space, 3);
        document.add(space);

        paragraph.setAlignment(Element.ALIGN_CENTER);
        String message = "No data found for ";
        if (StringUtils.isEmpty(reportName)) {
            message = com.portabull.utils.datautils.StringUtils.append(message, "this report");
        } else {
            if (reportName.toLowerCase().endsWith("report")) {
                message = com.portabull.utils.datautils.StringUtils.append(message, reportName);
            } else {
                message = com.portabull.utils.datautils.StringUtils.append(message, reportName, " Report");
            }
        }

        paragraph.add(new Chunk(message, new Font(Font.FontFamily.HELVETICA,
                14, Font.ITALIC, new BaseColor(
                49, 167, 178))));

        document.add(paragraph);
    }

    /**
     * It will create tempFile
     *
     * @param aliasName
     * @param extension
     * @return
     */

    public static File prepareTempFile(String aliasName, String extension) {
        return new File(com.portabull.utils.datautils.StringUtils.append(prepareTempPath(), File.separator, prepareTempName(aliasName, extension)));
    }

    /**
     * It will create a temp file name
     *
     * @param aliasName
     * @param extension
     * @return
     */

    public static String prepareTempName(String aliasName, String extension) {
        return new StringBuffer(aliasName != null ? aliasName : "Doc").append("_").append(new Date().getTime()).append("_")
                .append(new Random().nextInt(1000))
                .append(extension.contains(".") ? extension.toLowerCase() : "." + extension.toLowerCase()).toString();
    }

    /**
     * It will give the temp directory if not created in os it will create
     *
     * @return
     */

    public static String prepareTempPath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!new File(tmpDir).exists()) {
            new File(tmpDir).mkdirs();
        }
        return tmpDir;
    }

    /**
     * Allocating the Pdf page size
     *
     * @param columnNames
     * @param document
     */

    private static void allocatePageSizeForDocument(List<String> columnNames, Document document) {

        if (columnNames.size() > 15) {
            document.setPageSize(new RectangleReadOnly(1700.0F, 1400F));
            return;
        }

        switch (columnNames.size()) {
            case 6:
                document.setPageSize(new RectangleReadOnly(800.0F, 500F));
                return;
            case 7:
                document.setPageSize(new RectangleReadOnly(900.0F, 600F));
                return;
            case 8:
                document.setPageSize(new RectangleReadOnly(1000.0F, 700F));
                return;
            case 9:
                document.setPageSize(new RectangleReadOnly(1100.0F, 800F));
                return;
            case 10:
                document.setPageSize(new RectangleReadOnly(1200.0F, 900F));
                return;
            case 11:
                document.setPageSize(new RectangleReadOnly(1300.0F, 1000F));
                return;
            case 12:
                document.setPageSize(new RectangleReadOnly(1400.0F, 1100F));
                return;
            case 13:
                document.setPageSize(new RectangleReadOnly(1500.0F, 1200F));
                return;
            case 14:
                document.setPageSize(new RectangleReadOnly(1600.0F, 1300F));
                return;
            default:
                break;
        }
    }

    /**
     * It will add the logo to pdf if @param logoCompletePath is empty then default image will be loaded(portabull.png) for pdf only
     *
     * @param document
     * @param logoCompletePath
     * @param isLogoRequired
     */

    private static void addLogoToPdf(Document document, String logoCompletePath, boolean isLogoRequired) throws IOException, DocumentException {

        if (!isLogoRequired)
            return;

        if (StringUtils.isEmpty(logoCompletePath))
            logoCompletePath = "portabull.png";

        byte[] bytes;
        try {
            bytes = getStaticFileAsBytes(logoCompletePath);
        } catch (Exception e) {
            bytes = FileResponse.getBytes(logoCompletePath);
        }
        Image img = Image.getInstance(bytes);
        img.scalePercent(7, 7);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

    }

    /**
     * Adding the document tittle here for pdf
     *
     * @param document
     * @param payload
     * @throws DocumentException
     */

    private static void addDocumentTitle(Document document, MISPayload payload) throws DocumentException {

        Paragraph paragraph = new Paragraph();

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.BLACK);

        Chunk chunk = new Chunk(payload.getDocumentTittle(), font);

        paragraph.add(chunk);

        paragraph.setAlignment(Element.ALIGN_CENTER);

        document.add(paragraph);
        if (payload.isTimeStampRequired()) {
            Paragraph timeParagraph = new Paragraph();
            leaveEmptyLine(timeParagraph, 1);
            String timeStamp = new StringBuffer("Report generated on ").append(!StringUtils.isEmpty(payload.getTimeStampFormat()) ?
                    DateUtils.getTimeAsString(payload.getTimeStampFormat()) :
                    DateUtils.getDefaultTime()).toString();
            timeParagraph.add(timeStamp);
            document.add(timeParagraph);
        }


    }

    /**
     * It will create a table based on @param columnNames and @param data for pdf
     *
     * @param document
     * @param columnNames
     * @param data
     * @param payload     we have the color code in payload based on the headerColorCode, header color will be decided
     * @throws DocumentException
     */

    private static void createTable(Document document, List<String> columnNames, List<Map<String, Object>> data, MISPayload payload) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(columnNames.size());

        columnNames.forEach(column -> {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(255, 255, 51));
            if (!StringUtils.isEmpty(payload.getHeaderColorCode())) {
                Color color = Color.decode(payload.getHeaderColorCode());
                cell.setBackgroundColor(new BaseColor(color.getRed(), color.getGreen(), color.getBlue()));
            }
            table.addCell(cell);
        });

        table.setHeaderRows(1);
        prepareValues(table, data);
        document.add(table);
    }

    /**
     * It will generate the table values for pdf
     *
     * @param table
     * @param data
     */

    private static void prepareValues(PdfPTable table, List<Map<String, Object>> data) {
        data.forEach(row ->
                row.forEach((key, value) -> {
                    table.setWidthPercentage(100);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(value != null ? value.toString() : "");
                })
        );
    }

    /**
     * Adding footers for pdf
     *
     * @param document
     * @param reportName
     * @throws DocumentException
     */

    private static void addFooter(Document document, String reportName) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        reportName = reportName != null ? reportName : "";
        Paragraph space = new Paragraph();
        leaveEmptyLine(space, 3);
        document.add(space);

        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add("End of the " + reportName.trim() + " Report \r\nThank You");
        document.add(paragraph);
    }

    /**
     * It will create a empty lines for pdf
     *
     * @param paragraph
     * @param number
     */

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    /**
     * It will take from static files from resources folder
     *
     * @param fileName
     * @return
     * @throws IOException
     */

    public static byte[] getStaticFileAsBytes(String fileName) throws IOException {
        ClassPathResource cpr = new ClassPathResource(fileName);
        return FileCopyUtils.copyToByteArray(cpr.getInputStream());
    }

    /**
     * It will take from static files from resources folder
     *
     * @param fileName
     * @return
     * @throws IOException
     */

    public static InputStream getStaticFileAsInputstream(String fileName) throws IOException {
        return new ClassPathResource(fileName).getInputStream();
    }

    /**
     * It will convert multipart to a temp file
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */

    public static File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File file = prepareTempFile("multipartFile", FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        return file;
    }

    /**
     * To delete the file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(File file) throws IOException {
        Files.delete(file.toPath());
        return true;
    }

    /**
     * It will create temp file in temp location based on alias name and extension
     *
     * @param aliasName
     * @param extension
     * @return
     */

    public static File createTempFile(String aliasName, String extension) {
        return new File(prepareTempPath() + File.separator + prepareTempName(aliasName, extension));
    }

    public static File convertBytesToFile(File tempFile, byte[] bytes) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(bytes);
        }
        return tempFile;
    }

    public static File convertInputStreamToFile(File tempFile, InputStream inputStream) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(IOUtils.toByteArray(inputStream));
        }
        return tempFile;
    }

    private static String prepareTempName(String defaultFileName) {
        return new StringBuilder("Doc").append("_").
                append(new Date().getTime()).append("_").
                append(new Random().nextInt(1000)).append(defaultFileName).toString();
    }

    public static File createTempFile(String defaultFileName) {
        return new File(prepareTempPath() + File.separator + prepareTempName(defaultFileName));
    }

    public static String prepareRandomFileName(String fileName) {
        return new StringBuilder(String.valueOf(new Date().getTime())).
                append(new Random().nextInt(1010)).
                append(new Random().nextInt(1010))
                .append(fileName).toString();
    }

    public static FileResponse generateReportAsText(MISTextResponse data, MISPayload payload) {
        File file = prepareTempFile("MIS", FileConstants.TEXT);

        String line = getLine(data.getColumnLength());

        StringBuilder fullData = new StringBuilder(prepareDocumentTittle(payload)).append(prepareColumnHeaders(data.getColumnLength()));

        data.getQueryResult().forEach(column -> {
            column.forEach((k, v) ->
                    prepareColumnData(k, v != null ? v.toString() : "", data.getColumnLength(), fullData)
            );
            fullData.append(BasicConstants.PIPE);
            fullData.append(BasicConstants.NEWLINE);
            fullData.append(line);
        });

        addFooter(fullData, payload);
        logger.info("Mis Table {} ", fullData);

        try {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(fullData.toString());
            }
        } catch (IOException e) {
            logger.error("While writing the text to file it throws error", e);
        }

        TaskScheduler.deleteTempFilesOnSchedule(file, DateUtils.addTimeInMins(1));
        return FileResponse.createFileResponse(file);
    }

    private static void addFooter(StringBuilder fullData, MISPayload payload) {

        fullData.append(BasicConstants.NEWLINE).append(BasicConstants.NEWLINE)
                .append("End of the ").append(payload.getMisName() != null ? payload.getMisName() : "")
                .append(" Report \r\nThank You").append(BasicConstants.NEWLINE);
    }

    private static String prepareDocumentTittle(MISPayload payload) {

        String timeStamp = new StringBuffer("Report generated on ").append(!StringUtils.isEmpty(payload.getTimeStampFormat()) ?
                DateUtils.getTimeAsString(payload.getTimeStampFormat()) :
                DateUtils.getDefaultTime()).toString();

        return new StringBuilder().append(timeStamp).append(BasicConstants.NEWLINE).append(payload.getDocumentTittle())
                .append(BasicConstants.NEWLINE)
                .append(BasicConstants.NEWLINE)
                .append(BasicConstants.NEWLINE).toString();

    }


    private static String getLine(Map<String, Integer> columnNames) {

        StringBuilder builderLine = new StringBuilder();

        columnNames.forEach((k, v) ->
                builderLine.append(BasicConstants.PLUS).append(String.join(BasicConstants.EMPTY, Collections.nCopies(v + 4, BasicConstants.HYPHEN)))
        );
        builderLine.append(BasicConstants.PLUS);
        builderLine.append(BasicConstants.NEWLINE);
        return builderLine.toString();
    }


    private static String prepareColumnHeaders(Map<String, Integer> columnNames) {

        StringBuilder builderLine = new StringBuilder();

        StringBuilder columnNameBuilder = new StringBuilder();

        columnNames.forEach((k, v) -> {
            builderLine.append(BasicConstants.PLUS).append(String.join(BasicConstants.EMPTY, Collections.nCopies(v + 4, BasicConstants.HYPHEN)));
            columnNameBuilder.append(BasicConstants.PIPE).append(k).
                    append(String.join(BasicConstants.EMPTY, Collections.nCopies(k.length() > v ? k.length() - v : v - k.length() + 4, BasicConstants.SPACE)));
        });
        builderLine.append(BasicConstants.PLUS);
        builderLine.append(BasicConstants.NEWLINE);
        columnNameBuilder.append(BasicConstants.PIPE);


        return new StringBuilder(builderLine.toString()).append(columnNameBuilder.toString()).append(BasicConstants.NEWLINE).append(builderLine.toString()).toString();

    }

    private static void prepareColumnData(String columnName, String columnValue, Map<String, Integer> columnLength, StringBuilder fullData) {
        fullData.append(BasicConstants.PIPE).append(columnValue).append(String.join(BasicConstants.EMPTY,
                Collections.nCopies(columnValue.length() > columnLength.get(columnName) ? columnValue.length() - columnLength.get(columnName) :
                        columnLength.get(columnName) - columnValue.length() + 4, BasicConstants.SPACE)));
    }

    /**
     * It will generate a json file based on given object
     *
     * @param data
     * @return
     */

    public static FileResponse generateReportAsJSON(Object data) {
        File file = prepareTempFile("MIS", FileConstants.JSON);

        try {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(JsonUtils.beautiyJson(data));
            }
        } catch (IOException e) {
            logger.error("While writing the json into file it throws error", e);
        }

        TaskScheduler.deleteTempFilesOnSchedule(file, DateUtils.addTimeInMins(1));
        return FileResponse.createFileResponse(file);
    }

    /**
     * It prepares pdf password
     *
     * @param pdfPassword
     * @param multipartFile
     * @throws IOException
     */

    public static File preparePdfPassword(String pdfPassword, MultipartFile multipartFile) throws IOException {
        File file;

        try (InputStream inputStream = PdfEncriptionUtils.protectPdfPassword(new ByteArrayInputStream(multipartFile.getBytes()), pdfPassword)) {
            file = createTempFile(multipartFile.getOriginalFilename());
            convertInputStreamToFile(file, inputStream);
        }
        return file;
    }

    /**
     * It removes pdf password
     *
     * @param pdfPassword
     * @param multipartFile
     * @throws IOException
     */

    public static File removePdfPassword(String pdfPassword, MultipartFile multipartFile) throws IOException {
        File file;

        try (InputStream inputStream = PdfEncriptionUtils.removePdfPassword(new ByteArrayInputStream(multipartFile.getBytes()), pdfPassword)) {
            file = createTempFile(multipartFile.getOriginalFilename());
            convertInputStreamToFile(file, inputStream);
        }
        return file;
    }

    /**
     * It prepares pdf password if pdfPassword non empty
     *
     * @param pdfPassword
     * @param file
     * @throws IOException
     */

    public static void removePdfPassword(String pdfPassword, File file) throws IOException {
        if (StringUtils.isEmpty(pdfPassword))
            return;

        String absolutePath;
        try (InputStream inputStream = PdfEncriptionUtils.removePdfPassword(PdfEncriptionUtils.getInputStream(file), pdfPassword)) {

            absolutePath = file.getAbsolutePath();

            Files.delete(file.toPath());

            convertInputStreamToFile(new File(absolutePath), inputStream);
        }

    }

    /**
     * It will give new InputStream
     *
     * @param file
     * @return
     * @throws IOException
     */

    public static InputStream getInputStream(File file) throws IOException {
        InputStream inputStream;
        try (InputStream fileInputStream = new FileInputStream(file)) {
            inputStream = new ByteArrayInputStream(IOUtils.toByteArray(fileInputStream));
        }
        return inputStream;
    }

    public static File convertBase64File(String file, String fileName) throws IOException {

        byte[] data = Base64.getDecoder().decode(file.split(",")[1]);

        File tempFile = createTempFile(fileName);

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            outputStream.write(data);
        }

        return tempFile;
    }

    public static byte[] convertFileToBytes(File file) throws IOException {
        try (InputStream fileInputStream = new FileInputStream(file)) {
            return IOUtils.toByteArray(fileInputStream);
        }
    }
}
