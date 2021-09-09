package com.example.exam.getdatajava8.services;

import com.example.exam.getdatajava8.entity.Data;
import com.example.exam.getdatajava8.repository.GetDataRepository;
import com.example.exam.getdatajava8.thread.GetDataThread;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class GetDataService {

    private GetDataRepository getDataRepository;

    private final RestTemplate restTemplate;
    private String apiHost;

    private String employeeId;

    private static final String CSV_SEPARATOR = ",";

    @Autowired
    public GetDataService(
            RestTemplateBuilder builder,
            @Value("${external_api_url}") String apiHost,
            GetDataRepository getDataRepository
    ) {
        this.restTemplate = builder.build();
        this.apiHost = apiHost;
        this.getDataRepository = getDataRepository;

    }

    @SneakyThrows
    public void startGetData() {
        for (int i = 0; i < 250; i++) {
            String url = String.format("%s/%d", apiHost, i);
            GetDataThread getDataThread = new GetDataThread(this.getDataRepository, employeeId, i, url);
            getDataThread.join();
            getDataThread.start();
        }

        System.out.println(getDataRepository.count());
        exportCsv();
        exportExcel();
    }

    public void setEmployeeId(String empId) {
        this.employeeId = empId;
    }

    public List<Data> listData() {
        return getDataRepository.findAll();
    }

    public void exportCsv() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("export_data.csv"), "UTF-8"));
            // Set header
            StringBuffer header = new StringBuffer();
            header.append("EmployeeId");
            header.append(CSV_SEPARATOR);
            header.append("ThreadId");
            header.append(CSV_SEPARATOR);
            header.append("ID");
            header.append(CSV_SEPARATOR);
            header.append("AlbumId");
            header.append(CSV_SEPARATOR);
            header.append("Title");
            header.append(CSV_SEPARATOR);
            header.append("Url");
            header.append(CSV_SEPARATOR);
            header.append("ThumbnailUrl");
            header.append(CSV_SEPARATOR);
            header.append("Error");
            bw.write(header.toString());
            bw.newLine();
            // End Header
            List<Data> dataList = listData();
            for (Data data : dataList) {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(data.getEmployeeId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getThreadId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getAlbumId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getTitle());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getUrl());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(data.getThumbnailUrl());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append("");
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void exportExcel() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

        List<Data> dataList = this.listData();

        int rowNum = 0;
        int colNum = 0;
        System.out.println("Creating excel");

        // Set Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headerCol = new String[]{"EmployeeId", "ThreadId", "ID", "AlbumId", "Title", "Url", "ThumbnailUrl"};
        for (Object field : headerCol){
            Cell headerCell = headerRow.createCell(colNum++);
            headerCell.setCellValue((String) field);
        }
        // End Header

        for (Data data : dataList) {
            Row row = sheet.createRow(rowNum++);
            Object[] tempColArr = new Object[]{data.getEmployeeId(), data.getThreadId(), data.getId(), data.getAlbumId(), data.getTitle(), data.getUrl(), data.getThumbnailUrl()};
            colNum = 0;
            for (Object field : tempColArr) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Long) {
                    cell.setCellValue((Long) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream("export_data.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
