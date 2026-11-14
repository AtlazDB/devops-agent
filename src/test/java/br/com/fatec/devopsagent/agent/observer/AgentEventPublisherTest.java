package br.com.fatec.devopsagent.agent.observer;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.AgentRunner;
import br.com.fatec.devopsagent.agent.CommandExecutor;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Phase;
import br.com.fatec.devopsagent.agent.strategy.PlanThenExecuteStrategy;
import br.com.fatec.devopsagent.agent.strategy.ReActStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentEventPublisherTest {

    /** Observer de teste: so guarda tudo o que recebeu. */
    static class RecordingObserver implements AgentObserver {
        final List<AgentEvent> events = new ArrayList<>();

        @Override
        public void onEvent(AgentEvent event) {
            events.add(event);
        }

        List<AgentEvent.Type> types() {
            return events.stream().map(AgentEvent::type).toList();
        }
    }

    /** Executor falso (sem GitHub nem JAN). Falha na Action informada, se houver. */
    static CommandExecutor executorFailingOn(Action failOn) {
        return (step, context) -> {
            if (step.action() == failOn) {
                return new Observation(step.action(), false, "falha simulada");
            }
            String content = step.action() == Action.FETCH_JOBS ? "456" : "ok";
            return new Observation(step.action(), true, content);
        };
    }

    private AgentRunner runner(AgentEventPublisher publisher, Action failOn) {
        return new AgentRunner(
                List.of(new PlanThenExecuteStrategy(), new ReActStrategy()),
                executorFailingOn(failOn),
                publisher,
                "plan-then-execute",
                10);
    }

    @Test
    void planThenExecute_publica11EventosNaOrdem() {
        RecordingObserver observer = new RecordingObserver();
        runner(new AgentEventPublisher(List.of(observer)), null).run(123L, null);

        assertEquals(11, observer.events.size());
        assertEquals(Phase.INIT, observer.events.get(0).phase());
        assertEquals(AgentEvent.Type.FINISHED, observer.events.get(10).type());
        assertEquals(5L, observer.types().stream().filter(t -> t == AgentEvent.Type.STEP_EXECUTED).count());   // 1 por comando
    }

    @Test
    void reAct_publicaMaisEventosQuePlanThenExecute() {
        RecordingObserver observer = new RecordingObserver();
        runner(new AgentEventPublisher(List.of(observer)), null).run(123L, "react");

        assertEquals(23, observer.events.size());   // 5 voltas de PLANNING -> EXECUTING -> OBSERVING
    }

    @Test
    void falhaNoMeio_terminaComEventoFailed() {
        RecordingObserver observer = new RecordingObserver();
        AgentContext context = runner(new AgentEventPublisher(List.of(observer)), Action.FETCH_LOGS).run(123L, null);

        assertNotNull(context.getError());
        AgentEvent last = observer.events.get(observer.events.size() - 1);
        assertEquals(AgentEvent.Type.FAILED, last.type());
        assertTrue(last.message().contains("falha simulada"));
    }

    @Test
    void observerComDefeito_naoDerrubaOAgente() {
        AgentObserver quebrado = event -> {
            throw new RuntimeException("observer quebrado");
        };
        RecordingObserver observer = new RecordingObserver();

        AgentContext context = runner(new AgentEventPublisher(List.of(quebrado, observer)), null).run(123L, null);

        assertNull(context.getError());
        assertNotNull(context.getAnswer());
        assertEquals(11, observer.events.size());   // o observer bom recebeu tudo mesmo assim
    }

    @Test
    void unsubscribe_paraDeReceberEventos() {
        RecordingObserver observer = new RecordingObserver();
        AgentEventPublisher publisher = new AgentEventPublisher(List.of());
        publisher.subscribe(observer);
        publisher.unsubscribe(observer);

        runner(publisher, null).run(123L, null);

        assertTrue(observer.events.isEmpty());
    }
}