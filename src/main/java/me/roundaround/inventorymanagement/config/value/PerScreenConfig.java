package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.value.Position;

import java.util.HashMap;

public class PerScreenConfig extends HashMap<String, PerScreenConfig.ScreenConfig> {
  public static HashMap<String, HashMap<String, String>> serialize(PerScreenConfig config) {
    HashMap<String, HashMap<String, String>> serialized = new HashMap<>();
    for (Entry<String, ScreenConfig> entry : config.entrySet()) {
      serialized.put(entry.getKey(), ScreenConfig.serialize(entry.getValue()));
    }
    return serialized;
  }

  public static PerScreenConfig deserialize(HashMap<String, HashMap<String, String>> serialized) {
    PerScreenConfig config = new PerScreenConfig();
    for (Entry<String, HashMap<String, String>> entry : serialized.entrySet()) {
      config.put(entry.getKey(), ScreenConfig.deserialize(entry.getValue()));
    }
    return config;
  }

  public record ScreenConfig(ButtonVisibility playerSideSortVisibility,
                      ButtonVisibility playerSideTransferVisibility,
                      ButtonVisibility containerSideSortVisibility,
                      ButtonVisibility containerSideTransferVisibility,
                      Position playerSideOffset,
                      Position containerSideOffset) {
    public static HashMap<String, String> serialize(ScreenConfig config) {
      HashMap<String, String> serialized = new HashMap<>();
      serialized.put("playerSideSortVisibility", config.playerSideSortVisibility().getId());
      serialized.put("playerSideTransferVisibility", config.playerSideTransferVisibility().getId());
      serialized.put("containerSideSortVisibility", config.containerSideSortVisibility().getId());
      serialized.put("containerSideTransferVisibility",
          config.containerSideTransferVisibility().getId());
      serialized.put("playerSideOffset", Position.serialize(config.playerSideOffset()));
      serialized.put("containerSideOffset", Position.serialize(config.containerSideOffset()));
      return serialized;
    }

    public static ScreenConfig deserialize(HashMap<String, String> serialized) {
      return new ScreenConfig(ButtonVisibility.fromId(serialized.get("playerSideSortVisibility")),
          ButtonVisibility.fromId(serialized.get("playerSideTransferVisibility")),
          ButtonVisibility.fromId(serialized.get("containerSideSortVisibility")),
          ButtonVisibility.fromId(serialized.get("containerSideTransferVisibility")),
          Position.deserialize(serialized.get("playerSideOffset")),
          Position.deserialize(serialized.get("containerSideOffset")));
    }
  }
}
