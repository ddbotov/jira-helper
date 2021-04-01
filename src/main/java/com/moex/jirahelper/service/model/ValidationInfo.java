package com.moex.jirahelper.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationInfo {

    private final boolean error;
    private final String message;
    private final int level;

}
