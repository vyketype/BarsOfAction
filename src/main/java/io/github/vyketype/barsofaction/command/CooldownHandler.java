package io.github.vyketype.barsofaction.command;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class CooldownHandler {
    @Setter
    private int publicCooldownSeconds = 0;
    
    private final Map<UUID, Integer> playerCooldowns = new HashMap<>();
    private final Map<UUID, Long> lastActionBarUse = new HashMap<>();
}
