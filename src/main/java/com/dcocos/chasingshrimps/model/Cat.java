package com.dcocos.chasingshrimps.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Cat extends Animal {

    private String color;
    private Boolean isHungry;
}
