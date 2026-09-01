package br.com.fatec.devopsagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.fatec.devopsagent.client.GitHubClient;

@RestController
@RequestMapping("/api")
public class GitHubController {

private final GitHubClient gitHubService;

public GitHubController(GitHubClient gitHubClient) {
		this.gitHubService = gitHubClient;
}





}
