package dev.emrullah.ai_panel.controller;

import dev.emrullah.ai_panel.model.AiPanelChatResponse;
import dev.emrullah.ai_panel.service.ChatService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<AiPanelChatResponse> chat(
            @RequestParam @NotNull @NotBlank String userMessage
    ) {
        return ResponseEntity.ok()
                .body(chatService.chat(userMessage));
    }
}
