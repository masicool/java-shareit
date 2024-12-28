package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(long userId, ItemRequestNewDto itemRequestNewDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestNewDto, user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> findItemRequestsByUserId(long userId, boolean isOtherUsers) {
        List<ItemRequestDto> itemRequestDtos;

        if (isOtherUsers) {
            // найдем все запросы других пользователей, отсортированных от более новых к более старым
            itemRequestDtos = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId));
        } else {
            // найдем все запросы пользователя, отсортированные от более новых к более старым
            itemRequestDtos = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));
        }
        // получим список ID этих запросов
        List<Long> itemRequestIds = itemRequestDtos.stream().map(ItemRequestDto::getId).toList();

        // получим список вещей, созданных по запросам пользователя, используя список ID этих запросов
        Map<Long, List<Item>> itemsMap = itemRepository.findAllByRequestIdIn(itemRequestIds)
                .stream()
                .collect(Collectors.groupingBy(o -> o.getRequest().getId()));

        // заполним список созданных вещей по каждому запросу
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            itemRequestDto.setItems(ItemMapper.itemsToResponse(itemsMap.getOrDefault(itemRequestDto.getId(), List.of())));
        }

        return itemRequestDtos;
    }

    public ItemRequestDto findItemRequestById(long requestId) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with ID: " + requestId + " not found")));

        // получим список вещей, созданных по запросу
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(requestId));
        itemRequestDto.setItems(ItemMapper.itemsToResponse(items));

        return itemRequestDto;
    }
}
