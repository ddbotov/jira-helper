package com.moex.jirahelper.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.moex.jirahelper.api.JiraClient;
import com.moex.jirahelper.service.model.ValidationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class LabelsValidationService {

    @Autowired
    private JiraClient jiraClient;

    @Autowired
    private PrintValidationResultService printer;

    public void run() throws ExecutionException, InterruptedException {
        List<ValidationInfo> results = new ArrayList<>();
        results.add(new ValidationInfo(false, "checking labels", 0));
        Iterable<Issue> badDueDatesIssues = jiraClient.searchJql(
                "project in (ICDBJ) AND type not in (EPIC) AND (labels is EMPTY or labels  not in (BA, DEV, ANALYTICS, ARC, TEST, ORG, DEVOPS))");
        for (Issue issue: badDueDatesIssues) {
            results.add(new ValidationInfo(true, "bad labels in " + jiraClient.getIssueUrl(issue), 1));
        }
        if (results.size() == 1) {
            results.add(new ValidationInfo(false, "labels are fine", 1));
        }
        printer.add(results);
    }

}
