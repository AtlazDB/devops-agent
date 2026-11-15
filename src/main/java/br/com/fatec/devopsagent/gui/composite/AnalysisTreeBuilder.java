package br.com.fatec.devopsagent.gui.composite;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

// Monta a árvore Composite a partir do resultado do agente:
public final class AnalysisTreeBuilder {
    private static final String[] SUSPEITOS = {"error", "exception", "failed", "failure", "fatal"};
    private static final int MAX_LINHAS_POR_LOG = 30;

    private AnalysisTreeBuilder() {
    }

    public static RunNode build(AgentContext context) {
        RunNode root = new RunNode(context.getRunId(), context.getStrategy().name());
        Map<Action, ActionGroupNode> groups = new LinkedHashMap<>();

        for (Observation o : context.getObservations()) {
            ActionGroupNode group = groups.computeIfAbsent(o.action(), a -> {
                ActionGroupNode g = new ActionGroupNode(a);
                root.add(g);
                return g;
            });
            group.add(new ObservationNode(o));

            if (o.action() == Action.FETCH_LOGS && o.success() && o.content() != null) {
                addSuspiciousLines(group, o.content());
            }
        }
        return root;
    }

    private static void addSuspiciousLines(CompositeNode group, String log) {
        String[] lines = log.split("\\R");
        int added = 0;
        for (int i = 0; i < lines.length && added < MAX_LINHAS_POR_LOG; i++) {
            String lower = lines[i].toLowerCase(Locale.ROOT);
            for (String word : SUSPEITOS) {
                if (lower.contains(word)) {
                    group.add(new LogLineNode(i + 1, lines[i]));
                    added++;
                    break;
                }
            }
        }
    }
}