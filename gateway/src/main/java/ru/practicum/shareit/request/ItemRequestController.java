package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                @Valid @RequestBody ItemRequestNewDto itemRequestNewDto) {
        return itemRequestClient.createRequest(userId, itemRequestNewDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findItemRequestsOtherUsers(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.getItemRequestsByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(requestId);
    }
}
