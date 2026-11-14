package br.com.fatec.devopsagent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Logs do GitHub Actions podem ter milhares de linhas, e um modelo local pequeno tem contexto curto.
// O erro quase sempre esta no FINAL do log, entao guardamos so as ultimas N letras.
@Component
public class LogTruncator {

    private final int maxChars;

    public LogTruncator(@Value("${agent.log.max-chars:6000}") int maxChars) {
        this.maxChars = maxChars;
    }

    public String truncate(String log) {
        if (log == null || log.length() <= maxChars) {
            return log;
        }
        String tail = log.substring(log.length() - maxChars);
        int firstBreak = tail.indexOf('\n');               // nao comeca no meio de uma linha
        if (firstBreak >= 0 && firstBreak < tail.length() - 1) {
            tail = tail.substring(firstBreak + 1);
        }
        return "[... " + (log.length() - tail.length()) + " caracteres anteriores omitidos ...]\n" + tail;
    }
}