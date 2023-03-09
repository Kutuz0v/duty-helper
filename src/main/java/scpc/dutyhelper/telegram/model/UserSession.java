package scpc.dutyhelper.telegram.model;

import lombok.Builder;
import lombok.Data;
import scpc.dutyhelper.telegram.enums.ConversationState;

@Data
@Builder
public class UserSession {
    private Long chatId;
    private ConversationState state;
    private String city;
    private String text;
}
