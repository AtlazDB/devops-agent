package br.com.fatec.devopsagent.controller;

import br.com.fatec.devopsagent.entity.WorkflowJobsResponse;
import br.com.fatec.devopsagent.entity.WorkflowRun;
import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/jobs/{jobId}/logs")
    public String getJobLogs(@PathVariable String jobId) {
        return gitHubService.getJobLogs(jobId);
    }

    @GetMapping("/runs/{runId}")
    public WorkflowRun getWorkflowRunById(@PathVariable Long runId) {
        return gitHubService.getWorkflowRunById(runId);
    }

    @GetMapping("/runs/{runId}/jobs")
    public WorkflowJobsResponse getWorkflowJobs(@PathVariable Long runId) {
        return gitHubService.getWorkflowJobs(runId);
    }

    @GetMapping("/runs/ids")
    public List<Long> getWorkflowRunIds() {
        return gitHubService.getWorkflowRunIds();
    }
}