package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Step;
import br.com.fatec.devopsagent.entity.WorkflowRun;
import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.stereotype.Component;

// Concrete Command. Receiver = GitHubService.
// Busca a execucao (run) e devolve um resumo legivel, que depois vai para o prompt do JAN.
@Component
public class FetchRunCommand extends AbstractAgentCommand {

    private final GitHubService gitHub;

    public FetchRunCommand(GitHubService gitHub) {   // o Spring injeta o GitHubService
        this.gitHub = gitHub;
    }

    @Override
    public Action action() {
        return Action.FETCH_RUN;
    }

    @Override
    protected String run(Step step, AgentContext context) {
        WorkflowRun run = gitHub.getWorkflowRunById(context.getRunId());
        return "Run " + run.id() + " \"" + run.display_title() + "\""
                + " status=" + run.status() + " conclusion=" + run.conclusion();
    }
}