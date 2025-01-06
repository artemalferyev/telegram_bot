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
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private static final long MANAGER_USER_ID =  6614865222L;
    private final Map<Integer, Long> messageIdToUserIdMap = new HashMap<>();

    public TelegramBot(BotConfig config) {
        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получить приветствование"));

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
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            System.out.println("Callback query received: " + callbackData);

            switch (callbackData) {
                case "order":
                    sendOrderMessage(chatId);
                    break;
                case "delivery":
                    sendMessage(chatId,
                            "Важно:\n\n" +
                                    "▫️ Бесплатное хранение товаров — 30 дней с момента поступления заказа, далее — 0,15 € за каждый день;\n\n" +
                                    "▫️ Возврат в магазин — 15 €;\n\n" +
                                    "▫️ Отправка из Европы осуществляется 2 раза в неделю партиями.\n\n" +
                                    "▫️ Переупаковка готовых к отправке товаров — 5 €;\n\n" +
                                    "▫️ Доставка lux-брендов осуществляется в срок 7 дней, обсуждается индивидуально.\n\n" +
                                    "▫️ Доставка ювелирных украшений осуществляется курьером, обсуждается индивидуально.\n\n" +
                                    "▫️ Замеры — бесплатно."
                    );
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда.");
            }
        } else if (update.hasMessage()) {
            var message = update.getMessage();
            long chatId = message.getChatId();

            if (message.hasText()) {
                String messageText = message.getText();
                System.out.println("Received message from user, chatId: " + chatId + ", text: " + messageText);

                if (messageText.equals("/start")) {
                    String name = message.getChat().getFirstName();
                    sendWelcomeMessage(chatId, name);
                } else {
                    forwardToManager(update, chatId, messageText);
                }
            } else if (message.hasPhoto()) {
                forwardMediaToManager(message.getPhoto().get(0).getFileId(), "photo", chatId, update);
            } else if (message.hasVideo()) {
                forwardMediaToManager(message.getVideo().getFileId(), "video", chatId, update);
            } else if (message.hasAudio()) {
                forwardMediaToManager(message.getAudio().getFileId(), "audio", chatId, update);
            } else if (message.hasVoice()) {
                forwardMediaToManager(message.getVoice().getFileId(), "voice", chatId, update);
            }
        }
    }

    private void forwardMediaToManager(String fileId, String mediaType, long userChatId, Update update) {
        try {

            String userName = "";
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();

            if (firstName != null) {
                userName += firstName;
            }
            if (lastName != null) {
                userName += " " + lastName;
            }

            switch (mediaType) {
                case "photo":
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(String.valueOf(MANAGER_USER_ID));
                    sendPhoto.setPhoto(new InputFile(fileId));
                    sendPhoto.setCaption("Фото от пользователя " + userName + " (ID: " + userChatId + ")");
                    execute(sendPhoto);
                    break;

                case "video":
                    SendVideo sendVideo = new SendVideo();
                    sendVideo.setChatId(String.valueOf(MANAGER_USER_ID));
                    sendVideo.setVideo(new InputFile(fileId));
                    sendVideo.setCaption("Видео от пользователя " + userName + " (ID: " + userChatId + ")");
                    execute(sendVideo);
                    break;

                case "audio":
                    SendAudio sendAudio = new SendAudio();
                    sendAudio.setChatId(String.valueOf(MANAGER_USER_ID));
                    sendAudio.setAudio(new InputFile(fileId));
                    sendAudio.setCaption("Аудио от пользователя " + userName + " (ID: " + userChatId + ")");
                    execute(sendAudio);
                    break;

                case "voice":
                    SendVoice sendVoice = new SendVoice();
                    sendVoice.setChatId(String.valueOf(MANAGER_USER_ID));
                    sendVoice.setVoice(new InputFile(fileId));
                    sendVoice.setCaption("Голосовое сообщение от пользователя " + userName + " (ID: " + userChatId + ")");
                    execute(sendVoice);
                    break;
            }
        } catch (TelegramApiException e) {
            System.out.println("Error forwarding " + mediaType + " to manager: " + e.getMessage());
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
                createInlineButton("Оформить заказ", "https://t.me/marinakupupidon", true)
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

    private void sendOrderMessage(long chatId) {
        String messageText = "Мы будем рады Вам помочь ❤️\n" +
                "Скажите, пожалуйста, что Вас интересует.\n\n" +
                "Вы можете написать ниже текстовое сообщение, а также прислать фото, видео или голосовое сообщение ☺️";
        sendMessage(chatId, messageText);
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

    private void forwardToManager(Update update, long userChatId, String messageText) {
        String userName = "";
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();

        if (firstName != null) {
            userName += firstName;
        }
        if (lastName != null) {
            userName += " " + lastName;
        }

        SendMessage forwardMessage = new SendMessage();
        forwardMessage.setChatId(String.valueOf(MANAGER_USER_ID));
        forwardMessage.setText("Сообщение от пользователя " + userName + " (ID: " + userChatId + "):\n" + messageText);

        try {
            var sentMessage = execute(forwardMessage);
            if (sentMessage != null) {
                int messageId = sentMessage.getMessageId();
                messageIdToUserIdMap.put(messageId, userChatId);
                System.out.println("Mapped message ID " + messageId + " to user ID " + userChatId);
            } else {
                System.out.println("Error: Sent message is null. Unable to map user chat ID.");
            }
        } catch (TelegramApiException e) {
            System.out.println("Error forwarding message to manager: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleManagerResponse(org.telegram.telegrambots.meta.api.objects.Message replyToMessage, String managerMessage) {
        if (replyToMessage == null) {
            sendMessage(MANAGER_USER_ID, "Ошибка: невозможно определить пользователя. Пожалуйста, ответьте на конкретное сообщение.");
            System.out.println("Error: replyToMessage is null. Ensure the manager is replying to a forwarded user message.");
            return;
        }

        int originalMessageId = replyToMessage.getMessageId();
        Long userChatId = messageIdToUserIdMap.get(originalMessageId);

        System.out.println("Attempting to find user for original message ID: " + originalMessageId);
        System.out.println("Current map: " + messageIdToUserIdMap);

        if (userChatId == null) {
            sendMessage(MANAGER_USER_ID, "Ошибка: не удалось найти соответствующего пользователя для ответа.");
            System.out.println("Error: No user mapping found for message ID " + originalMessageId);
            return;
        }

        try {
            sendMessage(userChatId, "Ответ от менеджера:\n" + managerMessage);
            System.out.println("Successfully replied to user ID: " + userChatId + " with manager message: " + managerMessage);
        } catch (Exception e) {
            sendMessage(MANAGER_USER_ID, "Ошибка: не удалось отправить сообщение пользователю.");
            System.out.println("Error sending message to user ID " + userChatId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}