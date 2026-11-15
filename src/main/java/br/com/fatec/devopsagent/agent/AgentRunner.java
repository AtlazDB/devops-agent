package br.com.fatec.devopsagent.agent;

import br.com.fatec.devopsagent.agent.observer.AgentEvent;
import br.com.fatec.devopsagent.agent.observer.AgentEventPublisher;
import br.com.fatec.devopsagent.agent.state.AgentState;
import br.com.fatec.devopsagent.agent.state.FailedState;
import br.com.fatec.devopsagent.agent.state.InitState;
import br.com.fatec.devopsagent.agent.strategy.PlanningStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Set;
import java.util.TreeSet;

@Service
public class AgentRunner {

    private final Map<String, PlanningStrategy> strategies;
    private final CommandExecutor executor;
    private final AgentEventPublisher events;          // NOVO (Observer)
    private final String defaultStrategy;
    private final int maxIterations;

    @Autowired   // com 2 construtores, este e o que o Spring usa
    public AgentRunner(List<PlanningStrategy> available,
                       CommandExecutor executor,
                       AgentEventPublisher events,
                       @Value("${agent.strategy:plan-then-execute}") String defaultStrategy,
                       @Value("${agent.max-iterations:10}") int maxIterations) {

        this.strategies = available.stream()
                .collect(Collectors.toMap(PlanningStrategy::name, Function.identity()));
        this.executor = executor;
        this.events = events;
        this.defaultStrategy = defaultStrategy;
        this.maxIterations = maxIterations;
    }

    // construtor antigo, mantido para o AgentRunnerTest: sem observers
    public AgentRunner(List<PlanningStrategy> available,
                       CommandExecutor executor,
                       String defaultStrategy,
                       int maxIterations) {
        this(available, executor, new AgentEventPublisher(List.of()), defaultStrategy, maxIterations);
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

        AgentContext context = new AgentContext(runId, strategy, executor, maxIterations, events);
        AgentState state = new InitState();
        events.publish(AgentEvent.stateChanged(runId, state.phase()));      // Observer

        while (!state.isTerminal()) {
            state = state.handle(context);
            events.publish(AgentEvent.stateChanged(runId, state.phase()));  // Observer: cada transicao do State
        }

        if (state instanceof FailedState failed) {
            context.setError(failed.reason());
            events.publish(AgentEvent.failed(runId, failed.reason()));
        } else {
            events.publish(AgentEvent.finished(runId, context.getAnswer()));
        }
        return context;
    }
    // usado pela GUI (Parte 3) para preencher o combo de estratégias
    public Set<String> availableStrategies() {
        return new TreeSet<>(strategies.keySet());
    }
}