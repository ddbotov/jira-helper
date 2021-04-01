package com.moex.jirahelper.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Component
@Getter
public class JiraClient {

    private static final Integer MAX_ISSUES_IN_RESULT = 10_000;

    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${jiraUrl:https://jira.moex.com/}")
    private String jiraUrl;

    private JiraRestClient restClient;

    @PostConstruct
    public void postConstruct() {
        this.restClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(this.jiraUrl), this.username, this.password);
    }

    public Iterable<Issue> searchJql(String jql) throws ExecutionException, InterruptedException {
        return restClient.getSearchClient().searchJql(jql, MAX_ISSUES_IN_RESULT, null, null).get().getIssues();
    }

    //sometimes worklogs does not load, probably bug
    public Issue refreshIssue(Issue issue) throws ExecutionException, InterruptedException {
        return restClient.getIssueClient().getIssue(issue.getKey()).get();
    }

    public String getIssueUrl(Issue issue) {
        return getJiraUrl() + "browse/" + issue.getKey();
    }
}
