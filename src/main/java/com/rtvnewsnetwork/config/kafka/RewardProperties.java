package com.rtvnewsnetwork.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "reward")
public class RewardProperties {

    private Map<String, Integer> coins = Collections.emptyMap();

    public Map<String, Integer> getCoins() {
        return coins;
    }

    public void setCoins(Map<String, Integer> coins) {
        this.coins = coins;
    }
}

