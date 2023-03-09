package scpc.dutyhelper.telegram.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.service.UserService;
import scpc.dutyhelper.telegram.handler.UserRequestHandler;
import scpc.dutyhelper.telegram.helper.KeyboardHelper;
import scpc.dutyhelper.telegram.model.UserRequest;
import scpc.dutyhelper.telegram.service.TelegramService;

@Component
@RequiredArgsConstructor
public class StartCommandHandler extends UserRequestHandler {

    private static final String command = "/start";

    private final UserService userService;
    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;


    @Override
    public boolean isApplicable(UserRequest userRequest) {
        String[] queryParams = userRequest.getUpdate().getMessage().getText().split(" ");
        if (queryParams.length >= 2 && queryParams[0].equals(command)) {
            userRequest.getUpdate().getMessage().setText(queryParams[0]);
            userRequest.setCode(queryParams[1]);
        }
        return isCommand(userRequest.getUpdate(), command);
    }

    @Override
    public void handle(UserRequest request) {

        ReplyKeyboard replyKeyboard = keyboardHelper.buildMainMenu();

        String helloMessage = """
                \uD83D\uDC4BПривіт! Бот надсилатиме сповіщення щодо моніторингу веб-ресурсів налаштованих в сервісі Duty Helper.\s
                \rНа час дослідної експлуатації додано функцію перевірки доступності сервісу (на випадок якщо здається, що сервіс не працює); може бути видалено в подальшому.\s
                \rВідгуки/пропозиції закликаємо залишати через веб інтерфейс сервісу.""";
        if (request.getCode() != null) {
            User user = userService.connectTelegram(request.getChatId(), request.getCode());
            if (user != null) {
                helloMessage = String.format("%s, тепер ви можете користуватися ботом!", user.getFirstName());
            }
        }
        telegramService.sendMessage(request.getChatId(),
                helloMessage,
                replyKeyboard);
//        telegramService.sendMessage(request.getChatId(), "Обирайте з меню нижче ⤵️");
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
