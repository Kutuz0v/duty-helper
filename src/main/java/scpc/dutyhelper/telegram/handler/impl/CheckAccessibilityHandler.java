package scpc.dutyhelper.telegram.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import scpc.dutyhelper.telegram.enums.ConversationState;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.model.UserSession;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.telegram.service.UserSessionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
@RequiredArgsConstructor
public class CheckAccessibilityHandler extends UserRequestHandler {

    public static String GET_ALL_RESOURCES = "\uD83D\uDC40️Доступність сервісу";
    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate(), GET_ALL_RESOURCES);
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(request.getChatId(),
                String.format(
                        "Станом на %s сервіс Duty Helper активний",
                        LocalDateTime.now()
                                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))),
                replyKeyboardMarkup);

        String text = request.getUpdate().getMessage().getText();


        UserSession session = request.getUserSession();
        session.setText(text);
        session.setState(ConversationState.CONVERSATION_STARTED);
        userSessionService.saveSession(request.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
