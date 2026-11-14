package br.com.fatec.devopsagent.controller;

import br.com.fatec.devopsagent.agent.AgentRunner;
import br.com.fatec.devopsagent.entity.AnaliseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Roda o agente pela API, sem a GUI.
// Ex.: GET /api/agent/analisar/123456789?strategy=react
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentRunner runner;

    public AgentController(AgentRunner runner) {
        this.runner = runner;
    }

    @GetMapping("/analisar/{runId}")
    public AnaliseResponse analisar(@PathVariable Long runId,
                                    @RequestParam(required = false) String strategy) {
        return AnaliseResponse.from(runner.run(runId, strategy));
    }
}