package com.portabull.utils.fileutils;

import com.portabull.execption.PortableException;
import com.portabull.utils.JsonUtils;
import com.portabull.utils.objectutils.ObjectHandling;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ExcelUtils {

    public Map<String, Object> readExcelFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return this.readExcelFile(fileInputStream);
    }

    public Map<String, Object> readExcelFile(String completeFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(completeFilePath));
        return this.readExcelFile(fileInputStream);
    }

    public Map<String, Object> readExcelFile(MultipartFile multipartFile) throws PortableException, IOException {

        validate(multipartFile);

        File file = FileHandling.convertMultipartToFile(multipartFile);

        FileInputStream fileInputStream = new FileInputStream(file);

        Map<String, Object> excelFileData = this.readExcelFile(fileInputStream);

        FileHandling.deleteFile(file);

        return excelFileData;
    }

    public String readExcelFileAsJsonString(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return JsonUtils.getJsonString(this.readExcelFile(fileInputStream));
    }

    public String readExcelFileAsJsonString(String completeFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(completeFilePath));
        return JsonUtils.getJsonString(this.readExcelFile(fileInputStream));
    }

    public String readExcelFileAsJsonString(MultipartFile multipartFile) throws PortableException, IOException {

        validate(multipartFile);

        File file = FileHandling.convertMultipartToFile(multipartFile);

        FileInputStream fileInputStream = new FileInputStream(file);

        Map<String, Object> excelFileData = this.readExcelFile(fileInputStream);

        FileHandling.deleteFile(file);

        return JsonUtils.getJsonString(excelFileData);
    }

    public String readExcelFileAsJsonString(InputStream fileInputStream) throws IOException {
        return JsonUtils.getJsonString(readExcelFile(fileInputStream));
    }

    public Map<String, Object> readExcelFile(InputStream fileInputStream) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        Map<String, Object> workBookData = new HashMap<>();

        for (int sheetNumber = 0; sheetNumber < workbook.getNumberOfSheets(); sheetNumber++) {

            XSSFSheet sheet = workbook.getSheetAt(sheetNumber);

            List<Map<String, Object>> execlData = new ArrayList<>();


            List<Row> rows = new ArrayList<>();
            sheet.iterator().forEachRemaining(rows::add);


            List<Cell> columnCells = new ArrayList<>();
            rows.get(0).cellIterator().forEachRemaining(columnCells::add);
            rows.remove(0);


            List<String> columnNames = new ArrayList<>();
            prepareColumns(columnNames, columnCells);


            rows.forEach(row -> {
                List<Cell> cells = new ArrayList<>();

                row.cellIterator().forEachRemaining(cells::add);

                if (columnNames.size() == cells.size()) {

                    for (int columnIndex = 0; columnIndex < cells.size(); columnIndex++) {
                        Cell cell = cells.get(columnIndex);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                prepareRow(columnNames, DateUtil.isCellDateFormatted(cell) ?
                                        cell.getDateCellValue() : cell.getNumericCellValue(), columnIndex, execlData);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                prepareRow(columnNames, cell.getStringCellValue(), columnIndex, execlData);
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                prepareRow(columnNames, cell.getBooleanCellValue(), columnIndex, execlData);
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                prepareRow(columnNames, cell.getErrorCellValue(), columnIndex, execlData);
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                prepareRow(columnNames, cell.getCellFormula(), columnIndex, execlData);
                                break;
                            default:
                                break;
                        }
                    }

                }

            });

            fileInputStream.close();

            workBookData.put((com.portabull.utils.datautils.StringUtils.append("workbook", (sheetNumber + 1))), execlData);
        }


        return workBookData;
    }


    private void prepareColumns(List<String> columnNames, List<Cell> columnCells) {
        columnCells.forEach(cell -> {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    columnNames.add(String.valueOf(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_STRING:
                    columnNames.add(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    columnNames.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                case Cell.CELL_TYPE_ERROR:
                    columnNames.add(String.valueOf(cell.getErrorCellValue()));
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    columnNames.add(String.valueOf(cell.getCellFormula()));
                    break;
                default:
                    break;
            }
        });

    }

    private void prepareRow(List<String> columnNames, Object cellValue, int columnID, List<Map<String, Object>> execlData) {

        if (CollectionUtils.isEmpty(columnNames))
            return;

        if (ObjectHandling.isNullObject(cellValue))
            cellValue = "";

        if (cellValue instanceof Date)
            cellValue = cellValue.toString();

        if (columnID == 0) {
            Map<String, Object> data = new HashMap<>();
            data.put(columnNames.get(columnID), cellValue);
            execlData.add(data);
        }

        execlData.get(execlData.size() - 1).put(columnNames.get(columnID), cellValue);

    }

    private void validate(MultipartFile file) throws PortableException {
        if (StringUtils.isEmpty(file.getOriginalFilename()))
            throw new PortableException("File Name should not be empty");

        if (!FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), "xls")
                || !FilenameUtils.isExtension(file.getOriginalFilename().toLowerCase(), "xlsx"))
            throw new PortableException("File Format Should be Excel only");
    }


}
