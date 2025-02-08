package me.roundaround.inventorymanagement.testing;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import net.minecraft.datafixer.Schemas;
import net.minecraft.registry.*;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.ApiServices;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.util.profiler.log.DebugSampleLog;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

import java.net.Proxy;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SimpleTestServer extends MinecraftServer {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final ApiServices NONE_API_SERVICES = new ApiServices(null, ServicesKeySet.EMPTY, null, null);
  private static final GameRules GAME_RULES = Util.make(new GameRules(), gameRules -> {
    gameRules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
    gameRules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);
    gameRules.get(GameRules.RANDOM_TICK_SPEED).set(0, null);
  });
  private static final GeneratorOptions GENERATOR_OPTIONS = new GeneratorOptions(0L, false, false);

  private static SimpleTestServer instance;

  private final MultiValueDebugSampleLogImpl debugSampleLog = new MultiValueDebugSampleLogImpl(4);

  public static void create() {
    if (instance != null) {
      return;
    }

    try {
      ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(Paths.get("server.properties"));
      LevelStorage levelStorage = LevelStorage.create(Paths.get("world"));
      LevelStorage.Session session =
          levelStorage.createSession(serverPropertiesLoader.getPropertiesHandler().levelName);
      ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);

      instance = SimpleTestServer.startServer((thread) -> SimpleTestServer.load(thread, session, resourcePackManager));
    } catch (Exception e) {
      LOGGER.error("Failed to start test server");
      throw new RuntimeException(e);
    }
  }

  public static void cleanup() {
    instance = null;
  }

  private static SimpleTestServer load(
      Thread thread, LevelStorage.Session session, ResourcePackManager resourcePackManager
  ) {
    resourcePackManager.scanPacks();

    DataConfiguration dataConfiguration =
        new DataConfiguration(new DataPackSettings(new ArrayList<>(resourcePackManager.getIds()),
        List.of()
    ), FeatureFlags.FEATURE_MANAGER.getFeatureSet());
    LevelInfo levelInfo = new LevelInfo("Test Level",
        GameMode.CREATIVE,
        false,
        Difficulty.NORMAL,
        true,
        GAME_RULES,
        dataConfiguration
    );
    SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, false, true);
    SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks,
        CommandManager.RegistrationEnvironment.DEDICATED,
        4
    );

    try {
      SaveLoader saveLoader = Util.waitAndApply((executor) -> SaveLoading.load(serverConfig, (context) -> {
        Registry<DimensionOptions> registry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
        DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = context.worldGenRegistryManager()
            .get(RegistryKeys.WORLD_PRESET)
            .entryOf(WorldPresets.FLAT)
            .value()
            .createDimensionsRegistryHolder()
            .toConfig(registry);
        return new SaveLoading.LoadContext<>(
            new LevelProperties(levelInfo,
                GENERATOR_OPTIONS,
                dimensionsConfig.specialWorldProperty(),
                dimensionsConfig.getLifecycle()
            ),
            dimensionsConfig.toDynamicRegistryManager()
        );
      }, SaveLoader::new, Util.getMainWorkerExecutor(), executor)).get();
      return new SimpleTestServer(thread, session, resourcePackManager, saveLoader);
    } catch (Exception e) {
      LOGGER.warn("Failed to load vanilla datapack, bit oops", e);
      System.exit(-1);
      throw new IllegalStateException();
    }
  }

  private SimpleTestServer(
      Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader
  ) {
    super(serverThread,
        session,
        dataPackManager,
        saveLoader,
        Proxy.NO_PROXY,
        Schemas.getFixer(),
        NONE_API_SERVICES,
        WorldGenerationProgressLogger::create
    );

    this.setPlayerManager(new TestPlayerManager(this, this.getCombinedDynamicRegistries(), this.saveHandler));
  }

  @Override
  public boolean setupServer() {
    this.loadWorld();
    LOGGER.info("Started test server");
    return true;
  }

  @Override
  public DebugSampleLog getDebugSampleLog() {
    return this.debugSampleLog;
  }

  @Override
  public boolean shouldPushTickTimeLog() {
    return false;
  }

  @Override
  public void runTasksTillTickEnd() {
    this.runTasks();
  }

  @Override
  public SystemDetails addExtraSystemDetails(SystemDetails details) {
    details.addSection("Type", "Test server");
    return details;
  }

  @Override
  public void exit() {
    super.exit();
    LOGGER.info("Test server shutting down");
  }

  @Override
  public void setCrashReport(CrashReport report) {
    super.setCrashReport(report);
    LOGGER.error("Test server crashed\n{}", report.asString());
    System.exit(1);
  }

  @Override
  public boolean isHardcore() {
    return false;
  }

  @Override
  public int getOpPermissionLevel() {
    return 0;
  }

  @Override
  public int getFunctionPermissionLevel() {
    return 4;
  }

  @Override
  public boolean shouldBroadcastRconToOps() {
    return false;
  }

  @Override
  public boolean isDedicated() {
    return false;
  }

  @Override
  public int getRateLimit() {
    return 0;
  }

  @Override
  public boolean isUsingNativeTransport() {
    return false;
  }

  @Override
  public boolean areCommandBlocksEnabled() {
    return true;
  }

  @Override
  public boolean isRemote() {
    return false;
  }

  @Override
  public boolean shouldBroadcastConsoleToOps() {
    return false;
  }

  @Override
  public boolean isHost(GameProfile profile) {
    return false;
  }

  private static class TestPlayerManager extends PlayerManager {
    public TestPlayerManager(
        MinecraftServer server,
        CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager,
        WorldSaveHandler saveHandler
    ) {
      super(server, registryManager, saveHandler, 1);
    }
  }
}
