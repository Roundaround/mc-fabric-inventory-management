//package me.roundaround.inventorymanagement.compat.roundalib;
//
//import me.roundaround.inventorymanagement.client.gui.screen.DefaultPositionEditScreen;
//import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
//import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
//import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
//import me.roundaround.roundalib.config.option.PositionConfigOption;
//import me.roundaround.roundalib.config.value.Position;
//import net.minecraft.client.MinecraftClient;
//
//public class ConfigControlRegister {
//  private static final MinecraftClient client = MinecraftClient.getInstance();
//
//  private ConfigControlRegister() {
//  }
//
//  public static void init() {
//    try {
//      ControlRegistry.register(InventoryManagementConfig.getInstance().defaultPosition.getId(),
//          ConfigControlRegister::getSubScreenControl
//      );
//    } catch (ControlRegistry.RegistrationException e) {
//      // Deal with this later xD
//    }
//  }
//
//  private static SubScreenControl<Position, PositionConfigOption> getSubScreenControl(
//      MinecraftClient client, PositionConfigOption option, int width, int height
//  ) {
//    return new SubScreenControl<>(client, option, width, height, SubScreenControl.getValueDisplayMessageFactory(),
//        DefaultPositionEditScreen.getSubScreenFactory()
//    );
//  }
//}
