package com.onlyswapx.message.domain;

import com.onlyswapx.exchange.domain.ExchangeRequest;
import com.onlyswapx.exchange.infrastructure.ExchangeRepository;
import com.onlyswapx.message.dto.MessageRequest;
import com.onlyswapx.message.dto.MessageResponse;
import com.onlyswapx.message.infrastructure.MessageRepository;
import com.onlyswapx.user.domain.User;
import com.onlyswapx.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse send(String senderEmail, MessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ExchangeRequest exchange = exchangeRepository.findById(request.getExchangeId())
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

        Message message = Message.builder()
                .exchange(exchange)
                .sender(sender)
                .content(request.getContent())
                .messageType(Message.MessageType.valueOf(request.getMessageType()))
                .build();

        return toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getByExchange(Long exchangeId) {
        return messageRepository.findByExchangeIdOrderByCreatedAtAsc(exchangeId)
                .stream().map(this::toResponse).toList();
    }

    private MessageResponse toResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .exchangeId(m.getExchange().getId())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getFullName())
                .content(m.getContent())
                .messageType(m.getMessageType())
                .createdAt(m.getCreatedAt())
                .build();
    }
}