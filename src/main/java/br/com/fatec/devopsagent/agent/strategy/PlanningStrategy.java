package br.com.fatec.devopsagent.agent.strategy;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Plan;

// Contrato do pattern Strategy: decide COMO planejar.
// As implementações vêm na Parte 5.

public interface PlanningStrategy {

    String name();

    Plan plan(AgentContext context);
}