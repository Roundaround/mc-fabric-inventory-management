package me.roundaround.inventorymanagement.config;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;

import java.util.HashMap;

public class InventoryManagementConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final BooleanConfigOption SHOW_SORT;
  public final BooleanConfigOption SHOW_TRANSFER;
  public final BooleanConfigOption SHOW_STACK;
  public final PositionConfigOption DEFAULT_POSITION;
  public final PerScreenConfigOption PER_SCREEN_CONFIGS;

  public InventoryManagementConfig() {
    super(InventoryManagementMod.MOD_ID,
        options(InventoryManagementMod.MOD_ID).setConfigVersion(2));

    MOD_ENABLED = registerConfigOption(BooleanConfigOption.builder(this,
            "modEnabled",
            this.i18n("mod_enabled.label"))
        .setComment("Simple toggle for the mod! Set to false to disable.")
        .build());

    SHOW_SORT = registerConfigOption(BooleanConfigOption.yesNoBuilder(this,
            "showSort",
            this.i18n("show_sort.label"))
        .setComment("Whether or not to show sort buttons in the UI.")
        .build());

    SHOW_TRANSFER = registerConfigOption(BooleanConfigOption.yesNoBuilder(this,
            "showTransfer",
            this.i18n("show_transfer.label"))
        .setComment("Whether or not to show transfer buttons in the UI.")
        .build());

    SHOW_STACK = registerConfigOption(BooleanConfigOption.yesNoBuilder(this,
            "showStack",
            this.i18n("show_stack.label"))
        .setComment("Whether or not to show autostack buttons in the UI.")
        .build());

    DEFAULT_POSITION = registerConfigOption(PositionConfigOption.builder(this,
            "defaultPosition",
            this.i18n("default_position.label"),
            new Position(-4, -1))
        .setDisabledSupplier(() -> !SHOW_SORT.getValue() && !SHOW_TRANSFER.getValue() &&
            !SHOW_STACK.getValue())
        .setComment("Customize a default for button position.")
        .build());

    PER_SCREEN_CONFIGS = registerConfigOption(PerScreenConfigOption.builder(this,
        "perScreenConfigs",
        this.i18n("per_screen_configs.label")).build());
  }

  private String i18n(String key) {
    return this.getModId() + "." + key;
  }

  @Override
  protected boolean updateConfigVersion(int version, Config config) {
    if (version == 1) {
      Config modConfig = config.get("inventorymanagement");

      Config screenPositionsConfig = modConfig.get("screenPositions");
      if (screenPositionsConfig == null) {
        return false;
      }

      HashMap<String, Position> screenPositions = new HashMap<>();
      screenPositionsConfig.valueMap().keySet().forEach((key) -> {
        screenPositions.put(key, Position.deserialize(screenPositionsConfig.get(key)));
      });

      modConfig.remove("screenPositions");

      PerScreenConfig screenConfigs = new PerScreenConfig();
      screenPositions.forEach((key, value) -> {
        String cleanedKey = key.substring(0, key.lastIndexOf('-'));
        boolean playerSide = key.substring(key.lastIndexOf('-') + 1).equals("player");

        if (playerSide) {
          screenConfigs.setPlayerSideOffset(cleanedKey, value);
        } else {
          screenConfigs.setContainerSideOffset(cleanedKey, value);
        }
      });

      modConfig.set("perScreenConfigs", PerScreenConfigOption.serialize(screenConfigs));

      return true;
    }

    return false;
  }
}
