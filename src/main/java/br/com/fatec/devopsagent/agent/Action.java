package br.com.fatec.devopsagent.agent;

public enum Action {
    FETCH_RUN,        // GitHubService.getWorkflowRunById(Long runId)
    FETCH_JOBS,       // GitHubService.getWorkflowJobs(Long runId)  -> descobre os jobIds
    FETCH_LOGS,       // GitHubService.getJobLogs(String jobId)     -> precisa do jobId acima
    ANALYZE_WITH_JAN, // JanClient.analisar(String)
    ANSWER            // monta a resposta final, sem chamada externa
}