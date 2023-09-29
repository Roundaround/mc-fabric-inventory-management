package me.roundaround.inventorymanagement.config;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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
        this.i18n("per_screen_configs.label"))
        .hideFromConfigScreen()
        .setComment("Customize settings on a per-screen basis.")
        .build());
  }

  private String i18n(String key) {
    return this.getModId() + "." + key;
  }

  @Override
  protected boolean updateConfigVersion(int version, Config config) {
    Config modConfig = config.get("inventorymanagement");
    if (modConfig == null) {
      return false;
    }

    if (version == 1) {
      return runMigrations(modConfig,
          List.of(InventoryManagementConfig::removeThemeFromV1Config,
              InventoryManagementConfig::migrateV1ScreenPositionsToV2ScreenConfigs));
    }

    return false;
  }

  private static boolean runMigrations(
      Config modConfig, Iterable<Function<Config, Boolean>> migrators) {
    boolean migrated = false;
    for (Function<Config, Boolean> migrator : migrators) {
      migrated = migrator.apply(modConfig) || migrated;
    }
    return migrated;
  }

  private static boolean removeThemeFromV1Config(Config modConfig) {
    if (modConfig.isNull("guiTheme")) {
      return false;
    }

    modConfig.remove("guiTheme");
    return true;
  }

  private static boolean migrateV1ScreenPositionsToV2ScreenConfigs(Config modConfig) {
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

  public static String getScreenKey(Screen screen) {
    MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    String unmapped = mappingResolver.unmapClassName("named", screen.getClass().getName());
    return unmapped.replaceAll("\\.", "-");
  }
}
