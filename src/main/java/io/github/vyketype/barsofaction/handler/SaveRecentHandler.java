package io.github.vyketype.barsofaction.handler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to get the recent sent actionbar for /ab saverecent
 */
public class SaveRecentHandler {

    @Getter
    private final Map<UUID, String> recents = new HashMap<>();

}
