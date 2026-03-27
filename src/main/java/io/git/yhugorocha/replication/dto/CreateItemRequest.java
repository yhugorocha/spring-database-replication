package io.git.yhugorocha.replication.dto;

public class CreateItemRequest {

    private String name;

    public CreateItemRequest() {
    }

    public CreateItemRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
