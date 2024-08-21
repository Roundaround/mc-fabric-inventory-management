package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
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
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

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
    InventoryButtonsRegistry.INVENTORIES_2.registerBothSides(EnderChestInventory.class);
    InventoryButtonsRegistry.INVENTORIES_2.registerBothSides(LootableContainerBlockEntity.class);

    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerBothSides(ShulkerBoxScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerBothSides(GenericContainerScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerBothSides(Generic3x3ContainerScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerBothSides(HorseScreenHandler.class);

    // Hopper's container-side is only 1 slot tall, so we need to bump the buttons up a bit to make room
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.<HopperScreenHandler, HopperScreen>registerBothSides(
        HopperScreenHandler.class, (context) -> {
          PositioningFunction<HopperScreenHandler, HopperScreen> base = PositioningFunction.refSlotYAndBgRight();
          Position basePosition = base.apply(context);
          if (basePosition == null) {
            return null;
          }

          if (context.isPlayerInventory()) {
            return basePosition;
          }

          return basePosition.movedUp(InventoryButtonsManager.BUTTON_HEIGHT + InventoryButtonsManager.BUTTON_SPACING);
        });

    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(PlayerScreenHandler.class);
    // Furnace, smoker, blast furnace
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(AbstractFurnaceScreenHandler.class);
    // Anvil, smithing table
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(ForgingScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(CraftingScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(CrafterScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(BrewingStandScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(StonecutterScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(GrindstoneScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(CartographyTableScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(LoomScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(EnchantmentScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(BeaconScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.registerPlayerSideOnly(MerchantScreenHandler.class);

    // Creative screen dynamically needs to update its reference slot and thus position
    AtomicBoolean wasPreviouslyInventoryTab = new AtomicBoolean(false);
    InventoryButtonsRegistry.SCREEN_HANDLERS_2.<CreativeInventoryScreen.CreativeScreenHandler,
        CreativeInventoryScreen>registerPlayerSideOnly(
        CreativeInventoryScreen.CreativeScreenHandler.class, (context) -> {
          boolean isInventoryTab = context.getParentScreen().isInventoryTabSelected();
          if (isInventoryTab != wasPreviouslyInventoryTab.get()) {
            context.setReferenceSlot(isInventoryTab ? context.getDefaultReferenceSlot() : null);
          }
          wasPreviouslyInventoryTab.set(isInventoryTab);

          if (!context.hasReferenceSlot()) {
            return null;
          }

          PositioningFunction<CreativeInventoryScreen.CreativeScreenHandler, CreativeInventoryScreen> base =
              PositioningFunction.refSlotYAndBgRight();
          return base.apply(context);
        });

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit());
  }
}
