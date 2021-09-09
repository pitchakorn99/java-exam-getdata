package com.example.exam.getdatajava8;

import com.example.exam.getdatajava8.entity.Data;
import com.example.exam.getdatajava8.services.GetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Getdatajava8Application implements ApplicationRunner {

	@Autowired
	private GetDataService getDataService;

	public static String runService;
	public static String employeeId;

	public static void main(String[] args) {
		System.out.println(">>>>>>>>>>>>>>> args length = " + args.length);
		if(args != null && args.length > 0){
			runService = args[0].toLowerCase();
			if(args.length >=1 && !args[1].isEmpty()){
				employeeId = args[1].toLowerCase();
			};
		};

		SpringApplication.run(Getdatajava8Application.class, args);
		//if(runService == null || employeeId == null) System.exit(0);
	}

	@Override
	public void run(ApplicationArguments arg0) {
		System.out.println("runService="+runService+" | employeeId="+employeeId);
		try{
			if(runService.equals("getdata")){
				getDataService.setEmployeeId(employeeId);
				getDataService.startGetData();
			}
			System.exit(1);
		}catch (Exception ex){
			ex.printStackTrace();
			System.exit(2);
		}
	}
}
