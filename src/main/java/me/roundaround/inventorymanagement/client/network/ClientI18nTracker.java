package me.roundaround.inventorymanagement.client.network;

import net.minecraft.client.resource.language.I18n;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class ClientI18nTracker {
  private static ClientI18nTracker instance;

  private final HashMap<String, String> store = new HashMap<>();

  private ClientI18nTracker() {
  }

  public static ClientI18nTracker getInstance() {
    if (instance == null) {
      instance = new ClientI18nTracker();
    }
    return instance;
  }

  public HashMap<String, String> track(Collection<String> translationKeys) {
    HashMap<String, String> changed = new HashMap<>();
    translationKeys.forEach((translationKey) -> {
      String translated = I18n.translate(translationKey);
      if (!Objects.equals(this.store.put(translationKey, translated), translated)) {
        changed.put(translationKey, translated);
      }
    });
    return changed;
  }

  public void clear() {
    this.store.clear();
  }
}
