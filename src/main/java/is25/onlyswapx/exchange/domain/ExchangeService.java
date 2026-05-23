package is25.onlyswapx.exchange.domain;

import is25.onlyswapx.exchange.dto.ExchangeResponse;
import is25.onlyswapx.exchange.infrastructure.ExchangeRepository;
import is25.onlyswapx.user.domain.User;
import is25.onlyswapx.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExchangeResponse create(String requesterEmail, Long receiverId, String message) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ExchangeRequest exchange = ExchangeRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .message(message)
                .build();

        return toResponse(exchangeRepository.save(exchange));
    }

    @Transactional
    public ExchangeResponse accept(Long exchangeId, String userEmail) {
        ExchangeRequest exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        if (!exchange.getReceiver().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized");
        }
        exchange.setStatus(ExchangeRequest.ExchangeStatus.ACCEPTED);
        return toResponse(exchangeRepository.save(exchange));
    }

    @Transactional
    public ExchangeResponse reject(Long exchangeId, String userEmail) {
        ExchangeRequest exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        if (!exchange.getReceiver().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized");
        }
        exchange.setStatus(ExchangeRequest.ExchangeStatus.REJECTED);
        return toResponse(exchangeRepository.save(exchange));
    }

    public List<ExchangeResponse> getMyExchanges(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ExchangeRequest> sent = exchangeRepository.findByRequesterId(user.getId());
        List<ExchangeRequest> received = exchangeRepository.findByReceiverId(user.getId());
        sent.addAll(received);
        return sent.stream().map(this::toResponse).toList();
    }

    private ExchangeResponse toResponse(ExchangeRequest e) {
        return ExchangeResponse.builder()
                .id(e.getId())
                .requesterId(e.getRequester().getId())
                .requesterName(e.getRequester().getFullName())
                .receiverId(e.getReceiver().getId())
                .receiverName(e.getReceiver().getFullName())
                .status(e.getStatus())
                .message(e.getMessage())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
