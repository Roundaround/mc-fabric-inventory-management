package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.InventoryButtonsRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    InventoryButtonsManager.INSTANCE.init();
    ConfigControlRegister.init();

    FabricLoader.getInstance()
        .getModContainer(InventoryManagementMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(
            new Identifier(InventoryManagementMod.MOD_ID, "inventorymanagement-dark-ui"), container,
            Text.literal("Inventory Management Dark UI"), ResourcePackActivationType.NORMAL
        ));

    initKeyBindings();
    initButtonRegistry();
  }

  private void initKeyBindings() {
    KeyBinding configureKeybind = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.configure", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,
            "inventorymanagement.keybind.category"
        ));

    //    HandleScreenInputCallback.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
    //      if (!(screen instanceof HandledScreen)) {
    //        return false;
    //      }
    //
    //      if (configureKeybind.matchesKey(keyCode, scanCode)) {
    //        GuiUtil.setScreen(new PerScreenConfigScreen(screen,
    //            InventoryManagementMod.CONFIG.perScreenConfigs
    //        ));
    //        return true;
    //      }
    //
    //      return false;
    //    });
  }

  private void initButtonRegistry() {
    InventoryButtonsRegistry.INVENTORIES.sortableAndTransferable(EnderChestInventory.class);
    InventoryButtonsRegistry.INVENTORIES.sortableAndTransferable(LootableContainerBlockEntity.class);

    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(ShulkerBoxScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(GenericContainerScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(Generic3x3ContainerScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(HopperScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(HorseScreenHandler.class);

    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(PlayerScreenHandler.class);
    // Furnace, smoker, blast furnace
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(AbstractFurnaceScreenHandler.class);
    // Anvil, smithing table
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(ForgingScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(CraftingScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(CrafterScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(BrewingStandScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(StonecutterScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(GrindstoneScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(CartographyTableScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(LoomScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(EnchantmentScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(BeaconScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.playerSideSortable(MerchantScreenHandler.class);

    // Adjust positioning for hoppers
    InventoryButtonsRegistry.SCREEN_HANDLERS.setPositionFunction(
        HopperScreenHandler.class, (ButtonContext<HopperScreenHandler, HopperScreen> context) -> {
          PositioningFunction<HopperScreenHandler, HopperScreen> base = PositioningFunction.getDefault();
          Position basePosition = base.apply(context);

          if (context.isPlayerInventory()) {
            return basePosition;
          }

          return basePosition.movedUp(InventoryButtonsManager.BUTTON_HEIGHT + InventoryButtonsManager.BUTTON_SPACING);
        });

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit());
  }
}
