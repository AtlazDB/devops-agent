package br.com.fatec.devopsagent.gui.composite;

// Leaf concreta: uma linha suspeita encontrada no log de um job.
public class LogLineNode extends LeafNode {
    private final int lineNumber;
    private final String text;

    public LogLineNode(int lineNumber, String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    @Override
    public String label() {
        return "linha " + lineNumber + ": " + text.strip();
    }

    @Override
    public boolean hasProblem() {
        return true;
    }
}