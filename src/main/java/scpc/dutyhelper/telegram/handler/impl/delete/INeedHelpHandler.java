package scpc.dutyhelper.telegram.handler.impl.delete;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import scpc.dutyhelper.telegram.enums.ConversationState;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.model.UserSession;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.telegram.service.UserSessionService;

import java.util.List;

//@Component
public class INeedHelpHandler extends UserRequestHandler {

    public static String I_NEED_HELP = "❗️Потрібна допомога";
    public static List<String> cities = List.of("Київ", "Львів");

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public INeedHelpHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate(), I_NEED_HELP);
    }

    @Override
    public void handle(UserRequest userRequest) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildCitiesMenu(cities);
        telegramService.sendMessage(userRequest.getChatId(), "Оберіть місто або опишіть вручну⤵️", replyKeyboardMarkup);

        UserSession userSession = userRequest.getUserSession();
        userSession.setState(ConversationState.WAITING_FOR_CITY);
        userSessionService.saveSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

}
