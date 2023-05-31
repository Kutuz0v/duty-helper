package scpc.dutyhelper.telegram.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.service.MonitorService;
import scpc.dutyhelper.telegram.constant.Constants;
import scpc.dutyhelper.telegram.enums.ConversationState;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.model.UserSession;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.telegram.service.UserSessionService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;

import static scpc.dutyhelper.util.TimeUtil.getTimeDifference;


@Component
@RequiredArgsConstructor
public class GetUnavailableResourcesHandler extends UserRequestHandler {

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;
    private final MonitorService monitorService;

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate(), Constants.BTN_UNAVAILABLE_RESOURCES);
    }

    private String unavailableResourceToTelegramMessage(Monitor monitor) {
        return String.format("<a href=\"%s\">%s</a> %s\n",
                monitor.getUrl(),
                monitor.getFriendlyName(),
                getTimeDifference(Date.from(monitor.getStateFrom().atZone(ZoneId.systemDefault()).toInstant()), new Date()));
    }

    private String getUnavailableResourcesMessage() {
        String checkTime = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        List<Monitor> unavailableResources = monitorService.getUnavailable();
        if (unavailableResources == null || unavailableResources.size() == 0) {
            return String.format("Станом на %s недоступних ресурсів немає.", checkTime);
        }
        StringBuilder unavailableResourcesHolder = new StringBuilder();
        unavailableResources.forEach(
                monitor -> unavailableResourcesHolder.append(unavailableResourceToTelegramMessage(monitor))
        );
        return String.format(
                "Станом на %s недоступні такі ресурси: \n%s",
                checkTime,
                unavailableResourcesHolder
        );
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMainMenu();

        telegramService.sendMessage(request.getChatId(),
                getUnavailableResourcesMessage(),
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