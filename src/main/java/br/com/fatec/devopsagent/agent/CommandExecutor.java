package br.com.fatec.devopsagent.agent;

// Contrato do pattern Command.
// Quem for implementar o Command preenche isso — os estados só programam contra a interface.

public interface CommandExecutor {

    Observation execute(Step step, AgentContext context);
}