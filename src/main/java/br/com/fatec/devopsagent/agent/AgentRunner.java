package br.com.fatec.devopsagent.agent;

import br.com.fatec.devopsagent.agent.state.AgentState;
import br.com.fatec.devopsagent.agent.state.FailedState;
import br.com.fatec.devopsagent.agent.state.InitState;
import br.com.fatec.devopsagent.agent.strategy.PlanningStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AgentRunner {

    private final Map<String, PlanningStrategy> strategies;
    private final CommandExecutor executor;
    private final String defaultStrategy;
    private final int maxIterations;

    public AgentRunner(List<PlanningStrategy> available,
                       CommandExecutor executor,
                       @Value("${agent.strategy:plan-then-execute}") String defaultStrategy,
                       @Value("${agent.max-iterations:10}") int maxIterations) {

        this.strategies = available.stream()
                .collect(Collectors.toMap(PlanningStrategy::name, Function.identity()));
        this.executor = executor;
        this.defaultStrategy = defaultStrategy;
        this.maxIterations = maxIterations;
    }

    public AgentContext run(Long runId, String strategyName) {

        String chosen = (strategyName == null || strategyName.isBlank())
                ? defaultStrategy
                : strategyName;

        PlanningStrategy strategy = strategies.get(chosen);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "estrategia desconhecida: " + chosen + " (disponiveis: " + strategies.keySet() + ")");
        }

        AgentContext context = new AgentContext(runId, strategy, executor, maxIterations);
        AgentState state = new InitState();

        while (!state.isTerminal()) {
            state = state.handle(context);
        }

        if (state instanceof FailedState failed) {
            context.setError(failed.reason());   // <- lembra da armadilha da Parte 3
        }
        return context;
    }
}