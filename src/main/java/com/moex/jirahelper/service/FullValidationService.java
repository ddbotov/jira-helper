package com.moex.jirahelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class FullValidationService {

    @Autowired
    private LabelsValidationService labelsValidationService;

    @Autowired
    private TimeSpentValidationService timeSpentValidationService;

    public void run() throws ExecutionException, InterruptedException {
        //labelsValidationService.run();
        timeSpentValidationService.run();
    }

}
