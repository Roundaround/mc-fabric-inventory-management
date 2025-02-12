package me.roundaround.inventorymanagement.config;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.config.option.IntListConfigOption;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.inventorymanagement.roundalib.config.ConfigPath;
import me.roundaround.inventorymanagement.roundalib.config.manage.ModConfigImpl;
import me.roundaround.inventorymanagement.roundalib.config.manage.store.GameScopedFileStore;
import me.roundaround.inventorymanagement.roundalib.config.option.BooleanConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.option.PositionConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import me.roundaround.inventorymanagement.roundalib.nightconfig.core.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class GameScopedConfig extends ModConfigImpl implements GameScopedFileStore {
  private static GameScopedConfig instance = null;

  public static GameScopedConfig getInstance() {
    if (instance == null) {
      instance = new GameScopedConfig();
    }
    return instance;
  }

  public BooleanConfigOption modEnabled;
  public BooleanConfigOption showSort;
  public BooleanConfigOption showTransfer;
  public BooleanConfigOption showStack;
  public IntListConfigOption lockedInventorySlots;
  public PositionConfigOption defaultPosition;
  public PerScreenConfigOption perScreenConfigs;

  public GameScopedConfig() {
    super(InventoryManagementMod.MOD_ID, 2);
  }

  @Override
  protected void registerOptions() {
    this.modEnabled = this.register(BooleanConfigOption.builder(ConfigPath.of("modEnabled"))
        .setDefaultValue(true)
        .setComment("Simple toggle for the mod! Set to false to disable.")
        .build());

    this.showSort = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("showSort"))
        .setDefaultValue(true)
        .setComment("Whether or not to show sort buttons in the UI.")
        .build());

    this.showTransfer = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("showTransfer"))
        .setDefaultValue(true)
        .setComment("Whether or not to show transfer buttons in the UI.")
        .build());

    this.showStack = this.register(BooleanConfigOption.yesNoBuilder(ConfigPath.of("showStack"))
        .setDefaultValue(true)
        .setComment("Whether or not to show autostack buttons in the UI.")
        .build());

    this.lockedInventorySlots = this.buildRegistration(IntListConfigOption.builder(ConfigPath.of(
        "lockedInventorySlots"))
        .setComment("Which slots in your inventory should be skipped when transferring or sorting items.")
        .build()).noGuiControl().commit();

    this.defaultPosition = this.buildRegistration(PositionConfigOption.builder(ConfigPath.of("defaultPosition"))
        .setDefaultValue(new Position(0, 0))
        .onUpdate((option) -> option.setDisabled(
            !this.showSort.getValue() && !this.showTransfer.getValue() && !this.showStack.getValue()))
        .setComment("Customize a default for button position.")
        .build()).noGuiControl().commit();

    this.perScreenConfigs = this.buildRegistration(PerScreenConfigOption.builder(ConfigPath.of("perScreenConfigs"))
        .setDefaultValue(new PerScreenConfig())
        .setComment("Customize settings on a per-screen basis.")
        .build()).noGuiControl().commit();
  }

  @Override
  public boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    Config modConfig = inMemoryConfigSnapshot.get("inventorymanagement");
    if (modConfig == null) {
      return false;
    }

    if (versionSnapshot == 1) {
      return runMigrations(modConfig,
          List.of(GameScopedConfig::removeThemeFromV1Config,
              GameScopedConfig::migrateV1ScreenPositionsToV2ScreenConfigs
          )
      );
    }

    return false;
  }

  private static boolean runMigrations(
      Config modConfig, Iterable<Function<Config, Boolean>> migrators
  ) {
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
    screenPositionsConfig.valueMap()
        .keySet()
        .forEach((key) -> screenPositions.put(key, Position.fromString(screenPositionsConfig.get(key))));

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
