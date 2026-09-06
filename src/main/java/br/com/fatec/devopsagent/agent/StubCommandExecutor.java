package br.com.fatec.devopsagent.agent;

import org.springframework.stereotype.Component;


//TODO REMOVER quando o Command real chegar.
// Existe só pra aplicação subir enquanto o pattern Command não está pronto.
@Component
public class StubCommandExecutor implements CommandExecutor {

    @Override
    public Observation execute(Step step, AgentContext context) {
        return new Observation(step.action(), true, "stub: " + step.action() + " arg=" + step.argument());
    }
}