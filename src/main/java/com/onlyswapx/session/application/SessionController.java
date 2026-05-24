package com.onlyswapx.session.application;

import com.onlyswapx.session.domain.SessionService;
import com.onlyswapx.session.dto.SessionRequest;
import com.onlyswapx.session.dto.SessionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> create(
            @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getMySessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sessionService.getMySessions(userDetails.getUsername()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SessionResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sessionService.cancel(id, userDetails.getUsername()));
    }
}