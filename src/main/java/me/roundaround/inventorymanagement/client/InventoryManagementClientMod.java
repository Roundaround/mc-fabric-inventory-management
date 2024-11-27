package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.api.positioning.SlotPositionReference;
import me.roundaround.inventorymanagement.api.positioning.TitlePositionReference;
import me.roundaround.inventorymanagement.client.option.KeyBindings;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.mixin.HorseScreenHandlerAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.util.Coords;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ButtonManager.init();
    //    ConfigControlRegister.init();
    KeyBindings.init();

    FabricLoader.getInstance()
        .getModContainer(InventoryManagementMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(
            new Identifier(InventoryManagementMod.MOD_ID, "inventorymanagement-dark-ui"), container,
            Text.literal("Inventory Management Dark UI"), ResourcePackActivationType.NORMAL
        ));

    this.initButtonRegistry();
  }

  private void initButtonRegistry() {
    ButtonRegistry registry = ButtonRegistry.getInstance();

    registry.registerBothSides(ShulkerBoxScreenHandler.class, (context) -> {
      if (context.isPlayerInventory()) {
        return PositioningFunction.refSlotYAndBgRight().apply(context);
      }
      return PositioningFunction.fromPositionRefs(
          TitlePositionReference.left(), TitlePositionReference.bottom(), new Coords(0, 4)).apply(context);
    });

    //    registry.registerBothSides(ShulkerBoxScreenHandler.class);
    registry.registerBothSides(GenericContainerScreenHandler.class);
    registry.registerBothSides(Generic3x3ContainerScreenHandler.class);

    // Not all horses have an inventory
    registry.register(HorseScreenHandler.class).withPlayer().withContainer((context) -> {
      HorseScreenHandlerAccessor accessor = ((HorseScreenHandlerAccessor) context.getScreenHandler());
      return accessor.invokeHasChest(accessor.getEntity());
    });

    // Hopper's container-side is only 1 slot tall, so we need to bump the buttons up a bit to make room
    registry.registerBothSides(HopperScreenHandler.class, (context) -> {
      Coords base = PositioningFunction.refSlotYAndBgRight().apply(context);
      if (context.isPlayerInventory()) {
        return base;
      }

      int containerBot =
          base.y() + context.getButtonsTotalHeight(ButtonManager.BUTTON_HEIGHT, ButtonManager.BUTTON_SPACING);
      int playerTop =
          SlotPositionReference.top(InventoryHelper.getReferenceSlot(context.getScreen(), true)).get(context) +
          PositioningFunction.DEFAULT_SLOT_TOP_OFFSET;

      int adjustment = Math.min(0, playerTop - containerBot - GuiUtil.PADDING);
      return base.movedDown(adjustment);
    });

    registry.registerPlayerSideOnly(PlayerScreenHandler.class);
    // Furnace, smoker, blast furnace
    registry.registerPlayerSideOnly(AbstractFurnaceScreenHandler.class);
    // Anvil, smithing table
    registry.registerPlayerSideOnly(ForgingScreenHandler.class);
    registry.registerPlayerSideOnly(CraftingScreenHandler.class);
    registry.registerPlayerSideOnly(CrafterScreenHandler.class);
    registry.registerPlayerSideOnly(BrewingStandScreenHandler.class);
    registry.registerPlayerSideOnly(StonecutterScreenHandler.class);
    registry.registerPlayerSideOnly(GrindstoneScreenHandler.class);
    registry.registerPlayerSideOnly(CartographyTableScreenHandler.class);
    registry.registerPlayerSideOnly(LoomScreenHandler.class);
    registry.registerPlayerSideOnly(EnchantmentScreenHandler.class);
    registry.registerPlayerSideOnly(BeaconScreenHandler.class);
    registry.registerPlayerSideOnly(MerchantScreenHandler.class);

    // Creative screen dynamically needs to update its reference slot and thus position
    AtomicBoolean wasPreviouslyInventoryTab = new AtomicBoolean(false);
    registry.<CreativeInventoryScreen.CreativeScreenHandler, CreativeInventoryScreen>registerPlayerSideOnly(
        CreativeInventoryScreen.CreativeScreenHandler.class, (context) -> {
          boolean isInventoryTab = context.getScreen().isInventoryTabSelected();
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
