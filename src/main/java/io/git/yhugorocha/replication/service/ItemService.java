package io.git.yhugorocha.replication.service;

import io.git.yhugorocha.replication.dto.ItemResponse;
import io.git.yhugorocha.replication.model.Item;
import io.git.yhugorocha.replication.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void create(String name) {
        itemRepository.save(name);
    }

    public List<ItemResponse> findAll() {
        List<Item> items;

        try {
            items = itemRepository.findAllFromReader();
            System.out.println("Leitura feita na réplica");
        } catch (Exception ex) {
            System.out.println("Réplica indisponível, lendo do primary");
            items = itemRepository.findAllFromWriter();
        }

        return items.stream()
                .map(item -> new ItemResponse(item.getId(), item.getName()))
                .toList();
    }
}
