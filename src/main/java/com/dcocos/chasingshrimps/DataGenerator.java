package com.dcocos.chasingshrimps;

import static com.dcocos.chasingshrimps.model.Direction.*;
import static java.util.Set.of;
import static net.andreinc.mockneat.unit.objects.Filler.filler;
import static net.andreinc.mockneat.unit.objects.From.from;
import static net.andreinc.mockneat.unit.seq.IntSeq.intSeq;
import static net.andreinc.mockneat.unit.text.Formatter.fmt;
import static net.andreinc.mockneat.unit.types.Bools.bools;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.dcocos.chasingshrimps.model.Animal;
import com.dcocos.chasingshrimps.model.Cat;
import com.dcocos.chasingshrimps.model.Direction;
import com.dcocos.chasingshrimps.model.Shrimp;
import io.confluent.ksql.util.Pair;
import lombok.Getter;
import lombok.val;
import net.andreinc.mockneat.abstraction.MockUnitString;

@Component
@Getter
public class DataGenerator {

    private final List<String> colors = List.of("Black", "White", "Orange", "Gray", "Stripes");
    private List<Cat> cats = Collections.emptyList();
    private List<Shrimp> shrimps = Collections.emptyList();
    private List<Pair<Integer, Integer>> roadsCoordinates = Collections.emptyList();
    @Value("${generated.data.grid-size}")
    private int gridSize;
    @Value("${generated.data.step}")
    private int step;
    @Value("${generated.data.shrimps}")
    private int catsNumber;
    @Value("${generated.data.shrimps}")
    private int shrimpsNumber;
    @Value("${generated.data.movement}")
    private int movement;
    private MockUnitString catsIds;
    private MockUnitString shrimpsIds;

    public List<Cat> generateCats() {
        return from(cats).list(ints().range(0, catsNumber)).get();
    }

    public List<Shrimp> generateShrimps() {
        return from(shrimps).list(ints().range(0, shrimpsNumber)).get();
    }

    public void moveAnimalInGrid(Animal animal) {
        int x = animal.getX();
        int y = animal.getY();
        if (x % step == 0 && y % step == 0) {
            val directions = getPossibleDirectionsInIntersection(x, y);
            directions.remove(inverse(animal.getDirection()));
            animal.setDirection(from(directions).get());
        }
        animal.move(movement);
    }

    public List<Direction> getPossibleDirectionsInIntersection(int x, int y) {
        val possibleDirections = new HashSet<>(of(NORTH, SOUTH, EAST, WEST));
        if (x == 0) {
            possibleDirections.remove(WEST);
        }
        if (y == 0) {
            possibleDirections.remove(NORTH);
        }
        if (x == gridSize - 1) {
            possibleDirections.remove(EAST);
        }
        if (y == gridSize - 1) {
            possibleDirections.remove(SOUTH);
        }
        return new ArrayList<>(possibleDirections);
    }

    public List<Direction> getPossibleDirections(int x, int y) {
        if (x % step == 0 && y % step == 0) {
            return getPossibleDirectionsInIntersection(x, y);
        }
        if (x % step == 0) {
            return List.of(NORTH, SOUTH);
        }
        if (y % step == 0) {
            return List.of(EAST, WEST);
        }
        return List.of(EAST, WEST, NORTH, SOUTH);
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initRoadCoordinates();
        initCats();
        initShrimps();
    }

    private void initRoadCoordinates() {
        this.roadsCoordinates = new ArrayList<>();
        for (int i = 0; i < gridSize; i += step) {
            for (int j = 0; j < gridSize; j += movement) {
                roadsCoordinates.add(Pair.of(i, j));
                roadsCoordinates.add(Pair.of(j, i));
            }
        }
    }

    private void initCats() {
        this.catsIds = fmt("#{name} #{seq}")
                .param("name", names().full())
                .param("seq", intSeq());
        this.cats =
                filler(Cat::new)
                        .setter(Cat::setProfileId, catsIds)
                        .setter(Cat::setColor, from(colors))
                        .setter(Cat::setIsHungry, bools().probability(75.0))
                        .map(updateAnimalPosition())
                        .list(catsNumber)
                        .get();
    }

    private <T extends Animal> Function<T, T> updateAnimalPosition() {
        return animal -> {
            Pair<Integer, Integer> position = from(roadsCoordinates).get();
            int x = position.getLeft();
            int y = position.getRight();
            animal.setX(x);
            animal.setY(y);
            val direction = from(getPossibleDirections(x, y)).get();
            animal.setDirection(direction);
            return animal;
        };
    }

    private void initShrimps() {
        this.shrimpsIds = fmt("Shrimp #{seq}")
                .param("seq", intSeq());
        this.shrimps =
                filler(Shrimp::new)
                        .setter(Shrimp::setProfileId, shrimpsIds)
                        .setter(Shrimp::setDirection, from(Direction.class))
                        .map(updateAnimalPosition())
                        .list(shrimpsNumber)
                        .get();
    }
}
