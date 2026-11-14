package br.com.fatec.devopsagent.service;

import br.com.fatec.devopsagent.config.GitHubConfig;
import br.com.fatec.devopsagent.entity.WorkflowJobsResponse;
import br.com.fatec.devopsagent.entity.WorkflowRun;
import br.com.fatec.devopsagent.entity.WorkflowRunsResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

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

        // O GitHub responde 302 com o link temporario do arquivo de log (campo Location).
        ResponseEntity<String> response = restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().is3xxRedirection() && response.getHeaders().getLocation() != null) {
            // Segue o link SEM o token: o link ja traz a propria autorizacao,
            // e o token do GitHub nao deve ser enviado para outro servidor.
            return RestClient.create()
                    .get()
                    .uri(response.getHeaders().getLocation())
                    .retrieve()
                    .body(String.class);
        }
        return response.getBody();
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