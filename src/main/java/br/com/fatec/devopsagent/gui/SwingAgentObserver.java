package br.com.fatec.devopsagent.gui;

import br.com.fatec.devopsagent.agent.observer.AgentEvent;
import br.com.fatec.devopsagent.agent.observer.AgentObserver;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// Observer: Subscriber concreto que atualiza a tela.
public class SwingAgentObserver implements AgentObserver {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final JLabel statusLabel;
    private final JTextArea eventLog;

    public SwingAgentObserver(JLabel statusLabel, JTextArea eventLog) {
        this.statusLabel = statusLabel;
        this.eventLog = eventLog;
    }

    @Override
    public void onEvent(AgentEvent event) {
        String hora = LocalTime.ofInstant(event.at(), ZoneId.systemDefault()).format(HORA);
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Estado: " + event.phase());
            eventLog.append(hora + " [" + event.type() + " run=" + event.runId() + "] " + event.message() + "\n");
            eventLog.setCaretPosition(eventLog.getDocument().getLength());
        });
    }
}
