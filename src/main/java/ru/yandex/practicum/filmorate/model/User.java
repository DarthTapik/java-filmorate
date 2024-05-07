package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashMap;

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
    private HashMap<Integer, FriendshipStatus> friends;

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return login;
        }
    }

    public void setFriends(HashMap<Integer, FriendshipStatus> friends) {
        this.friends = new HashMap<>(friends);
    }

    public void addFriend(int id, FriendshipStatus status) {
        friends.put(id, status);
    }

    public void removeFriend(int id) {
        friends.remove(id);
    }
}

