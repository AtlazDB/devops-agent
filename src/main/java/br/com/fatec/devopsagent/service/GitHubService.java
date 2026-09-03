package br.com.fatec.devopsagent.service;

import br.com.fatec.devopsagent.config.GitHubConfig;
import br.com.fatec.devopsagent.entity.WorkflowJobsResponse;
import br.com.fatec.devopsagent.entity.WorkflowRunsResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import br.com.fatec.devopsagent.entity.WorkflowRun;

import java.util.List;

@Service
public class GitHubService {

    private final RestClient restClient;
    private final GitHubConfig config;

    public GitHubService(GitHubConfig config) {
        this.config = config;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + config.token())
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }

    public @Nullable WorkflowRunsResponse getWorkflowRuns() {

        String url = String.format(
                "/repos/%s/%s/actions/runs",
                config.organization(),
                config.repository()
        );

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(WorkflowRunsResponse.class);
    }

        public String getJobLogs(String jobId) {

        String url = String.format(
                "/repos/%s/%s/actions/jobs/%s/logs",
                config.organization(),
                config.repository(),
                jobId
        );

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

        public WorkflowRun getWorkflowRunById(Long runId) {
        String url = String.format(
                "/repos/%s/%s/actions/runs/%d",
                config.organization(),
                config.repository(),
                runId
        );

        WorkflowRun run = restClient.get()
                .uri(url)
                .retrieve()
                .body(WorkflowRun.class);

        if (run == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow run not found: " + runId);
        }
        return run;
        }

        public WorkflowJobsResponse getWorkflowJobs(Long runId) {

        String url = String.format(
                "/repos/%s/%s/actions/runs/%d/jobs",
                config.organization(),
                config.repository(),
                runId
        );

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(WorkflowJobsResponse.class);
        }

        public List<Long> getWorkflowRunIds() {

        WorkflowRunsResponse response = getWorkflowRuns();
        if (response == null) {
                return List.of();
        }
        return response.workflow_runs()
                .stream()
                .map(WorkflowRun::id)
                .toList();
        }

}