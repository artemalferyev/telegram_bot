package com.example.telegram_bot.service;

import com.example.telegram_bot.config.BotConfig;
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
        } catch (TelegramApiException e) {
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
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "reviews":
                    sendReviewsLink(chatId);
                    break;
                case "instagram":
                    sendInstagramLink(chatId);
                    break;
                case "order":
                    sendOrderMessage(chatId);
                    break;
                case "delivery":
                    sendMessage(chatId, "Информация о доставке: Мы доставляем товары по всему миру. Свяжитесь с менеджером для уточнения деталей.");
                    break;
                case "terms":
                    sendMessage(chatId, "Условия покупки: Полные условия можно узнать на нашем сайте или у менеджера.");
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда.");
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                String name = update.getMessage().getChat().getFirstName();
                sendWelcomeMessage(chatId, name);
            } else {
                sendMessage(chatId, "Неизвестная команда. Используйте /start.");
            }
        }
    }

    private void sendWelcomeMessage(long chatId, String name) {
        String textToSend = name + ", здравствуйте! \n\n" +
                "Это наш бот-помощник байер-сервиса KUPIDON, созданный для вашего удобства. \n\n" +
                "Мы помогаем осуществлять покупки желаемых товаров из США, Европы, а также ювелирных украшений из Дубая. \n\n" +
                "Почему шопинг с KUPIDON — это лучший выбор? Вот 5 причин: \n\n" +
                "- Прозрачная и адекватная наценка. \n" +
                "- Бесплатные замеры. \n" +
                "- Только оригинальные брендовые вещи с гарантией качества и подлинности. \n" +
                "- Индивидуальный подход к каждому клиенту. \n" +
                "- Выгодные условия доставки. \n\n" +
                "Рады помочь вам с покупками! \n" +
                "Выберите интересующий раздел ниже 🔻";

        sendMessageWithMainButtons(chatId, textToSend);
    }

    private void sendMessageWithMainButtons(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton reviewsButton = new InlineKeyboardButton();
        reviewsButton.setText("Отзывы");
        reviewsButton.setCallbackData("reviews");

        InlineKeyboardButton instagramButton = new InlineKeyboardButton();
        instagramButton.setText("Инстаграм");
        instagramButton.setCallbackData("instagram");

        InlineKeyboardButton orderButton = new InlineKeyboardButton();
        orderButton.setText("Оформить заказ");
        orderButton.setCallbackData("order");

        InlineKeyboardButton deliveryButton = new InlineKeyboardButton();
        deliveryButton.setText("Доставка");
        deliveryButton.setCallbackData("delivery");

        InlineKeyboardButton termsButton = new InlineKeyboardButton();
        termsButton.setText("Условия");
        termsButton.setCallbackData("terms");

        InlineKeyboardButton catalogButton = new InlineKeyboardButton();
        catalogButton.setText("Каталог");
        catalogButton.setUrl("https://t.me/kupidonbuyer");

        rowsInline.add(List.of(reviewsButton));
        rowsInline.add(List.of(instagramButton));
        rowsInline.add(List.of(orderButton));
        rowsInline.add(List.of(deliveryButton));
        rowsInline.add(List.of(termsButton));
        rowsInline.add(List.of(catalogButton));

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

    private void sendOrderMessage(long chatId) {
        String managerUsername = "@MarinaKupidon";
        String messageText = "Мы будем рады Вам помочь ❤️\n" +
                "Скажите, пожалуйста, что Вас интересует.\n\n" +
                "Вы также можете прислать фото, видео или голосовое сообщение ☺️\n\n" +
                "Свяжитесь с нами здесь: " + managerUsername;

        sendMessage(chatId, messageText);
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