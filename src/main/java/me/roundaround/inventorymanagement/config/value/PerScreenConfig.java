package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.value.Position;

import java.io.Serializable;
import java.util.HashMap;

public class PerScreenConfig extends HashMap<String, PerScreenConfig.ScreenConfig> {
  record ScreenConfig(ButtonVisibility playerSideSortVisibility,
                      ButtonVisibility playerSideTransferVisibility,
                      ButtonVisibility containerSideSortVisibility,
                      ButtonVisibility containerSideTransferVisibility,
                      Position playerSideOffset,
                      Position containerSideOffset) implements Serializable {
  }
}
