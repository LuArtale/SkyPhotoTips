package com.ea2soa.skyphototips.dto;

public class Event {

    private String type_events;
    private Long dni;
    private String description;
    private Long id;

    @Override
    public String toString() {
        return "Event{" +
                "type_events='" + type_events + '\'' +
                ", dni=" + dni +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    public String getType_events() {
        return type_events;
    }

    public void setType_events(String type_events) {
        this.type_events = type_events;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
