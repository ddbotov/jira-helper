package com.moex.jirahelper.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimeSpentUnit {

    private final String issueTitle;
    private final String issueUrl;
    private final int minutesSpent;

}
