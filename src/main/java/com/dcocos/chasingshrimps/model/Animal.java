package com.dcocos.chasingshrimps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Animal {

    private String profileId;

    @JsonIgnore
    private int x;
    @JsonIgnore
    private int y;

    private Direction direction;

    public void move(int movement) {
        this.x += direction.getX() * movement;
        this.y += direction.getY() * movement;
    }

    public void changeDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonProperty("location")
    public String locationAsString() {
        return x + " " + y;
    }

}
