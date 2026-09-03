package br.com.fatec.devopsagent.entity;

import java.util.List;

public record WorkflowJobsResponse(
        int total_count,
        List<WorkflowJob> jobs
) {}