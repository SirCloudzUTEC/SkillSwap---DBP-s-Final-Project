package com.onlyswapx.message.application;

import com.onlyswapx.message.domain.MessageService;
import com.onlyswapx.message.dto.MessageRequest;
import com.onlyswapx.message.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.send(userDetails.getUsername(), request));
    }

    @GetMapping("/exchange/{exchangeId}")
    public ResponseEntity<List<MessageResponse>> getByExchange(
            @PathVariable Long exchangeId) {
        return ResponseEntity.ok(messageService.getByExchange(exchangeId));
    }
}