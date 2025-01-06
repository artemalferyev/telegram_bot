package com.example.telegram_bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("kupidonbuyerservice_bot")
    String botName;

    @Value("8067532440:AAGkgv3au6rW-DOTkdsK_yiIw9aA-2zVfAs")
    String token;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }
}
