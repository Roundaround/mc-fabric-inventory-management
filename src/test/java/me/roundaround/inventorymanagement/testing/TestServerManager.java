package me.roundaround.inventorymanagement.testing;

import net.fabricmc.fabric.impl.gametest.FabricGameTestModInitializer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.storage.LevelStorage;

import java.nio.file.Paths;

public final class TestServerManager {
  private static MinecraftServer server;

  private TestServerManager() {
  }

  @SuppressWarnings("UnstableApiUsage")
  public static void initialize() {
    if (server != null) {
      return;
    }

    FabricGameTestModInitializer init = new FabricGameTestModInitializer();
    init.onInitialize();

    try {
      ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(Paths.get("run/server.properties"));
      LevelStorage levelStorage = LevelStorage.create(Paths.get("run/world"));
      LevelStorage.Session session = levelStorage.createSession(
          serverPropertiesLoader.getPropertiesHandler().levelName);
      ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);

      server = TestServer.startServer(
          (thread) -> TestServer.create(thread, session, resourcePackManager, TestFunctions.getTestFunctions(),
              BlockPos.ORIGIN
          ));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void close() {
    server = null;
  }
}
