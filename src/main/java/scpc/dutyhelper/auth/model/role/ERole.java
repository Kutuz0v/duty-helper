package scpc.dutyhelper.auth.model.role;

import lombok.Getter;
import scpc.dutyhelper.exception.BadRequestException;

@Getter
public enum ERole {
    USER("Користувач"),
    MODERATOR("Модератор"),
    ADMINISTRATOR("Адміністратор");

    private final String value;

    ERole(String value) {
        this.value = value;
    }

    public static ERole fromValue(String value) {
        return switch (value) {
            case "Користувач" -> USER;
            case "Модератор" -> MODERATOR;
            case "Адміністратор" -> ADMINISTRATOR;
            default -> throw new BadRequestException("Role [" + value
                    + "] not supported.");
        };
    }

    @Override
    public String toString() {
//        System.out.println(value);
        return value;
    }
}
