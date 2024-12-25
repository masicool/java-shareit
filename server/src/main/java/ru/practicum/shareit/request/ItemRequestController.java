package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                        @RequestBody ItemRequestNewDto itemRequestNewDto) {
        return itemRequestService.createRequest(userId, itemRequestNewDto);
    }

    @GetMapping
    public List<ItemRequestDto> findItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.findItemRequestsByUserId(userId, false);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findItemRequestsOtherUsers(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.findItemRequestsByUserId(userId, true);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@PathVariable long requestId) {
        return itemRequestService.findItemRequestById(requestId);
    }
}
