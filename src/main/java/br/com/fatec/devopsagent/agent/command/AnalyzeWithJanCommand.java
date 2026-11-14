package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;
import br.com.fatec.devopsagent.service.JanClient;
import br.com.fatec.devopsagent.service.LogTruncator;
import org.springframework.stereotype.Component;

// Concrete Command. Receivers = JanClient (Spring AI ChatClient -> modelo local no JAN) e LogTruncator.
// Se o Step nao trouxer o texto pronto, monta o prompt com o que ja foi buscado do GitHub.
@Component
public class AnalyzeWithJanCommand extends AbstractAgentCommand {

    private static final String INSTRUCAO = """
            Voce e um especialista em CI/CD. Abaixo estao dados de uma execucao do GitHub Actions que falhou.
            Responda em portugues, de forma objetiva:
            1. O que falhou
            2. A causa provavel
            3. Como corrigir
            """;

    private final JanClient jan;
    private final LogTruncator truncator;

    public AnalyzeWithJanCommand(JanClient jan, LogTruncator truncator) {
        this.jan = jan;
        this.truncator = truncator;
    }

    @Override
    public Action action() {
        return Action.ANALYZE_WITH_JAN;
    }

    @Override
    protected String run(Step step, AgentContext context) {
        String dados = (step.argument() != null && !step.argument().isBlank())
                ? step.argument()
                : coletarDados(context);
        String resposta = jan.analisar(INSTRUCAO + "\n" + dados);
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalStateException("o modelo devolveu resposta vazia");
        }
        return resposta;
    }

    // Junta o que os comandos anteriores buscaram (estao guardados no context como Observations)
    private String coletarDados(AgentContext context) {
        StringBuilder sb = new StringBuilder();
        for (Observation o : context.getObservations()) {
            if (!o.success()) {
                continue;
            }
            switch (o.action()) {
                case FETCH_RUN -> sb.append("## Execucao\n").append(o.content()).append("\n\n");
                case FETCH_JOBS -> sb.append("## Job que falhou\nid ").append(o.content()).append("\n\n");
                case FETCH_LOGS -> sb.append("## Log do job (final)\n").append(truncator.truncate(o.content())).append("\n\n");
                default -> { }
            }
        }
        return sb.toString();
    }
}