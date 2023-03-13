package scpc.dutyhelper.telegram.handler.impl.delete;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import scpc.dutyhelper.telegram.enums.ConversationState;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.model.UserSession;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.telegram.service.UserSessionService;

//@Component
public class CityEnteredHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public CityEnteredHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate())
                && ConversationState.WAITING_FOR_CITY.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithCancel();
        telegramService.sendMessage(userRequest.getChatId(),
                "✍️Тепер опишіть яка допомога вам потрібна⤵️",
                replyKeyboardMarkup);

        String city = userRequest.getUpdate().getMessage().getText();

        UserSession session = userRequest.getUserSession();
        session.setCity(city);
        session.setState(ConversationState.WAITING_FOR_TEXT);
        userSessionService.saveSession(userRequest.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

}
