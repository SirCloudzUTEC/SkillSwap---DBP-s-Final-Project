package is25.onlyswapx.exchange.application;

import is25.onlyswapx.exchange.domain.ExchangeService;
import is25.onlyswapx.exchange.dto.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping
    public ResponseEntity<ExchangeResponse> create(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String message = (String) body.getOrDefault("message", "");
        return ResponseEntity.ok(
                exchangeService.create(userDetails.getUsername(), receiverId, message));
    }

    @GetMapping
    public ResponseEntity<List<ExchangeResponse>> getMyExchanges(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                exchangeService.getMyExchanges(userDetails.getUsername()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ExchangeResponse> accept(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                exchangeService.accept(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ExchangeResponse> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                exchangeService.reject(id, userDetails.getUsername()));
    }
}