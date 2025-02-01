package me.roundaround.inventorymanagement.server.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerI18nTracker {
  private static final HashMap<UUID, ServerI18nTracker> instances = new HashMap<>();

  private final HashMap<String, String> store = new HashMap<>();

  private ServerI18nTracker(UUID player) {
  }

  private ServerI18nTracker(Map<String, String> store) {
    this.store.putAll(store);
  }

  public static ServerI18nTracker getInstance(PlayerEntity player) {
    return getInstance(player.getUuid());
  }

  public static ServerI18nTracker getInstance(UUID player) {
    return instances.computeIfAbsent(player, ServerI18nTracker::new);
  }

  public void track(Map<String, String> values) {
    this.store.putAll(values);
  }

  public String get(String translationKey) {
    return this.store.getOrDefault(translationKey, Language.getInstance().get(translationKey));
  }

  public Snapshot snapshot() {
    return new Snapshot(this);
  }

  public static void remove(PlayerEntity player) {
    instances.remove(player.getUuid());
  }

  public static class Snapshot {
    private final Map<String, String> store;

    private Snapshot(ServerI18nTracker source) {
      this.store = Map.copyOf(source.store);
    }

    public String get(ItemStack stack) {
      return this.get(stack.getTranslationKey());
    }

    public String get(String translationKey) {
      return this.store.getOrDefault(translationKey, Language.getInstance().get(translationKey));
    }
  }
}
