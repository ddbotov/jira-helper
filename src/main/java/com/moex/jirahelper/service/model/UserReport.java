package com.moex.jirahelper.service.model;

import com.atlassian.jira.rest.client.api.domain.Worklog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public class UserReport {

    private final String name;
    private final List<LocalDate> dates;
    private final HashMap<LocalDate, List<TimeSpentUnit>> timeSpentUnitsMap = new HashMap<>();

    public void add(String issueTitle, String issueUrl, Worklog worklog) {
        LocalDate date = toLocalDate(worklog.getStartDate());
        if (!dates.contains(date)) {
            log.trace("worklog for " + date + " from " + name + " in " + issueUrl + " not in sprint");
            return;
        }
        timeSpentUnitsMap.compute(date, (key, timeSpentUnits) -> {
            if (timeSpentUnits == null) {
                timeSpentUnits = new ArrayList<>();
            }
            TimeSpentUnit timeSpentUnit = new TimeSpentUnit(issueTitle, issueUrl, worklog.getMinutesSpent());
            timeSpentUnits.add(timeSpentUnit);
            return timeSpentUnits;
        });
    }

    public LocalDate toLocalDate(DateTime dateTime) {
        DateTime dateTimeUtc = dateTime.withZone(DateTimeZone.getDefault());
        return LocalDate.of(dateTimeUtc.getYear(), dateTimeUtc.getMonthOfYear(), dateTimeUtc.getDayOfMonth());
    }

    public List<TimeSpentUnit> getUserDateReports(LocalDate date) {
        List<TimeSpentUnit> result = timeSpentUnitsMap.get(date);
        return result == null ? Collections.emptyList() : result;
    }

    public boolean isEmpty() {
        return timeSpentUnitsMap.isEmpty();
    }

}
