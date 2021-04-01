package com.moex.jirahelper.api;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.gitlab4j.api.Constants.TargetType.*;
import static org.gitlab4j.api.Constants.ActionType.*;

@Component
public class GitlabClient {

    GitLabApi gitLabApi;

    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${jiraUrl:http://gitlab.moex.com/}")
    private String gitlabUrl;

    @Value("#{T(java.time.LocalDate).parse('${sprintReportStartDate}')}")
    private LocalDate sprintReportStartDate;

    @PostConstruct
    public void postConstruct() throws GitLabApiException {
        //this.gitLabApi = GitLabApi.oauth2Login(gitlabUrl, username, password);
        //Constants.SearchScope a = Constants.SearchScope.USERS;
        //String b = "username=BotovDM";
        //List<?> x = gitLabApi.getSearchApi().globalSearch(a, b);

/*        List<Event> x = getAllGitlabUserEvents("LebedevAA");
        System.out.println();*/
        //gitLabApi.getCommitsApi().get
    }

    Constants.ActionType[] validActionTypes = {
            CREATED, UPDATED, CLOSED, REOPENED, PUSHED, COMMENTED, MERGED, JOINED, LEFT, DESTROYED, EXPIRED
    };

    Constants.TargetType[] validTargetTypes = {
        ISSUE, MILESTONE, NOTE, PROJECT, SNIPPET, USER
    };


    private List<Event> getAllGitlabUserEvents(String login) throws GitLabApiException {
        List<Event> result = new ArrayList<>();
        for (Constants.ActionType actionType : validActionTypes) {
            for (Constants.TargetType targetType : validTargetTypes) {
                result.addAll(gitLabApi.getEventsApi().getUserEvents(login, actionType, targetType,
                        Date.from(sprintReportStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        new Date(), Constants.SortOrder.ASC));
            }
        }
        return result;
    }

}

