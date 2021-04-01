package com.moex.jirahelper;

import com.moex.jirahelper.api.GitlabClient;
import com.moex.jirahelper.service.FullValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JiraHelperApplication implements CommandLineRunner {

	@Autowired
	private FullValidationService fullValidationService;

/*	@Autowired
	private GitlabClient gitlabClient;*/

	public static void main(String[] args) {
		SpringApplication.run(JiraHelperApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fullValidationService.run();


	}

}
