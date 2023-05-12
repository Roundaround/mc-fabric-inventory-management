package me.roundaround.inventorymanagement.compat.roundalib;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.gui.screen.DefaultPositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ConfigControlRegister {
  private static final MinecraftClient client = MinecraftClient.getInstance();

  private ConfigControlRegister() {
  }

  public static void init() {
    try {
      ControlRegistry.register(InventoryManagementMod.CONFIG.DEFAULT_POSITION.getId(),
          ConfigControlRegister::defaultPositionEditScreenControlFactory);
    } catch (ControlRegistry.RegistrationException e) {
      // Deal with this later xD
    }
  }

  private static SubScreenControl<Position, PositionConfigOption> defaultPositionEditScreenControlFactory(
      ConfigListWidget.OptionEntry<Position, PositionConfigOption> parent) {
    SubScreenControl<Position, PositionConfigOption> control =
        new SubScreenControl<>(parent, DefaultPositionEditScreen.getSubScreenFactory());

    ((ButtonWidget) control.children().get(0)).setMessage(Text.literal(parent.getOption()
        .getValue()
        .toString()));

    parent.getOption().subscribeToValueChanges(client.currentScreen, (prev, curr) -> {
      ((ButtonWidget) control.children().get(0)).setMessage(Text.literal(curr.toString()));
    });

    return control;
  }
}
