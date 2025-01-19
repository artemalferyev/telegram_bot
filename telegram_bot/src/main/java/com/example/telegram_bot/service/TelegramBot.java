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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Получить приветствование"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error setting bot commands: " + e.getMessage());
            e.printStackTrace();
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
        if (update.hasMessage()) {
            var message = update.getMessage();

            long chatId = message.getChatId();

            if (message.hasText()) {
                String messageText = message.getText();
                if (messageText.equals("/start")) {
                    String name = message.getChat().getFirstName();
                    sendWelcomeMessage(chatId, name);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "delivery":
                    sendDeliveryWithPhotosAndButtons(chatId);
                    break;

                case "europe":
                    sendEuropePhotos(chatId);
                    break;

                case "usa":
                    sendUsaPhotos(chatId);
                    break;

                default:
                    sendMessage(chatId, "Неизвестная команда.");
            }
        }
    }

    private void sendDeliveryWithPhotosAndButtons(long chatId) {
        String info = "ВАЖНО:\n\n" +
                "▫️ Порог беспошлинного ввоза товаров - 200€.\n\n" +
                "▫️ Бесплатное хранение товаров — 60 дней с момента поступления заказа, далее — 0,20 € за каждый день;\n\n" +
                "▫️ Возврат в магазин — 7€;\n\n" +
                "▫️ Переупаковка готовых к отправке товаров — 5 €;\n\n" +
                "▫️ Косметику и парфюм с Европы отправлять можно.\n\n" +
                "▫️ Доставка lux-брендов осуществляется в срок 7 дней, обсуждается индивидуально.\n\n" +
                "▫️ Доставка ювелирных украшений осуществляется курьером, обсуждается индивидуально.\n\n" +
                "▫️ Замеры в магазине — бесплатно.\n\n" +
                "▫️ После отправки из Европы вы получаете трек-номер.\n\n" +
                "▫️ Услуга срезания ценников (больше 2-х) - 4€ (необходимо в случае, когда товар заказан на скидке и надо пройти таможенный лимит).";

        String[] photoPaths = {
                "photo_2025-01-17 15.47.06.jpeg",
                "photo_2025-01-17 15.47.09.jpeg"
        };

        try {
            InputStream photoStream1 = getClass().getClassLoader().getResourceAsStream(photoPaths[0]);
            InputFile inputFile1 = new InputFile(photoStream1, photoPaths[0]);
            SendPhoto firstPhoto = new SendPhoto();
            firstPhoto.setChatId(String.valueOf(chatId));
            firstPhoto.setPhoto(inputFile1);
            firstPhoto.setCaption(info);

            execute(firstPhoto);

            InputStream photoStream2 = getClass().getClassLoader().getResourceAsStream(photoPaths[1]);
            InputFile inputFile2 = new InputFile(photoStream2, photoPaths[1]);
            SendPhoto secondPhoto = new SendPhoto();
            secondPhoto.setChatId(String.valueOf(chatId));
            secondPhoto.setPhoto(inputFile2);

            execute(secondPhoto);

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Выберите регион:");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            rowsInline.add(List.of(
                    createInlineButton("Европа", "europe", false),
                    createInlineButton("США", "usa", false)
            ));

            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);

            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Error sending delivery photos and buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void sendEuropePhotos(long chatId) {
        String[] europePhotoPaths = {
                "photo_2025-01-17 15.47.11.jpeg",
                "photo_2025-01-17 15.47.13.jpeg"
        };

        sendPhotos(chatId, europePhotoPaths, "\uD83C\uDDEA\uD83C\uDDFA");
    }

    private void sendUsaPhotos(long chatId) {
        String[] usaPhotoPaths = {
                "photo_2025-01-17 15.47.16.jpeg",
                "photo_2025-01-17 15.47.18.jpeg"
        };

        sendPhotos(chatId, usaPhotoPaths, "\uD83C\uDDFA\uD83C\uDDF8");
    }

    private void sendPhotos(long chatId, String[] photoPaths, String introText) {
        try {
            SendMessage introMessage = new SendMessage();
            introMessage.setChatId(String.valueOf(chatId));
            introMessage.setText(introText);
            execute(introMessage);

            for (String path : photoPaths) {

                InputStream photoStream = getClass().getClassLoader().getResourceAsStream(path);
                if (photoStream == null) {
                    System.out.println("Error: Photo not found - " + path);
                    continue;
                }

                InputFile photoFile = new InputFile(photoStream, path);

                SendPhoto photo = new SendPhoto();
                photo.setChatId(String.valueOf(chatId));
                photo.setPhoto(photoFile);

                execute(photo);
            }
        } catch (TelegramApiException e) {
            System.out.println("Error sending photos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void sendWelcomeMessage(long chatId, String name) {
        String textToSend = name + ", здравствуйте! \n\n" +
                "Я - бот-помощник байер-сервиса KUPIDON, созданный для вашего удобства. Я отвечу на все ваши вопросы! \n\n" +
                "Байер-сервис KUPIDON помогает осуществлять покупки желаемых товаров из США, Европы, а также ювелирных украшений из Дубая. \n\n" +
                "Почему шопинг с KUPIDON — это лучший выбор? Вот 5 причин: \n\n" +
                "- Адекватная наценка; \n" +
                "- Бесплатные замеры; \n" +
                "- Только оригинальные брендовые вещи с гарантией качества и подлинности; \n" +
                "- Индивидуальный подход к каждому клиенту; \n" +
                "- Выгодные условия доставки. \n\n" +
                "Выберите интересующий раздел ниже 🔻";

        sendMessageWithMainButtons(chatId, textToSend);
    }

    private void sendMessageWithMainButtons(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(
                createInlineButton("Оформить заказ", "https://t.me/marinakupidon", true)
        ));
        rowsInline.add(List.of(
                createInlineButton("Каталог", "https://t.me/kupidonbuyer", true)
        ));
        rowsInline.add(List.of(
                createInlineButton("Доставка", "delivery", false)
        ));
        rowsInline.add(List.of(
                createInlineButton("Отзывы", "https://t.me/feedbackkupidon", true)
        ));

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message with buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private InlineKeyboardButton createInlineButton(String text, String dataOrUrl, boolean isUrl) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        if (isUrl) {
            button.setUrl(dataOrUrl);
        } else {
            button.setCallbackData(dataOrUrl);
        }
        return button;
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}