package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

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

    @AssertTrue(message = "Дата релиза не может быть раньше чем 28 Декабря 1895 года")
    private boolean isValidReleaseDate() {
        return releaseDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28));
    }
}
