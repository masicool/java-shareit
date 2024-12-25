package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestNewDto itemRequestNewDto, User user) {
        return ItemRequest.builder()
                .description(itemRequestNewDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsToResponse(itemRequest.getItems()))
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(toItemRequestDto(itemRequest));
        }
        return dtos;
    }

    private static List<ItemForResponseDto> itemsToResponse(List<Item> items) {
        if (items == null) return List.of();

        List<ItemForResponseDto> itemForResponseDtos = new ArrayList<>();
        for (Item item : items) {
            if (item != null) {
                itemForResponseDtos.add(new ItemForResponseDto(item.getId(), item.getName(), item.getOwner().getId()));
            }
        }
        return itemForResponseDtos;
    }
}
