package com.moex.jirahelper.service.model;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
public class TimeSpentReport {

    private final Map<String, UserReport> usersWorklogs = new HashMap<>();
    private final List<LocalDate> dates;

    public void add(String issueUrl, Issue issue) {
        for (Worklog worklog : issue.getWorklogs()) {
            String name = worklog.getAuthor().getName();
            usersWorklogs.compute(name, (key, userReport) -> {
                if (userReport == null) {
                    userReport = new UserReport(name, dates);
                }
                userReport.add(issue.getSummary(), issueUrl, worklog);
                return userReport;
            });
        }
    }

    public List<TimeSpentUnit> getUserDateReports(String user, LocalDate date) {
        UserReport result = usersWorklogs.get(user);
        return result == null ? Collections.emptyList() : result.getUserDateReports(date) ;
    }

    public List<UserReport> getReportForNotMyUsers(Set<String> myUsers) {
        List<UserReport> result = new ArrayList<>();
        for (Map.Entry<String, UserReport> entry : usersWorklogs.entrySet()) {
            if (!myUsers.contains(entry.getKey())) {
                UserReport userReport = entry.getValue();
                if (!userReport.isEmpty()) {
                    result.add(userReport);
                }
            }
        }
        return result;
    }
}
