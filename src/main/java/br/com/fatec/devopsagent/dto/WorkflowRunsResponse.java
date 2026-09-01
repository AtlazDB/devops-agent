package br.com.fatec.devopsagent.dto;

import java.util.List;

public record WorkflowRunsResponse(
        int total_count,
        List<WorkflowRun> workflow_runs
) {
}