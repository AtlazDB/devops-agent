package br.com.fatec.devopsagent.gui;

import br.com.fatec.devopsagent.agent.AgentRunner;
import br.com.fatec.devopsagent.agent.observer.AgentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

@Component
@ConditionalOnProperty(name = "agent.gui.enabled", havingValue = "true")
public class GuiLauncher {
    private static final Logger log = LoggerFactory.getLogger(GuiLauncher.class);
    private final AgentRunner runner;
    private final AgentEventPublisher publisher;

    public GuiLauncher(AgentRunner runner, AgentEventPublisher publisher) {
        this.runner = runner;
        this.publisher = publisher;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void open() {
        if (GraphicsEnvironment.isHeadless()) {
            log.warn("Ambiente headless: a GUI não será aberta.");
            return;
        }
        SwingUtilities.invokeLater(() ->
                new MainWindow(runner, publisher, runner.availableStrategies()).setVisible(true));
    }
}