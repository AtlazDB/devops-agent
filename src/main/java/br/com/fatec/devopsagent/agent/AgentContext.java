package br.com.fatec.devopsagent.agent;

import br.com.fatec.devopsagent.agent.strategy.PlanningStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgentContext {

    private final Long runId;
    private final PlanningStrategy strategy;
    private final CommandExecutor executor;
    private final int maxIterations;

    private final List<Observation> observations = new ArrayList<>();

    private Plan plan = new Plan(List.of());
    private int iterations = 0;
    private String answer;
    private String error;

    public AgentContext(Long runId,
                        PlanningStrategy strategy,
                        CommandExecutor executor,
                        int maxIterations) {
        this.runId = runId;
        this.strategy = strategy;
        this.executor = executor;
        this.maxIterations = maxIterations;
    }

    public void record(Observation observation) {
        observations.add(observation);
    }

    public Optional<Observation> lastObservation() {
        return observations.isEmpty()
                ? Optional.empty()
                : Optional.of(observations.get(observations.size() - 1));
    }

    public boolean hasExecuted(Action action) {
        return observations.stream().anyMatch(o -> o.action() == action);
    }

    // Incrementa o contador. Chamar UMA vez por ciclo, no estado PLANNING.
    public boolean exceededLimit() {
        return ++iterations > maxIterations;
    }

    public Long getRunId() {
        return runId;
    }

    public PlanningStrategy getStrategy() {
        return strategy;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public List<Observation> getObservations() {
        return List.copyOf(observations);
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}