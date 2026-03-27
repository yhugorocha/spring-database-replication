package io.git.yhugorocha.replication.controller;

import io.git.yhugorocha.replication.dto.CreateItemRequest;
import io.git.yhugorocha.replication.dto.ItemResponse;
import io.git.yhugorocha.replication.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateItemRequest request) {
        itemService.create(request.getName());
    }

    @GetMapping
    public List<ItemResponse> findAll() {
        return itemService.findAll();
    }
}