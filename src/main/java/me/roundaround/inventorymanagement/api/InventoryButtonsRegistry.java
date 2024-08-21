package me.roundaround.inventorymanagement.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class InventoryButtonsRegistry {
  private InventoryButtonsRegistry() {
  }

  public static final Registry<Class<? extends Inventory>> INVENTORIES_2 = new Registry<>();
  public static final Registry<Class<? extends ScreenHandler>> SCREEN_HANDLERS_2 = new Registry<>();
  public static final Registry<Class<? extends HandledScreen<?>>> HANDLED_SCREENS_2 = new Registry<>();

  @SuppressWarnings("unchecked")
  public static <H extends ScreenHandler, S extends HandledScreen<H>> Optional<PositioningFunction<H, S>> getPositioningFunction(
      ButtonContext<H, S> context
  ) {
    return Stream.of(
            SCREEN_HANDLERS_2.getPositioningFunction(context.getScreenHandlerClass()),
            HANDLED_SCREENS_2.getPositioningFunction(context.getScreenClass()),
            INVENTORIES_2.getPositioningFunction(context.getInventoryClass())
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .map((positioningFunction) -> (PositioningFunction<H, S>) positioningFunction);
  }

  public static class Registry<C extends Class<?>> {
    private final HashMap<C, Registration<?, ?>> store = new HashMap<>();

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

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> register(
        C clazz
    ) {
      Registration<H, S> registration = new Registration<>();
      this.store.put(clazz, registration);
      return new RegistrationEditor<>(registration);
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerBothSides(
        C clazz
    ) {
      return this.<H, S>register(clazz).withPlayerAndContainer();
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerBothSides(
        C clazz, PositioningFunction<H, S> positioningFunction
    ) {
      return this.<H, S>registerBothSides(clazz).withPosition(positioningFunction);
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerPlayerSideOnly(
        C clazz
    ) {
      return this.<H, S>register(clazz).withPlayer();
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerPlayerSideOnly(
        C clazz, PositioningFunction<H, S> positioningFunction
    ) {
      return this.<H, S>registerPlayerSideOnly(clazz).withPosition(positioningFunction);
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerContainerSideOnly(
        C clazz
    ) {
      return this.<H, S>register(clazz).withContainer();
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerContainerSideOnly(
        C clazz, PositioningFunction<H, S> positioningFunction
    ) {
      return this.<H, S>registerContainerSideOnly(clazz).withPosition(positioningFunction);
    }

    @SuppressWarnings("unchecked")
    private <H extends ScreenHandler, S extends HandledScreen<H>> Registration<H, S> get(Class<?> clazz) {
      // Unchecked cast is safe because as long as the registration exists, the types should match.
      return (Registration<H, S>) this.store.get(this.getAssignableClass(clazz));
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> Optional<PositioningFunction<H, S>> getPositioningFunction(
        Class<?> clazz
    ) {
      return Optional.ofNullable(this.<H, S>get(clazz)).map(Registration::getPositioningFunction);
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> Optional<Boolean> isSortable(
        Class<?> clazz, ButtonContext<H, S> context
    ) {
      if (clazz == null) {
        return Optional.empty();
      }

      Registration<H, S> registration = this.get(clazz);
      if (registration == null) {
        return Optional.empty();
      }

      boolean isPlayerSide = context.isPlayerInventory();

      Function<ButtonContext<H, S>, Boolean> producer = isPlayerSide ?
          registration.dynamicallyHasPlayerInventory :
          registration.dynamicallyHasContainerInventory;
      if (producer != null) {
        return Optional.of(producer.apply(context));
      }

      return Optional.of(
          isPlayerSide && registration.hasPlayerInventory || !isPlayerSide && registration.hasContainerInventory);
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> Optional<Boolean> supportsTransferring(
        Class<?> clazz, ButtonContext<H, S> context
    ) {
      if (clazz == null) {
        return Optional.empty();
      }

      Registration<H, S> registration = this.get(clazz);
      if (registration == null) {
        return Optional.empty();
      }

      boolean hasPlayerInventory = registration.dynamicallyHasPlayerInventory != null ?
          registration.dynamicallyHasPlayerInventory.apply(context) :
          registration.hasPlayerInventory;
      boolean hasContainerInventory = registration.dynamicallyHasContainerInventory != null ?
          registration.dynamicallyHasContainerInventory.apply(context) :
          registration.hasContainerInventory;

      return Optional.of(hasContainerInventory && hasPlayerInventory);
    }
  }

  public static class Registration<H extends ScreenHandler, S extends HandledScreen<H>> {
    private boolean hasPlayerInventory = false;
    private boolean hasContainerInventory = false;
    private Function<ButtonContext<H, S>, Boolean> dynamicallyHasPlayerInventory = null;
    private Function<ButtonContext<H, S>, Boolean> dynamicallyHasContainerInventory = null;
    private PositioningFunction<H, S> positioningFunction = null;

    public boolean isHasPlayerInventory() {
      return hasPlayerInventory;
    }

    public boolean isHasContainerInventory() {
      return hasContainerInventory;
    }

    public Function<ButtonContext<H, S>, Boolean> getDynamicallyHasPlayerInventory() {
      return dynamicallyHasPlayerInventory;
    }

    public Function<ButtonContext<H, S>, Boolean> getDynamicallyHasContainerInventory() {
      return dynamicallyHasContainerInventory;
    }

    public PositioningFunction<H, S> getPositioningFunction() {
      return positioningFunction;
    }
  }

  public static class RegistrationEditor<H extends ScreenHandler, S extends HandledScreen<H>> {
    private final Registration<H, S> registration;

    public RegistrationEditor(Registration<H, S> registration) {
      this.registration = registration;
    }

    public RegistrationEditor<H, S> withInventories(boolean hasPlayerInventory, boolean hasContainerInventory) {
      this.registration.hasPlayerInventory = hasPlayerInventory;
      this.registration.hasContainerInventory = hasContainerInventory;
      return this;
    }

    public RegistrationEditor<H, S> withPlayerAndContainer() {
      return this.withInventories(true, true);
    }

    public RegistrationEditor<H, S> withPlayer() {
      return this.withInventories(true, this.registration.hasContainerInventory);
    }

    public RegistrationEditor<H, S> withContainer() {
      return this.withInventories(this.registration.hasPlayerInventory, true);
    }

    public RegistrationEditor<H, S> withInventories(
        Function<ButtonContext<H, S>, Boolean> dynamicallyHasPlayerInventory,
        Function<ButtonContext<H, S>, Boolean> dynamicallyHasContainerInventory
    ) {
      this.registration.dynamicallyHasPlayerInventory = dynamicallyHasPlayerInventory;
      this.registration.dynamicallyHasContainerInventory = dynamicallyHasContainerInventory;
      return this;
    }

    public RegistrationEditor<H, S> withPlayer(Function<ButtonContext<H, S>, Boolean> dynamicallyHasPlayerInventory) {
      return this.withInventories(dynamicallyHasPlayerInventory, this.registration.dynamicallyHasContainerInventory);
    }

    public RegistrationEditor<H, S> withContainer(Function<ButtonContext<H, S>, Boolean> dynamicallyHasContainerInventory) {
      return this.withInventories(this.registration.dynamicallyHasPlayerInventory, dynamicallyHasContainerInventory);
    }

    public RegistrationEditor<H, S> withPosition(PositioningFunction<H, S> positioningFunction) {
      this.registration.positioningFunction = positioningFunction;
      return this;
    }
  }
}
