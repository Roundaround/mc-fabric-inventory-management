package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.mixin.HorseScreenHandlerAccessor;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
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
    ButtonRegistry.INVENTORIES.registerBothSides(EnderChestInventory.class);
    ButtonRegistry.INVENTORIES.registerBothSides(LootableContainerBlockEntity.class);

    ButtonRegistry.SCREEN_HANDLERS.registerBothSides(ShulkerBoxScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerBothSides(GenericContainerScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerBothSides(Generic3x3ContainerScreenHandler.class);

    // Not all horses have an inventory
    ButtonRegistry.SCREEN_HANDLERS.<HorseScreenHandler, HorseScreen>register(HorseScreenHandler.class)
        .withPlayer()
        .withContainer((context) -> {
          HorseScreenHandlerAccessor accessor = ((HorseScreenHandlerAccessor) context.getScreenHandler());
          return accessor.invokeHasChest(accessor.getEntity());
        });

    // Hopper's container-side is only 1 slot tall, so we need to bump the buttons up a bit to make room
    ButtonRegistry.SCREEN_HANDLERS.<HopperScreenHandler, HopperScreen>registerBothSides(
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

    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(PlayerScreenHandler.class);
    // Furnace, smoker, blast furnace
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(AbstractFurnaceScreenHandler.class);
    // Anvil, smithing table
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(ForgingScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(CraftingScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(CrafterScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(BrewingStandScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(StonecutterScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(GrindstoneScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(CartographyTableScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(LoomScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(EnchantmentScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(BeaconScreenHandler.class);
    ButtonRegistry.SCREEN_HANDLERS.registerPlayerSideOnly(MerchantScreenHandler.class);

    // Creative screen dynamically needs to update its reference slot and thus position
    AtomicBoolean wasPreviouslyInventoryTab = new AtomicBoolean(false);
    ButtonRegistry.SCREEN_HANDLERS.<CreativeInventoryScreen.CreativeScreenHandler,
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
