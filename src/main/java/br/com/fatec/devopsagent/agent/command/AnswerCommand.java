package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;
import org.springframework.stereotype.Component;

import java.util.List;

// Concrete Command sem chamada externa: devolve a ultima analise bem-sucedida do JAN.
@Component
public class AnswerCommand extends AbstractAgentCommand {

    @Override
    public Action action() {
        return Action.ANSWER;
    }

    @Override
    protected String run(Step step, AgentContext context) {
        List<Observation> obs = context.getObservations();
        for (int i = obs.size() - 1; i >= 0; i--) {   // de tras pra frente = a mais recente
            Observation o = obs.get(i);
            if (o.action() == Action.ANALYZE_WITH_JAN && o.success()) {
                return o.content();
            }
        }
        throw new IllegalStateException("nenhuma analise do JAN disponivel para responder");
    }
}