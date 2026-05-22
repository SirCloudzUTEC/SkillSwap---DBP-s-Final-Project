package is25.onlyswapx.credit.application;

import is25.onlyswapx.credit.domain.CreditService;
import is25.onlyswapx.credit.dto.CreditBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/balance")
    public ResponseEntity<CreditBalanceResponse> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creditService.getBalance(userDetails.getUsername()));
    }

    @PostMapping("/sessions/{sessionId}/confirm")
    public ResponseEntity<String> confirm(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        creditService.confirmSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok("Confirmed");
    }
}