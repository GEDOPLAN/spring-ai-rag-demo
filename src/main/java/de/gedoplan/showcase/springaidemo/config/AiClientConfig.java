package de.gedoplan.showcase.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClientConfig {

    @Bean
    ChatClient produceChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

}
