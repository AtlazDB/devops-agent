package br.com.fatec.devopsagent.controller;

import br.com.fatec.devopsagent.service.JanClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepuradorController {

    private final JanClient janClient;

    public DepuradorController(JanClient janClient) {
        this.janClient = janClient;
    }

    @GetMapping("/jan/teste")
    public String teste(@RequestParam(defaultValue = "diga apenas OK") String texto) {
        return janClient.analisar(texto);
    }
}