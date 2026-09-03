package br.com.fatec.devopsagent;

import br.com.fatec.devopsagent.entity.WorkflowJobsResponse;
import br.com.fatec.devopsagent.entity.WorkflowRun;
import br.com.fatec.devopsagent.entity.WorkflowRunsResponse;
import br.com.fatec.devopsagent.service.GitHubService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import br.com.fatec.devopsagent.entity.WorkflowJob;

import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DevopsagentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevopsagentApplication.class, args);
    }

    @Bean
    CommandLineRunner run(GitHubService gitHubService) {
        return args -> {
            WorkflowRunsResponse response = gitHubService.getWorkflowRuns();

            assert response != null;
            System.out.println(response.total_count());

            for (WorkflowRun run : response.workflow_runs()) {
                System.out.println(run.id());
                System.out.println(run.display_title());
                System.out.println(run.status());
                System.out.println(run.conclusion());
            }

            // ==================================================
            // TESTE DAS NOVAS IMPLEMENTAÇÕES
            // ==================================================

            List<Long> runIds = response.workflow_runs()
                    .stream()
                    .map(WorkflowRun::id)
                    .toList();

            System.out.println("\n===== IDs DAS WORKFLOW RUNS =====");

            for (Long id : runIds) {
                System.out.println("Run ID: " + id);
            }

            // Pega a primeira Run encontrada para testar
            if (!runIds.isEmpty()) {

                Long runId = runIds.get(0);

                System.out.println("\n===== TESTANDO WORKFLOW RUN POR ID =====");
                System.out.println("Buscando Run ID: " + runId);

                WorkflowRun workflowRun =
                        gitHubService.getWorkflowRunById(runId);

                System.out.println("ID: " + workflowRun.id());
                System.out.println("Título: " + workflowRun.display_title());
                System.out.println("Status: " + workflowRun.status());
                System.out.println("Conclusão: " + workflowRun.conclusion());


                System.out.println("\n===== TESTANDO JOBS DA RUN =====");

                WorkflowJobsResponse jobsResponse =
                        gitHubService.getWorkflowJobs(runId);

                System.out.println("Total de Jobs: " + jobsResponse.total_count());

                for (WorkflowJob job : jobsResponse.jobs()) {

                    System.out.println("Job ID: " + job.id());
                    System.out.println("Nome: " + job.name());
                    System.out.println("Status: " + job.status());
                    System.out.println("Conclusão: " + job.conclusion());

                    System.out.println("--------------------");
                }
            }
        };
    }
}