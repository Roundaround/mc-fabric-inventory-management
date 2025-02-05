package me.roundaround.inventorymanagement.api.registry;

import me.roundaround.inventorymanagement.api.gui.SlotRangeFunction;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

public class SlotRangeRegistry {
  private SlotRangeRegistry() {
  }

  public static final Registry<Class<? extends ScreenHandler>> SCREEN_HANDLERS = new Registry<>();
  public static final Registry<Class<? extends Inventory>> INVENTORIES = new Registry<>();

  public static <H extends ScreenHandler> SlotRange getPlayerSide(PlayerEntity player, Inventory inventory) {
    return getPlayerSideFunction(player.currentScreenHandler, inventory).apply(player, inventory, true);
  }

  @SuppressWarnings("unchecked")
  public static <H extends ScreenHandler> SlotRangeFunction<H> getPlayerSideFunction(
      ScreenHandler screenHandler, Inventory inventory
  ) {
    return (SlotRangeFunction<H>) Stream.of(
        SCREEN_HANDLERS.getPlayerSideFunction(screenHandler.getClass()),
        INVENTORIES.getPlayerSideFunction(inventory.getClass())
    ).filter(Optional::isPresent).map(Optional::get).findFirst().orElseGet(SlotRangeFunction::defaultBulkInventory);
  }

  public static <H extends ScreenHandler> SlotRange getContainerSide(PlayerEntity player, Inventory inventory) {
    return getContainerSideFunction(player.currentScreenHandler, inventory).apply(player, inventory, false);
  }

  @SuppressWarnings("unchecked")
  public static <H extends ScreenHandler> SlotRangeFunction<H> getContainerSideFunction(
      ScreenHandler screenHandler, Inventory inventory
  ) {
    return (SlotRangeFunction<H>) Stream.of(
        SCREEN_HANDLERS.getContainerSideFunction(screenHandler.getClass()),
        INVENTORIES.getContainerSideFunction(inventory.getClass())
    ).filter(Optional::isPresent).map(Optional::get).findFirst().orElseGet(SlotRangeFunction::defaultBulkInventory);
  }

  public static class Registry<C extends Class<?>> {
    private final HashMap<C, Registration<?>> store = new HashMap<>();

    @SuppressWarnings("DuplicatedCode")
    public C getAssignableClass(Class<?> clazz) {
      HashSet<C> assignableClasses = new HashSet<>();
      for (C registeredClass : this.store.keySet()) {
        if (registeredClass.isAssignableFrom(clazz)) {
          assignableClasses.add(registeredClass);
        }
      }

      // Find the most specific assignable class
      C mostSpecificAssignableClass = null;
      for (C assignableClass : assignableClasses) {
        if (mostSpecificAssignableClass == null || assignableClass.isAssignableFrom(mostSpecificAssignableClass)) {
          mostSpecificAssignableClass = assignableClass;
        }
      }

      return mostSpecificAssignableClass;
    }

    public <H extends ScreenHandler> RegistrationEditor<H> register(
        C clazz
    ) {
      Registration<H> registration = new Registration<>();
      this.store.put(clazz, registration);
      return new RegistrationEditor<>(registration);
    }

    @SuppressWarnings("unchecked")
    private <H extends ScreenHandler> Registration<H> get(Class<?> clazz) {
      // Unchecked cast is safe because as long as the registration exists, the types should match.
      return (Registration<H>) this.store.get(this.getAssignableClass(clazz));
    }

    public <H extends ScreenHandler> Optional<SlotRangeFunction<H>> getPlayerSideFunction(
        Class<?> clazz
    ) {
      return Optional.ofNullable(this.<H>get(clazz)).map(Registration::getPlayerSide);
    }

    public <H extends ScreenHandler> Optional<SlotRangeFunction<H>> getContainerSideFunction(
        Class<?> clazz
    ) {
      return Optional.ofNullable(this.<H>get(clazz)).map(Registration::getContainerSide);
    }
  }

  public static class Registration<H extends ScreenHandler> {
    private SlotRangeFunction<H> playerSide = null;
    private SlotRangeFunction<H> containerSide = null;

    public SlotRangeFunction<H> getPlayerSide() {
      return this.playerSide;
    }

    public SlotRangeFunction<H> getContainerSide() {
      return this.containerSide;
    }
  }

  public static class RegistrationEditor<H extends ScreenHandler> {
    private final Registration<H> registration;

    public RegistrationEditor(Registration<H> registration) {
      this.registration = registration;
    }

    public RegistrationEditor<H> withPlayerSide(SlotRangeFunction<H> playerSide) {
      this.registration.playerSide = playerSide;
      return this;
    }

    public RegistrationEditor<H> withContainerSide(SlotRangeFunction<H> containerSide) {
      this.registration.containerSide = containerSide;
      return this;
    }

    public RegistrationEditor<H> withRanges(
        SlotRangeFunction<H> playerSide, SlotRangeFunction<H> containerSide
    ) {
      this.registration.playerSide = playerSide;
      this.registration.containerSide = containerSide;
      return this;
    }
  }
}
