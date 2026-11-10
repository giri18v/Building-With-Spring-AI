package com.spring.project.controller;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InformationController {

    private final ChatClient chatClient;
    private final ChatClient defaultChatClient;

    public InformationController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.defaultChatClient = builder
            .defaultSystem("You are a Strict Technical Java Interviewer.Ask the follow up questions in your response")
            .build();
    }

    @GetMapping
    public String getInformation(@RequestParam String text){

        return chatClient.prompt().user(text).call().content();
    }


    @GetMapping("/user-prompt")
    public String getUserPrompts(@RequestParam String persona, @RequestParam String question){
        String systemMessage = switch (persona.toLowerCase()) {
            case "pirate" -> "You are a pirate. Respond everything in pirate mode";
            case "interviewer" -> "You are a strict interviewer.Ask follow up questions in your response";
            default -> "You are a helpful assistant";
        };
        return chatClient.prompt()
            .system(systemMessage)
                .user(question)
                    .call()
                        .content();
    }


    @GetMapping("/default-prompt")
    public String getDefaultPrompts( @RequestParam String question){
        return defaultChatClient.prompt()
            .user(question)
            .call()
            .content();
    }



    @GetMapping("/advisor")
    public String getAdvisorPrompts( @RequestParam String language,@RequestParam String task){
        PromptTemplate promptTemplate = new
            PromptTemplate("Write a Short {language} code snippet that demonstrates how to do {task} and include comments explaining step by step ");
        Prompt prompt = promptTemplate.create(Map.of(
            "language", language,
            "task", task
        ));
        return defaultChatClient.prompt(prompt)
            .call()
            .content() ;
    }

}
