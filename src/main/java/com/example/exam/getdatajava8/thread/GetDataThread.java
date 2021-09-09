package com.example.exam.getdatajava8.thread;

import com.example.exam.getdatajava8.entity.Data;
import com.example.exam.getdatajava8.repository.GetDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class GetDataThread extends Thread {

    private final RestTemplate restTemplate;

    private GetDataRepository getDataRepository;

    private Thread thread;

    private String employeeId;
    private int id;
    private String url;

    public GetDataThread(GetDataRepository getDataRepository, String employeeId, int id, String url) {

        this.getDataRepository = getDataRepository;
        this.employeeId = employeeId;
        this.id = id;
        this.url = url;

        RestTemplateBuilder builder = new RestTemplateBuilder();

        this.restTemplate = builder.build();
    }

    @Transactional
    @Override
    public void run() {
        //System.out.println("Thread running : " + id);
        try {
            long threadId = Thread.currentThread().getId();
            Optional<Data> response = Optional.ofNullable(restTemplate.getForObject(url, Data.class));
            if(response.isPresent()){
                response.get().setEmployeeId(this.employeeId);
                response.get().setThreadId(threadId);
                this.getDataRepository.save(response.get());
                System.out.println(response.get().getThumbnailUrl());
            }
        } catch (Exception e) {
            System.out.println("Thread has been interrupted " + e.toString());
        }
    }
}