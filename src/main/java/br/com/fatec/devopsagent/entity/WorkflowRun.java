package br.com.fatec.devopsagent.entity;

public record WorkflowRun(
        Long id,
        String display_title,
        String status,
        String conclusion
) {
}