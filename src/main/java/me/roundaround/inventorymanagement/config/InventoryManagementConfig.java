package me.roundaround.inventorymanagement.config;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.GuiTheme;
import me.roundaround.roundalib.config.value.Position;

import java.util.Arrays;

public class InventoryManagementConfig extends ModConfig {
  public final BooleanConfigOption MOD_ENABLED;
  public final BooleanConfigOption SHOW_SORT;
  public final BooleanConfigOption SHOW_TRANSFER;
  public final BooleanConfigOption SHOW_STACK;
  public final OptionListConfigOption<GuiTheme> GUI_THEME;
  public final PositionConfigOption DEFAULT_POSITION;
  public final PerScreenPositionConfigOption SCREEN_POSITIONS;

  public InventoryManagementConfig() {
    super(InventoryManagementMod.MOD_ID);

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

    GUI_THEME = registerConfigOption(OptionListConfigOption.builder(this,
            "guiTheme",
            this.i18n("gui_theme.label"),
            Arrays.asList(GuiTheme.values()),
            GuiTheme.getDefault())
        .setComment("Whether the buttons should use light theme (vanilla),",
            "dark theme (VanillaTweaks dark UI), or automatically choose ",
            "based on whether you have VanillaTweaks dark UI enabled.")
        .build());

    DEFAULT_POSITION = registerConfigOption(PositionConfigOption.builder(this,
            "defaultPosition",
            this.i18n("default_position.label"),
            new Position(-4, -1))
        .setDisabledSupplier(() -> !SHOW_SORT.getValue() && !SHOW_TRANSFER.getValue() &&
            !SHOW_STACK.getValue())
        .setComment("Customize a default for button position.")
        .build());

    SCREEN_POSITIONS = registerConfigOption(PerScreenPositionConfigOption.builder(this,
            "screenPositions",
            this.i18n("screen_positions.label"))
        .hideFromConfigScreen()
        .setComment("Customize button position on a per-screen basis.")
        .build());
  }

  private String i18n(String key) {
    return this.getModId() + "." + key;
  }
}
