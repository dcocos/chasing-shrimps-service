package com.dcocos.chasingshrimps.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dcocos.chasingshrimps.model.Cat;
import com.dcocos.chasingshrimps.model.Shrimp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.KsqlObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatsAndShrimpsService {

    private final Client client;
    private final ObjectMapper objectMapper;
    @Value("${stream.catsLocations}")
    private String catsLocationsStream;
    @Value("${stream.shrimpsLocations}")
    private String shrimpsLocationsStream;

    public void insertCat(Cat cat) {
        val map = objectMapper.convertValue(cat, Map.class);
        val insert = new KsqlObject(map);
        try {
            client.insertInto(catsLocationsStream, insert).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while inserting cats locations", e);
        }
    }

    public void insertShrimp(Shrimp shrimp) {
        val map = objectMapper.convertValue(shrimp, Map.class);
        val insert = new KsqlObject(map);
        try {
            client.insertInto(shrimpsLocationsStream, insert).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while inserting shrimps locations", e);
        }
    }
}
