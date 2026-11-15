package br.com.fatec.devopsagent.gui.composite;

import br.com.fatec.devopsagent.agent.Observation;

// Leaf concreta: uma execução de Command (Observation). Problema = o comando falhou.
public class ObservationNode extends LeafNode {
    private static final int PREVIEW_LEN = 80;
    private final Observation observation;

    public ObservationNode(Observation observation) {
        this.observation = observation;
    }

    public Observation observation() {
        return observation;
    }

    @Override
    public String label() {
        String out = observation.content() == null ? "" : observation.content().replaceAll("\\s+", " ");
        return out.length() > PREVIEW_LEN ? out.substring(0, PREVIEW_LEN) + "..." : out;
    }

    @Override
    public boolean hasProblem() {
        return !observation.success();
    }
}