package br.com.fatec.devopsagent.gui.composite;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public interface AnalysisNode {
    String label();
    boolean hasProblem();
    int countProblems();
    List<AnalysisNode> children();
    DefaultMutableTreeNode toTreeNode();
}