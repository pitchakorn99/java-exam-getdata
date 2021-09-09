package com.example.exam.getdatajava8.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "tbl_data")
public class Data {
    @Id
    private int id;

    private long threadId;
    private String employeeId;
    private int albumId;
    private String title;
    private String url;
    private String thumbnailUrl;
}
