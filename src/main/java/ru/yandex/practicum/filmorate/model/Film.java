package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    private Integer id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Длительность не может быть отрицательной")
    private int duration;
    private Set<Integer> likes;
    private int likesCount = 0;

    @AssertTrue(message = "Дата релиза не может быть раньше чем 28 Декабря 1895 года")
    private boolean isValidReleaseDate() {
        return releaseDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28));
    }

    @Autowired
    public void setLikes(Set<Integer> likes) {
        this.likes = new HashSet<>(likes);
    }

    public void addLike(Integer id) {
        this.likes.add(id);
        likesCount++;
    }

    public void deleteLike(Integer id) {
        this.likes.remove(id);
        likesCount--;
    }
}
