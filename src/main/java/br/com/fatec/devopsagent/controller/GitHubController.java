package br.com.fatec.devopsagent.controller;

import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GitHubController {

private final GitHubService gitHubService;

public GitHubController(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
}

@GetMapping("/jobs{jobId}/logs")
    public String getJobLogs(@PathVariable String jobId) {
    return gitHubService.getJobLogs(jobId);
}



}
