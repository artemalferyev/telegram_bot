package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {

        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получить приветствование"));
        listOfCommands.add(new BotCommand("/mydata", "сохранить данные"));
        listOfCommands.add(new BotCommand("/deletedata", "удалить данные"));
        listOfCommands.add(new BotCommand("/info", "информация, как использовать бота"));
        listOfCommands.add(new BotCommand("/settings", "wTF"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
        }
}

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {

                case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                        sendMessage(chatId, "Прости, эта команда невозможна.");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String whiteHeart = "\uD83E\uDEE1";
        String answer = "Добро пожаловать в байер-сервис KUPIDON " + whiteHeart + " " + name;

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
        }
    }

