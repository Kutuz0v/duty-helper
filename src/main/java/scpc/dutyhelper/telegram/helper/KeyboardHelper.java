package scpc.dutyhelper.telegram.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import scpc.dutyhelper.telegram.constant.Constants;

import java.util.List;

/**
 * Helper class, allows to build keyboards for users
 */
@Component
public class KeyboardHelper {

    // Will be deleted on refactoring stage
    @Deprecated
    public ReplyKeyboardMarkup buildCitiesMenu(List<String> cities) {
        List<KeyboardButton> buttons = List.of(
                new KeyboardButton("Київ"),
                new KeyboardButton("Львів"));
        KeyboardRow row1 = new KeyboardRow(buttons);

        KeyboardRow row2 = new KeyboardRow(List.of(new KeyboardButton(Constants.BTN_CANCEL)));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildMainMenu() {
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(Constants.BTN_CHECK_ACCESSIBILITY);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(Constants.BTN_UNAVAILABLE_RESOURCES);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow1, keyboardRow2))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    public ReplyKeyboardMarkup buildMenuWithCancel() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(Constants.BTN_CANCEL);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .selective(true)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
