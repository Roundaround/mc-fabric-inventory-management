package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.gui.ButtonRegistry;
import me.roundaround.inventorymanagement.api.gui.positioning.Coords;
import me.roundaround.inventorymanagement.api.gui.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.api.gui.positioning.SlotPositionReference;
import me.roundaround.inventorymanagement.api.sorting.ItemVariantRegistry;
import me.roundaround.inventorymanagement.api.sorting.VariantGroup;
import me.roundaround.inventorymanagement.client.option.KeyBindings;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.mixin.HorseScreenHandlerAccessor;
import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import me.roundaround.inventorymanagement.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
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
            Text.translatable("inventorymanagement.resource.dark"), ResourcePackActivationType.NORMAL
        ));

    this.initButtonRegistry();
    this.initItemVariantRegistries();

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit());
  }

  private void initButtonRegistry() {
    ButtonRegistry registry = ButtonRegistry.getInstance();

    registry.registerBothSides(ShulkerBoxScreenHandler.class);
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
  }

  private void initItemVariantRegistries() {
    ItemVariantRegistry color = ItemVariantRegistry.COLOR;
    color.register(VariantGroup.by(Items.SHULKER_BOX, ConventionalItemTags.SHULKER_BOXES));
    // TODO: Replace with ConventionalItemTags.GLASS_BLOCKS_CHEAP starting in 1.21
    color.register(VariantGroup.by(Items.GLASS, InventoryManagementItemTags.GLASSES));
    color.register(VariantGroup.by(Items.GLASS_PANE, ConventionalItemTags.GLASS_PANES));
    color.register(VariantGroup.by(ItemTags.WOOL));
    color.register(VariantGroup.by(ItemTags.WOOL_CARPETS));
    color.register(VariantGroup.by(ConventionalItemTags.DYES));
    color.register(VariantGroup.by(Items.CAKE, InventoryManagementItemTags.CAKES));
    color.register(VariantGroup.by(ItemTags.CANDLES));
    color.register(VariantGroup.by(ItemTags.BEDS));
    color.register(VariantGroup.by(ItemTags.BANNERS));
    color.register(VariantGroup.by(ItemTags.TERRACOTTA));
    // TODO: Replace with ConventionalItemTags.GLAZED_TERRACOTTAS starting in 1.21
    color.register(VariantGroup.by(InventoryManagementItemTags.GLAZED_TERRACOTTAS));
    // TODO: Replace with ConventionalItemTags.CONCRETES starting in 1.21
    color.register(VariantGroup.by(InventoryManagementItemTags.CONCRETES));
    // TODO: Replace with ConventionalItemTags.CONCRETE_POWDERS starting in 1.21
    color.register(VariantGroup.by(InventoryManagementItemTags.CONCRETE_POWDERS));

    ItemVariantRegistry material = ItemVariantRegistry.MATERIAL;
    material.register(VariantGroup.by(ConventionalItemTags.BARRELS));
    material.register(VariantGroup.by("Boats", ItemTags.BOATS));
    material.register(VariantGroup.by(ConventionalItemTags.BOOKSHELVES));
    material.register(VariantGroup.by("Buttons", ItemTags.BUTTONS));
    material.register(VariantGroup.by(ConventionalItemTags.CHESTS));
    material.register(VariantGroup.by("Doors", ItemTags.DOORS));
    material.register(VariantGroup.by("Fences", ItemTags.FENCES));
    material.register(VariantGroup.by("Fence gates", ItemTags.FENCE_GATES));
    material.register(VariantGroup.by("Hanging signs", ItemTags.HANGING_SIGNS));
    material.register(VariantGroup.by("Leaves", ItemTags.LEAVES));
    material.register(VariantGroup.by("Logs", InventoryManagementItemTags.NATURAL_LOGS));
    material.register(VariantGroup.by("Planks", ItemTags.PLANKS));
    material.register(VariantGroup.by("Pressure plates", InventoryManagementItemTags.PRESSURE_PLATES));
    material.register(VariantGroup.by("Rails", ItemTags.RAILS));
    material.register(VariantGroup.by("Saplings", ItemTags.SAPLINGS));
    // TODO: Replace with ConventionalItemTags.SEEDS starting in 1.21
    material.register(VariantGroup.by("Seeds", InventoryManagementItemTags.SEEDS));
    material.register(VariantGroup.by("Signs", ItemTags.SIGNS));
    material.register(VariantGroup.by("Slabs", ItemTags.SLABS));
    material.register(VariantGroup.by("Stairs", ItemTags.STAIRS));
    // TODO: Replace with ConventionalItemTags.STRIPPED_LOGS starting in 1.21
    material.register(VariantGroup.by("Stripped logs", InventoryManagementItemTags.STRIPPED_LOGS));
    // TODO: Replace with ConventionalItemTags.STRIPPED_WOODS starting in 1.21
    material.register(VariantGroup.by("Stripped woods", InventoryManagementItemTags.STRIPPED_WOODS));
    material.register(VariantGroup.by("Trapdoors", ItemTags.TRAPDOORS));
    material.register(VariantGroup.by("Walls", ItemTags.WALLS));
    material.register(VariantGroup.by("Woods", InventoryManagementItemTags.NATURAL_WOODS));

    ItemVariantRegistry shape = ItemVariantRegistry.SHAPE;
    shape.register(VariantGroup.by(ItemTags.ANVIL));
    shape.register(VariantGroup.by(ConventionalItemTags.POTIONS));
    shape.register(VariantGroup.by("Coppers", InventoryManagementItemTags.COPPERS));
    // TODO: Other cuttable stones
    // TODO: Pottery sherds (and shards?)
    // TODO: Equipment?
  }
}
