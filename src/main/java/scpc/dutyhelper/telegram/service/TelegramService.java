package scpc.dutyhelper.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.repository.UserRepository;
import scpc.dutyhelper.telegram.sender.UptimeRobotSCPCBotSender;

import java.util.Arrays;

/**
 * This service allows to communicate with Telegram API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramService {

    private final UserRepository userRepository;

    private final UptimeRobotSCPCBotSender botSender;

    public void sendMessageForAll(String... message) {
        userRepository.findAllByTelegramChatIdIsNotNull().stream()
                .map(User::getTelegramChatId)
                .forEach(id -> sendMessage(id, message));
    }

    public void sendMessage(Long chatId, String... text) {
        Arrays.stream(text).forEach(message -> sendMessage(chatId, message, null));
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(chatId.toString())
                //Other possible parse modes: MARKDOWNV2, MARKDOWN, which allows to make text bold, and all other things
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(replyKeyboard)
                .build();
        execute(sendMessage);
    }

    // Потрібно параметризувати? Чим?
    private void execute(BotApiMethod botApiMethod) {
        try {
            botSender.execute(botApiMethod);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }
}
