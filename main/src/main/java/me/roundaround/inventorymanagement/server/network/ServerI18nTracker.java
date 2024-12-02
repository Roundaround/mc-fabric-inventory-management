package me.roundaround.inventorymanagement.server.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerI18nTracker {
  private static final HashMap<UUID, ServerI18nTracker> instances = new HashMap<>();

  private final UUID player;
  private final HashMap<String, String> store = new HashMap<>();

  private ServerI18nTracker(UUID player) {
    this.player = player;
  }

  public static ServerI18nTracker getInstance(UUID player) {
    return instances.computeIfAbsent(player, ServerI18nTracker::new);
  }

  public void track(Map<String, String> values) {
    this.store.putAll(values);
  }

  public String get(String translationKey) {
    return this.store.getOrDefault(translationKey, translationKey);
  }

  public void clear() {
    this.store.clear();
    instances.remove(this.player);
  }
}
