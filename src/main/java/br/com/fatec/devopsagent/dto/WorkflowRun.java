package br.com.fatec.devopsagent.dto;

public record WorkflowRun(
        Long id,
        String display_title,
        String status,
        String conclusion
) {
}