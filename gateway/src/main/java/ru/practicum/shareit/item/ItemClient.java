package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemsByRequest(String textToFind) {
        return get("/search?=" + textToFind);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentNewDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}