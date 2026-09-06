package br.com.fatec.devopsagent.agent;

import java.util.List;

public record Plan(List<Step> steps) {

    public boolean isEmpty() {
        return steps.isEmpty();
    }
}