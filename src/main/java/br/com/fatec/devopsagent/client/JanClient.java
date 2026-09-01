package br.com.fatec.devopsagent.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class JanClient {

    private final ChatClient chatClient;

    public JanClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analisar(String texto) {
        return chatClient.prompt()
                .user(texto)
                .call()
                .content();
    }
}