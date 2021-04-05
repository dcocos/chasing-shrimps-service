package com.dcocos.chasingshrimps.job;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dcocos.chasingshrimps.DataGenerator;
import com.dcocos.chasingshrimps.service.CatsAndShrimpsService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnimalMovementProducerJob {

    private final CatsAndShrimpsService catsAndShrimpsService;
    private final AtomicBoolean stopProducing = new AtomicBoolean(true);
    private final DataGenerator mockedData;

    public void toggle() {
        boolean temp;
        do {
            temp = stopProducing.get();
        } while (!stopProducing.compareAndSet(temp, !temp));
    }

    public boolean isOn() {
        return stopProducing.get();
    }

    @Scheduled(fixedDelay = 1000)
    public void emitCatEvents() {
        if (isOn()) {
            mockedData.getCats().forEach(cat -> {
                mockedData.moveAnimalInGrid(cat);
                catsAndShrimpsService.insertCat(cat);
            });
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void emitShrimpsEvents() {
        if (isOn()) {
            mockedData.getShrimps().forEach(shrimp -> {
                mockedData.moveAnimalInGrid(shrimp);
                catsAndShrimpsService.insertShrimp(shrimp);
            });
        }
    }
}
