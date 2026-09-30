package com.momentum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "momentum")
public class LevelProperties {
    private List<Long> levelThresholds = new ArrayList<>(List.of(0L, 100L, 250L, 450L, 700L, 1000L, 1400L, 1900L));

    public List<Long> getLevelThresholds() {
        return levelThresholds;
    }

    public void setLevelThresholds(List<Long> levelThresholds) {
        this.levelThresholds = levelThresholds;
    }
}
