package scpc.dutyhelper.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import scpc.dutyhelper.telegram.service.TelegramService;

@Component
@RequiredArgsConstructor
public class AdminNotifier {
    private final TelegramService telegramService;

    public void notifyAdmin(String message) {
        Long ADMIN_CHAT_ID = 440024209L;
        telegramService.sendMessage(ADMIN_CHAT_ID, "\uD83E\uDEB2 " + message);
    }

}
