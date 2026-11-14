package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;
import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.stereotype.Component;

// Concrete Command. Receiver = GitHubService.
//
// O jobId deveria vir no argument do Step:
//  - ReAct: vem preenchido (a strategy leu o FETCH_JOBS antes de planejar).
//  - Plan-then-execute: vem null, porque o plano foi montado antes de saber o jobId.
// Para o plan-then-execute funcionar de verdade, o comando procura o jobId no contexto.
@Component
public class FetchLogsCommand extends AbstractAgentCommand {

    private final GitHubService gitHub;

    public FetchLogsCommand(GitHubService gitHub) {
        this.gitHub = gitHub;
    }

    @Override
    public Action action() {
        return Action.FETCH_LOGS;
    }

    @Override
    protected String run(Step step, AgentContext context) {
        String jobId = step.argument();
        if (jobId == null || jobId.isBlank()) {
            jobId = context.getObservations().stream()
                    .filter(o -> o.action() == Action.FETCH_JOBS && o.success())
                    .map(Observation::content)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "FETCH_LOGS sem jobId: execute FETCH_JOBS antes"));
        }
        String logs = gitHub.getJobLogs(jobId);
        if (logs == null || logs.isBlank()) {
            throw new IllegalStateException("log vazio para o job " + jobId);
        }
        return logs;
    }
}