package br.com.fatec.devopsagent.agent;

import br.com.fatec.devopsagent.agent.strategy.PlanThenExecuteStrategy;
import br.com.fatec.devopsagent.agent.strategy.ReActStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentRunnerTest {

    /** Fake que registra tudo que foi pedido, pra gente inspecionar depois. */
    static class RecordingExecutor implements CommandExecutor {

        final List<Step> executed = new ArrayList<>();
        Action failOn;

        @Override
        public Observation execute(Step step, AgentContext context) {
            executed.add(step);

            if (step.action() == failOn) {
                return new Observation(step.action(), false, "falha simulada em " + step.action());
            }

            String content = switch (step.action()) {
                case FETCH_RUN -> "run concluded=failure";
                case FETCH_JOBS -> "456";                 // o jobId, pelo contrato combinado
                case FETCH_LOGS -> "ERROR: NullPointerException at Foo.java:42";
                case ANALYZE_WITH_JAN -> "O job falhou por NPE na linha 42.";
                case ANSWER -> "Analise final: NPE na linha 42.";
            };
            return new Observation(step.action(), true, content);
        }
    }

    private AgentRunner runnerWith(RecordingExecutor executor, String defaultStrategy) {
        return new AgentRunner(
                List.of(new PlanThenExecuteStrategy(), new ReActStrategy()),
                executor,
                defaultStrategy,
                10);
    }

    private String argumentOf(RecordingExecutor executor, Action action) {
        return executor.executed.stream()
                .filter(s -> s.action() == action)
                .findFirst()
                .orElseThrow()
                .argument();
    }

    @Test
    void planThenExecute_executaTudoMasNaoSabeOJobId() {
        RecordingExecutor executor = new RecordingExecutor();

        AgentContext context = runnerWith(executor, "plan-then-execute").run(123L, null);

        assertNull(context.getError());
        assertNotNull(context.getAnswer());
        assertEquals(5, executor.executed.size());
        assertNull(argumentOf(executor, Action.FETCH_LOGS));   // cego: planejou antes de saber
    }

    @Test
    void reAct_executaOsMesmosPassosMasComOJobIdPreenchido() {
        RecordingExecutor executor = new RecordingExecutor();

        AgentContext context = runnerWith(executor, "plan-then-execute").run(123L, "react");

        assertNull(context.getError());
        assertNotNull(context.getAnswer());
        assertEquals(5, executor.executed.size());
        assertEquals("456", argumentOf(executor, Action.FETCH_LOGS));   // reagiu à observação
    }

    @Test
    void falhaNoMeioInterrompeEregistraOMotivo() {
        RecordingExecutor executor = new RecordingExecutor();
        executor.failOn = Action.FETCH_LOGS;

        AgentContext context = runnerWith(executor, "plan-then-execute").run(123L, null);

        assertNull(context.getAnswer());
        assertNotNull(context.getError());
        assertTrue(context.getError().contains("FETCH_LOGS"));
        assertEquals(3, executor.executed.size());   // parou no 3o passo
    }

    @Test
    void runIdNuloFalhaLogoNoInit() {
        RecordingExecutor executor = new RecordingExecutor();

        AgentContext context = runnerWith(executor, "plan-then-execute").run(null, null);

        assertEquals("runId nao informado", context.getError());
        assertTrue(executor.executed.isEmpty());
    }
}