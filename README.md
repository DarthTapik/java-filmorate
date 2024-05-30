# java-filmorate
Проект fimorate - это backend rest api сервис 
для оценивания и поиска фильмов.

Структура базы данных проекта: 
 

ER-диаграмма

![ER_diagram](https://github.com/DarthTapik/java-filmorate/blobadd-database/filmorate_db_er-diagram.png)

idef1x-диаграмма

![idef1x_diagram](https://github.com/DarthTapik/java-filmorate/blob/add-database/filmorate_db_idef1x-diagram.png)

В базе данных содержится две основные сущности User и Film.
Для соблюдения атомарности, отдельно 
были вынесены связуещие таблицы User_Friend - для отображения
статуса дружбы между пользователями,
Like - для хранения информации о лайке конкретного 
пользователя на конкретный фильм, связующая таблица Film_Genre и еще одна сущность Genre
