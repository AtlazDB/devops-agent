package br.com.fatec.devopsagent.dto;

public record WorkflowRun(
        Long id,
        String name,
        String status,
        String conclusion
) {
}