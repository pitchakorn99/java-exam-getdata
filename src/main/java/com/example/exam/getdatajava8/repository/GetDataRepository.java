package com.example.exam.getdatajava8.repository;

import com.example.exam.getdatajava8.entity.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GetDataRepository extends JpaRepository<Data, Integer> {

}
