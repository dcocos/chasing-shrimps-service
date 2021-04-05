package com.dcocos.chasingshrimps.model;

import lombok.Getter;

@Getter
public enum Direction {

    NORTH(0, -1),
    SOUTH(0, 1),
    WEST(-1, 0),
    EAST(1, 0);

    private final int x;
    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Direction inverse(Direction direction) {
        switch (direction) {
            case SOUTH:
                return NORTH;
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
        }
        throw new IllegalStateException();
    }
}
