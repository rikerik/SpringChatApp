package com.rikerik.chat.config;

import com.rikerik.chat.chat.ChatMessage;
import com.rikerik.chat.chat.MessageType;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent sdEvent
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sdEvent.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            messageTemplate.convertAndSend("/topic/public",chatMessage);
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(
            SessionConnectEvent scEvent
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(scEvent.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username == null) {
            log.info("User connected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.JOIN)
                    .sender(username)
                    .build();

            messageTemplate.convertAndSend("/topic/public",chatMessage);
        }
    }
}
