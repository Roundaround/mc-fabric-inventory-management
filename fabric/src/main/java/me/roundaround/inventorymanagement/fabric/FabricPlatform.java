package me.roundaround.inventorymanagement.fabric;

import me.roundaround.roundalib.util.Env;
import me.roundaround.roundalib.util.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class FabricPlatform implements Platform {
  @Override
  public Path getGameDir() {
    return FabricLoader.getInstance().getGameDir();
  }

  @Override
  public Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }

  @Override
  public Env getEnv() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Env.CLIENT : Env.SERVER;
  }

  @Override
  public Optional<ModInfo> getModInfo(String modId) {
    return FabricLoader.getInstance().getModContainer(modId).map(c -> new ModInfo(
        c.getMetadata().getName(),
        c.getMetadata().getVersion().getFriendlyString(),
        c.getMetadata().getContact().get("issues")
    ));
  }

  @Override
  public void onServerStarting(Consumer<MinecraftServer> callback) {
    ServerLifecycleEvents.SERVER_STARTING.register(callback::accept);
  }

  @Override
  public void onServerStopped(Consumer<MinecraftServer> callback) {
    ServerLifecycleEvents.SERVER_STOPPED.register(callback::accept);
  }
}
