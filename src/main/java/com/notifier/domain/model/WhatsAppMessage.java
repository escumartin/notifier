package com.notifier.domain.model;

import lombok.Data;

@Data
public class WhatsAppMessage {

    private String recipient;
    private String content;

    // Example constructor:
    public WhatsAppMessage(String recipient, String content) {
        this.recipient = recipient;
        this.content = content;
    }
}