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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

            if (messageText.equals("/start") || messageText.equals("Начать")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            } else {
                sendMessage(chatId, "Для начала нажмите кнопку 'Начать'.");
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("start")) {
                startCommandReceived(chatId, update.getCallbackQuery().getFrom().getFirstName());
            } else if (callbackData.equals("reviews")) {
                sendReviewsLink(chatId);
            } else if (callbackData.equals("instagram")) {
                sendInstagramLink(chatId);
            } else if (callbackData.equals("manager")) {
                sendManagerChat(chatId);
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String whiteHeart = "\uD83E\uDD0D";
        String answer = name + ", добро пожаловать в байер-сервис KUPIDON " + whiteHeart;

        sendMessageWithMainButtons(chatId, answer);
    }

    private void sendMessageWithMainButtons(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton reviewsButton = new InlineKeyboardButton();
        reviewsButton.setText("Отзывы");
        reviewsButton.setCallbackData("reviews");

        InlineKeyboardButton instagramButton = new InlineKeyboardButton();
        instagramButton.setText("Инстаграм");
        instagramButton.setCallbackData("instagram");

        InlineKeyboardButton managerButton = new InlineKeyboardButton();
        managerButton.setText("Обратиться к менеджеру");
        managerButton.setCallbackData("manager");

        rowInline.add(reviewsButton);
        rowInline.add(instagramButton);
        rowInline.add(managerButton);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendReviewsLink(long chatId) {
        String reviewsUrl = "https://t.me/feedbackkupidon";
        sendMessage(chatId, "Вы можете прочитать отзывы в нашем канале: " + reviewsUrl);
    }

    private void sendInstagramLink(long chatId) {
        String instagramUrl = "https://www.instagram.com/kupidon_buyerservice/?igsh=ampzend5ejR6MDAx&utm_source=qr";
        sendMessage(chatId, "Перейдите на наш Instagram: " + instagramUrl);
    }

    private void sendManagerChat(long chatId) {
        String managerUsername = "@MarinaKupidon";
        sendMessage(chatId, "Вы можете связаться с менеджером: " + managerUsername);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    }

