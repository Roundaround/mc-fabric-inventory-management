package me.roundaround.inventorymanagement.config.value;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;

public class ScreenOverridesStore {
  private final HashMap<HandledScreen<?>, ScreenOverrides> playerHandledScreen = new HashMap<>();
  private final HashMap<ScreenHandler, ScreenOverrides> playerScreenHandler = new HashMap<>();
  private final HashMap<HandledScreen<?>, ScreenOverrides> containerHandledScreen = new HashMap<>();
  private final HashMap<ScreenHandler, ScreenOverrides> containerScreenHandler = new HashMap<>();
}
