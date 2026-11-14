package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Step;
import br.com.fatec.devopsagent.entity.WorkflowJob;
import br.com.fatec.devopsagent.entity.WorkflowJobsResponse;
import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.stereotype.Component;

// Concrete Command. Receiver = GitHubService.
//
// CONTRATO com a Strategy (ver ReActStrategy.jobIdFromObservations e AgentRunnerTest):
// o content desta Observation e SO o id do job que falhou, ex.: "456".
@Component
public class FetchJobsCommand extends AbstractAgentCommand {

    private final GitHubService gitHub;

    public FetchJobsCommand(GitHubService gitHub) {
        this.gitHub = gitHub;
    }

    @Override
    public Action action() {
        return Action.FETCH_JOBS;
    }

    @Override
    protected String run(Step step, AgentContext context) {
        WorkflowJobsResponse response = gitHub.getWorkflowJobs(context.getRunId());
        if (response == null || response.jobs() == null || response.jobs().isEmpty()) {
            throw new IllegalStateException("a run " + context.getRunId() + " nao tem jobs");
        }
        return response.jobs().stream()
                .filter(job -> "failure".equalsIgnoreCase(job.conclusion()))   // so o que falhou
                .map(WorkflowJob::id)
                .map(String::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "nenhum job com conclusion=failure na run " + context.getRunId()));
    }
}