package scpc.dutyhelper.telegram.handler.impl.delete;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import scpc.dutyhelper.telegram.enums.ConversationState;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.model.UserSession;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.telegram.service.UserSessionService;

import static scpc.dutyhelper.telegram.constant.Constants.BTN_CANCEL;

//@Component
public class CancelHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public CancelHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate(), BTN_CANCEL);
    }

    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();
        telegramService.sendMessage(userRequest.getChatId(),
                "Обирайте з меню нижче ⤵️", replyKeyboard);

        UserSession userSession = userRequest.getUserSession();
        userSession.setState(ConversationState.CONVERSATION_STARTED);
        userSessionService.saveSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
