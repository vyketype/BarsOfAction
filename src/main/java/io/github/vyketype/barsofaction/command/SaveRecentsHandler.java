package io.github.vyketype.barsofaction.command;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class SaveRecentsHandler {
    private final Map<UUID, String> recents = new HashMap<>();
}
