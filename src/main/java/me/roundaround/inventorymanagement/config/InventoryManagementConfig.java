package me.roundaround.inventorymanagement.config;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;

public class InventoryManagementConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final PositionConfigOption DEFAULT_POSITION;
  public final PerScreenPositionConfigOption SCREEN_POSITIONS;

  public InventoryManagementConfig() {
    super(InventoryManagementMod.MOD_ID);

    MOD_ENABLED = registerConfigOption(
        BooleanConfigOption
            .builder("modEnabled", "inventorymanagement.mod_enabled.label")
            .setComment("Simple toggle for the mod! Set to false to disable.")
            .build());

    DEFAULT_POSITION = registerConfigOption(
        PositionConfigOption
            .builder("defaultPosition", "inventorymanagement.default_position.label", new Position(-4, -1))
            .setComment("Customize a default for button position.")
            .build());

    SCREEN_POSITIONS = registerConfigOption(
        PerScreenPositionConfigOption
            .builder("screenPositions", "inventorymanagement.screen_positions.label")
            .hideFromConfigScreen()
            .setComment("Customize button position on a per-screen basis.")
            .build());
  }
}
