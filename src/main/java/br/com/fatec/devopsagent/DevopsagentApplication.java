package br.com.fatec.devopsagent;

import br.com.fatec.devopsagent.dto.WorkflowRun;
import br.com.fatec.devopsagent.dto.WorkflowRunsResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import br.com.fatec.devopsagent.client.GitHubClient;
import br.com.fatec.devopsagent.client.JanClient;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DevopsagentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevopsagentApplication.class, args);
    }

    @Bean
    CommandLineRunner run(GitHubClient gitHubService) {
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
        };
    }

    @Bean
    CommandLineRunner testarIA(JanClient ia) {
        return args -> {
            System.out.println("Testando a IA");
            String resposta = ia.analisar("Me explique o que é um Design Pattern em Java");
            System.out.println(resposta);
        };
    }
}