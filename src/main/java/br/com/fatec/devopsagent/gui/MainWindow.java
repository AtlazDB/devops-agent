package br.com.fatec.devopsagent.gui;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.AgentRunner;
import br.com.fatec.devopsagent.agent.observer.AgentEventPublisher;
import br.com.fatec.devopsagent.gui.composite.AnalysisTreeBuilder;
import br.com.fatec.devopsagent.gui.composite.RunNode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class MainWindow extends JFrame {
    private final AgentRunner runner;
    private final AgentEventPublisher publisher;
    private final JTextField runIdField = new JTextField(14);
    private final JComboBox<String> strategyBox;
    private final JButton analyzeButton = new JButton("Analisar");
    private final JLabel statusLabel = new JLabel("Estado: -");
    private final JTree resultTree = new JTree(new DefaultMutableTreeNode("Nenhuma análise ainda"));
    private final JTextArea answerArea = new JTextArea();
    private final JTextArea eventArea = new JTextArea();
    private final SwingAgentObserver observer;

    public MainWindow(AgentRunner runner, AgentEventPublisher publisher, Collection<String> strategies) {
        super("DevOps Agent - Análise de builds do GitHub Actions");
        this.runner = runner;
        this.publisher = publisher;
        this.strategyBox = new JComboBox<>(strategies.toArray(String[]::new));

        this.observer = new SwingAgentObserver(statusLabel, eventArea);
        publisher.subscribe(observer);

        buildLayout();

        analyzeButton.addActionListener(e -> analyze());
        runIdField.addActionListener(e -> analyze());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                publisher.unsubscribe(observer);
            }
        });

        setSize(1000, 650);
        setLocationRelativeTo(null);
    }

    private void buildLayout() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Run ID:"));
        top.add(runIdField);
        top.add(new JLabel("Estratégia:"));
        top.add(strategyBox);
        top.add(analyzeButton);
        top.add(statusLabel);

        answerArea.setEditable(false);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        eventArea.setEditable(false);
        eventArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Resposta", new JScrollPane(answerArea));
        tabs.addTab("Eventos (Observer)", new JScrollPane(eventArea));

        JScrollPane treeScroll = new JScrollPane(resultTree);
        treeScroll.setBorder(BorderFactory.createTitledBorder("Árvore da análise (Composite)"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, tabs);
        split.setResizeWeight(0.45);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(split, BorderLayout.CENTER);
    }

    private void analyze() {
        Long runId;
        try {
            runId = Long.parseLong(runIdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um Run ID numérico.", "Run ID inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String strategy = (String) strategyBox.getSelectedItem();
        analyzeButton.setEnabled(false);
        answerArea.setText("Analisando...");
        eventArea.setText("");

        new SwingWorker<AgentContext, Void>() {
            @Override
            protected AgentContext doInBackground() {
                return runner.run(runId, strategy);
            }

            @Override
            protected void done() {
                try {
                    showResult(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    answerArea.setText("Erro ao executar o agente: " + e.getCause().getMessage());
                } finally {
                    analyzeButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void showResult(AgentContext context) {
        RunNode root = AnalysisTreeBuilder.build(context);
        resultTree.setModel(new DefaultTreeModel(root.toTreeNode()));
        for (int i = 0; i < resultTree.getRowCount(); i++) {
            resultTree.expandRow(i);
        }

        if (context.getError() != null) {
            answerArea.setText("O agente falhou: " + context.getError());
        } else {
            answerArea.setText(context.getAnswer() == null ? "(sem resposta)" : context.getAnswer());
            answerArea.setCaretPosition(0);
        }
    }
}