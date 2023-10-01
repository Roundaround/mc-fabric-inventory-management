package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public abstract class InventoryButtonsRegistry {
  public static final SubRegistry<Class<? extends Inventory>, PositioningFunction<?, ?>>
      INVENTORIES = new SubRegistry<>();
  public static final SubRegistry<Class<? extends ScreenHandler>, PositioningFunction<?, ?>>
      SCREEN_HANDLERS = new SubRegistry<>();
  public static final SubRegistry<Class<? extends HandledScreen<?>>, PositioningFunction<?, ?>>
      HANDLED_SCREENS = new SubRegistry<>();

  public static class DefaultOptions<T extends PositioningFunction<?, ?>> {
    private final PerScreenConfig.ScreenConfig config = new PerScreenConfig.ScreenConfig();
    private T positioningFunction = null;

    public void patch(DefaultOptions<T> other) {
      config.patch(other.config);
      if (other.positioningFunction != null) {
        positioningFunction = other.positioningFunction;
      }
    }

    public DefaultOptions<T> withPlayerSideStack() {
      config.setPlayerSideStackVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withPlayerSideTransfer() {
      config.setPlayerSideTransferVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withPlayerSideSort() {
      config.setPlayerSideSortVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withPlayerSideOffset(Position offset) {
      config.setPlayerSideOffset(offset);
      return this;
    }

    public DefaultOptions<T> withContainerSideStack() {
      config.setContainerSideStackVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withContainerSideTransfer() {
      config.setContainerSideTransferVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withContainerSideSort() {
      config.setContainerSideSortVisibility(ButtonVisibility.SHOW);
      return this;
    }

    public DefaultOptions<T> withContainerSideOffset(Position offset) {
      config.setContainerSideOffset(offset);
      return this;
    }

    public DefaultOptions<T> withButtonPositionFunction(T buttonPositionFunction) {
      this.positioningFunction = buttonPositionFunction;
      return this;
    }

    public ButtonVisibility getSortVisibility(boolean isPlayerInventory) {
      return isPlayerInventory
          ? config.getPlayerSideSortVisibility()
          : config.getContainerSideSortVisibility();
    }

    public ButtonVisibility getTransferVisibility(boolean isPlayerInventory) {
      return isPlayerInventory
          ? config.getPlayerSideTransferVisibility()
          : config.getContainerSideTransferVisibility();
    }

    public ButtonVisibility getStackVisibility(boolean isPlayerInventory) {
      return isPlayerInventory
          ? config.getPlayerSideStackVisibility()
          : config.getContainerSideStackVisibility();
    }

    public PositioningFunction<?, ?> getPositioningFunction() {
      return positioningFunction;
    }
  }

  public static class SubRegistry<C extends Class<?>, P extends PositioningFunction<?, ?>> {
    private final HashMap<C, DefaultOptions<P>> store = new HashMap<>();

    public C getAssignableClass(Class<?> clazz) {
      HashSet<C> assignableClasses = new HashSet<>();
      for (C registeredClass : store.keySet()) {
        if (registeredClass.isAssignableFrom(clazz)) {
          assignableClasses.add(registeredClass);
        }
      }

      // Find the most specific assignable class
      C mostSpecificAssignableClass = null;
      for (C assignableClass : assignableClasses) {
        if (mostSpecificAssignableClass == null ||
            assignableClass.isAssignableFrom(mostSpecificAssignableClass)) {
          mostSpecificAssignableClass = assignableClass;
        }
      }

      return mostSpecificAssignableClass;
    }

    public void clear(C clazz) {
      store.remove(clazz);
    }

    public DefaultOptions<P> get(Class<?> clazz) {
      return store.get(getAssignableClass(clazz));
    }

    public void set(C clazz, DefaultOptions<P> options) {
      store.put(clazz, options);
    }

    public void upsert(C clazz, DefaultOptions<P> options) {
      DefaultOptions<P> existingOptions = store.get(clazz);
      if (existingOptions != null) {
        existingOptions.patch(options);
      } else {
        store.put(clazz, options);
      }
    }

    public void sortableAndTransferable(C clazz) {
      upsert(clazz,
          new DefaultOptions<P>().withPlayerSideSort()
              .withContainerSideSort()
              .withPlayerSideTransfer()
              .withContainerSideTransfer()
              .withPlayerSideStack()
              .withContainerSideStack());
    }

    public void sortable(C clazz) {
      upsert(clazz, new DefaultOptions<P>().withPlayerSideSort().withContainerSideSort());
    }

    public void playerSideSortable(C clazz) {
      upsert(clazz, new DefaultOptions<P>().withPlayerSideSort());
    }

    public void containerSideSortable(C clazz) {
      upsert(clazz, new DefaultOptions<P>().withContainerSideSort());
    }

    public void transferable(C clazz) {
      upsert(clazz,
          new DefaultOptions<P>().withPlayerSideTransfer()
              .withContainerSideTransfer()
              .withPlayerSideStack()
              .withContainerSideStack());
    }

    public void playerSideTransferable(C clazz) {
      upsert(clazz, new DefaultOptions<P>().withPlayerSideTransfer().withPlayerSideStack());
    }

    public void containerSideTransferable(C clazz) {
      upsert(clazz, new DefaultOptions<P>().withContainerSideTransfer().withContainerSideStack());
    }

    public void setPositionFunction(C clazz, P positionFunction) {
      upsert(clazz, new DefaultOptions<P>().withButtonPositionFunction(positionFunction));
    }
  }
}
