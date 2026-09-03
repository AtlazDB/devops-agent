package br.com.fatec.devopsagent.entity;

public record WorkflowJob(
        Long id,
        String name,
        String status,
        String conclusion
) {}