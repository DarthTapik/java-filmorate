package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    Integer id;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "email не может быть пустым")
    String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[A-Za-z0-9_.-]*$",
            message = "Логин должен быть без пробелов, "
                    + "и содержать только Латинские буквы и символы: \"_\" \"-\" \".\"")
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return login;
        }
    }


}
