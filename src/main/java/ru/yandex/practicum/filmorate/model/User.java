package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "email не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[A-Za-z0-9_.-]*$",
            message = "Логин должен быть без пробелов, "
                    + "и содержать только Латинские буквы и символы: \"_\" \"-\" \".\"")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return login;
        }
    }

    public void setFriends(Set<Integer> friends) {
        this.friends = new HashSet<>(friends);
    }

    public void addFriend(int id) {
        friends.add(id);
    }

    public void removeFriend(int id) {
        friends.remove(id);
    }
}

