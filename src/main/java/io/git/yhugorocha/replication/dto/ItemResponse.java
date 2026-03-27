package io.git.yhugorocha.replication.dto;

public class ItemResponse {

    private Long id;
    private String name;

    public ItemResponse() {
    }

    public ItemResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
