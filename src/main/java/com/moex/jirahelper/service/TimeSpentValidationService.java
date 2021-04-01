package com.moex.jirahelper.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.moex.jirahelper.api.JiraClient;
import com.moex.jirahelper.service.model.TimeSpentReport;
import com.moex.jirahelper.service.model.TimeSpentUnit;
import com.moex.jirahelper.service.model.UserReport;
import com.moex.jirahelper.service.model.ValidationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TimeSpentValidationService {

    @Value("${sprintName}")
    private String sprintName;

    @Value("#{T(java.time.LocalDate).parse('${sprintReportStartDate}')}")
    private LocalDate sprintReportStartDate;

    @Value("${users}")
    private Set<String> users;

    @Autowired
    private JiraClient jiraClient;

    @Autowired
    private PrintValidationResultService printer;

    public void run() throws ExecutionException, InterruptedException {
        List<ValidationInfo> results = new ArrayList<>();
        results.add(new ValidationInfo(false, "checking time spent worklogs in sprint " + sprintName +
                " from date " + sprintReportStartDate, 0));

        LocalDate endDate = LocalDate.now();
        List<LocalDate> dates = sprintReportStartDate.datesUntil(endDate)
                .filter(date -> date.getDayOfWeek()!= DayOfWeek.SATURDAY && date.getDayOfWeek()!=DayOfWeek.SUNDAY)
                .collect(Collectors.toList());

        TimeSpentReport report = fillReport(dates);
        checkMyUsers(report, dates, results);
        checkNotMyUsers(report, dates, results);
        printer.add(results);
    }

    private void checkNotMyUsers(TimeSpentReport report, List<LocalDate> dates, List<ValidationInfo> results) {
        // проверить, что списаний от неотслеживаемых пользователей нет
        List<UserReport> notMyUsersWorklogs = report.getReportForNotMyUsers(users);
        if (notMyUsersWorklogs.isEmpty()) {
            results.add(new ValidationInfo(false, "no other users logged time to sprint " + sprintName, 1));
        } else {
            for (UserReport notMyUsersWorklog : notMyUsersWorklogs) {
                results.add(new ValidationInfo(true, "not our time tracking user " + notMyUsersWorklog.getName() + " logged time to sprint " + sprintName, 1));
                for (LocalDate date : dates) {
                    List<TimeSpentUnit> timeSpentUnits = notMyUsersWorklog.getUserDateReports(date);
                    if (!timeSpentUnits.isEmpty()) {
                        results.add(new ValidationInfo(true, "exists worklog at " + date, 2));
                        for (TimeSpentUnit userDateReport : timeSpentUnits) {
                            results.add(new ValidationInfo(true, "spent " + userDateReport.getMinutesSpent()
                                    + " minutes on " + userDateReport.getIssueUrl() + " " + userDateReport.getIssueTitle() + "\"", 3));
                        }
                    }
                }

            }
        }
    }

    private void checkMyUsers(TimeSpentReport report, List<LocalDate> dates, List<ValidationInfo> results) {
        // проверить, что по всем тикетам за каждый день в интервале есть списания.
        for (String user : users) {
            results.add(new ValidationInfo(false, "check worklogs for " + user, 1));
            for (LocalDate date : dates) {
                List<TimeSpentUnit> timeSpentUnits = report.getUserDateReports(user, date);
                if (timeSpentUnits.isEmpty()) {
                    results.add(new ValidationInfo(true, "no worklog at " + date, 2));
                } else {
                    results.add(new ValidationInfo(false, "exists worklog at " + date, 2));
                    for (TimeSpentUnit userDateReport : timeSpentUnits) {
                        results.add(new ValidationInfo(false, "spent " + userDateReport.getMinutesSpent()
                                + " minutes on " + userDateReport.getIssueUrl() + " \"" + userDateReport.getIssueTitle() + "\"", 3));
                    }
                }
            }
        }
    }

    private TimeSpentReport fillReport(List<LocalDate> dates) throws ExecutionException, InterruptedException {
        TimeSpentReport report = new TimeSpentReport(dates);
        Iterable<Issue> issuesInSprint = jiraClient.searchJql("Sprint = " + sprintName);
        for (Issue issue: issuesInSprint) {
            issue = jiraClient.refreshIssue(issue);
            report.add(jiraClient.getIssueUrl(issue), issue);
        }
        return report;
    }

}
