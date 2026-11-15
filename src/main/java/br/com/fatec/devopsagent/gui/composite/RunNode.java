package br.com.fatec.devopsagent.gui.composite;

// Composite concreto: raiz da árvore, representa a execução (run) analisada.
public class RunNode extends CompositeNode {
    private final Long runId;
    private final String strategy;

    public RunNode(Long runId, String strategy) {
        this.runId = runId;
        this.strategy = strategy;
    }

    @Override
    public String label() {
        return "Run #" + runId + " [estrategia: " + strategy + "]";
    }
}