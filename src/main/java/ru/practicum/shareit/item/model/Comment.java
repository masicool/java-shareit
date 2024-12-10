package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор комментария

    private String text; // текст комментария

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // вещь, к которой относится комментарий

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author; // автор комментария

    private LocalDateTime created; // дата создания комментария
}
