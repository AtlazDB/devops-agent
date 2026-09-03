package br.com.fatec.devopsagent.dto;

import java.util.List;

public record WorkflowRunsResponse(
        List<WorkflowRun> workflow_runs,
        int total_count
) {
}