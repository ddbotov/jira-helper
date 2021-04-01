package com.moex.jirahelper.service;

import com.moex.jirahelper.service.model.ValidationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PrintValidationResultService {

    public void print(List<ValidationInfo> results) {
        int errorCounter = 0;
        StringBuilder sb = new StringBuilder();
        for (ValidationInfo result : results) {
            sb.append("  ".repeat(result.getLevel()));
            sb.append(result.getMessage());
            if (result.isError() ) {
                sb.append(" (ERROR)");
                errorCounter++;
            }
            sb.append("\n");
        }
        log.info("\nerrors count: " + errorCounter + "\n" + sb.toString());
    }

    public void add(List<ValidationInfo> results) {
        print(results);
    }
}
