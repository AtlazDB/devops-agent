package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.CommandExecutor;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Command: o Invoker.
// Substitui o StubCommandExecutor. Os estados continuam falando so com a interface CommandExecutor,
// entao o ExecutingState NAO muda nada.
// O Spring injeta todos os AgentCommand; para criar uma Action nova basta criar um @Component novo.
@Component
public class CommandDispatcher implements CommandExecutor {

    private final Map<Action, AgentCommand> commands = new EnumMap<>(Action.class);

    public CommandDispatcher(List<AgentCommand> available) {
        for (AgentCommand command : available) {
            AgentCommand previous = commands.put(command.action(), command);
            if (previous != null) {
                throw new IllegalStateException("dois comandos para a mesma Action: " + command.action());
            }
        }
    }

    @Override
    public Observation execute(Step step, AgentContext context) {
        AgentCommand command = commands.get(step.action());
        if (command == null) {
            return new Observation(step.action(), false, "nenhum comando registrado para " + step.action());
        }
        return command.execute(step, context);
    }
}