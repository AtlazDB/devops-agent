package br.com.fatec.devopsagent.gui.composite;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public abstract class CompositeNode implements AnalysisNode {
    private final List<AnalysisNode> children = new ArrayList<>();

    public CompositeNode add(AnalysisNode child) {
        children.add(child);
        return this;
    }

    public void remove(AnalysisNode child) {
        children.remove(child);
    }

    @Override
    public List<AnalysisNode> children() {
        return List.copyOf(children);
    }

    @Override
    public boolean hasProblem() {
        return children.stream().anyMatch(AnalysisNode::hasProblem);
    }

    @Override
    public int countProblems() {
        return children.stream().mapToInt(AnalysisNode::countProblems).sum();
    }

    @Override
    public DefaultMutableTreeNode toTreeNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (AnalysisNode child : children) {
            node.add(child.toTreeNode());
        }
        return node;
    }

    @Override
    public String toString() {
        int problems = countProblems();
        return label() + (problems > 0 ? " (" + problems + " problema(s))" : " (ok)");
    }
}