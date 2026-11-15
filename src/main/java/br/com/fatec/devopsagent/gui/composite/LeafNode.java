package br.com.fatec.devopsagent.gui.composite;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public abstract class LeafNode implements AnalysisNode {
    @Override
    public int countProblems() {
        return hasProblem() ? 1 : 0;
    }

    @Override
    public List<AnalysisNode> children() {
        return List.of();
    }

    @Override
    public DefaultMutableTreeNode toTreeNode() {
        return new DefaultMutableTreeNode(this, false);
    }

    @Override
    public String toString() {
        return (hasProblem() ? "[ERRO] " : "[OK] ") + label();
    }
}