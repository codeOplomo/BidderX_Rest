package org.anas.bidderx_rest.service.dto;

import java.util.UUID;

public class CategoryDTO {
    private UUID id;
    private String name;

    // getters and setters
    public CategoryDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
