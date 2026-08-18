package br.com.fatec.devopsagent.service;
import br.com.fatec.devopsagent.config.GitHubConfig;
import br.com.fatec.devopsagent.entity.WorkflowRunsResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}