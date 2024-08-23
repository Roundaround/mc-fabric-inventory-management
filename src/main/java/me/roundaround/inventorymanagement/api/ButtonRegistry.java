package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ButtonRegistry {
  private ButtonRegistry() {
  }

  public static final Registry<Class<? extends ScreenHandler>> SCREEN_HANDLERS = new Registry<>();
  public static final Registry<Class<? extends HandledScreen<?>>> HANDLED_SCREENS = new Registry<>();
  public static final Registry<Class<? extends Inventory>> INVENTORIES = new Registry<>();

  public static <H extends ScreenHandler, S extends HandledScreen<H>> List<ButtonVisibility> getSortButtonVisibility(
      ButtonContext<H, S> context
  ) {
    return Stream.of(
        SCREEN_HANDLERS.isSortable(context.getScreenHandlerClass(), context),
        HANDLED_SCREENS.isSortable(context.getScreenClass(), context),
        INVENTORIES.isSortable(context.getInventoryClass(), context)
    ).map(ButtonVisibility::of).toList();
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> List<ButtonVisibility> getTransferAndStackButtonVisibility(
      ButtonContext<H, S> context
  ) {
    return Stream.of(
        SCREEN_HANDLERS.supportsTransferring(context.getScreenHandlerClass(), context),
        HANDLED_SCREENS.supportsTransferring(context.getScreenClass(), context),
        INVENTORIES.supportsTransferring(context.getInventoryClass(), context)
    ).map(ButtonVisibility::of).toList();
  }

  @SuppressWarnings("unchecked")
  public static <H extends ScreenHandler, S extends HandledScreen<H>> Optional<PositioningFunction<H, S>> getPositioningFunction(
      ButtonContext<H, S> context
  ) {
    return Stream.of(
            SCREEN_HANDLERS.getPositioningFunction(context.getScreenHandlerClass()),
            HANDLED_SCREENS.getPositioningFunction(context.getScreenClass()),
            INVENTORIES.getPositioningFunction(context.getInventoryClass())
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .map((positioningFunction) -> (PositioningFunction<H, S>) positioningFunction);
  }

  public static class Registry<C extends Class<?>> {
    private final HashMap<C, Registration<?, ?>> store = new HashMap<>();

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
      return Optional.of(isPlayerSide && registration.getHasPlayerInventory(context) ||
          !isPlayerSide && registration.getHasContainerInventory(context));
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

      return Optional.of(registration.getHasPlayerInventory(context) && registration.getHasContainerInventory(context));
    }

    public <H extends ScreenHandler, S extends HandledScreen<H>> Optional<PositioningFunction<H, S>> getPositioningFunction(
        Class<?> clazz
    ) {
      return Optional.ofNullable(this.<H, S>get(clazz)).map(Registration::getPositioningFunction);
    }
  }

  public static class Registration<H extends ScreenHandler, S extends HandledScreen<H>> {
    private Function<ButtonContext<H, S>, Boolean> hasPlayerInventory = (context) -> false;
    private Function<ButtonContext<H, S>, Boolean> hasContainerInventory = (context) -> false;
    private PositioningFunction<H, S> positioningFunction = null;

    public boolean getHasPlayerInventory(ButtonContext<H, S> context) {
      return this.hasPlayerInventory.apply(context);
    }

    public boolean getHasContainerInventory(ButtonContext<H, S> context) {
      return this.hasContainerInventory.apply(context);
    }

    public PositioningFunction<H, S> getPositioningFunction() {
      return this.positioningFunction;
    }
  }

  public static class RegistrationEditor<H extends ScreenHandler, S extends HandledScreen<H>> {
    private final Registration<H, S> registration;

    public RegistrationEditor(Registration<H, S> registration) {
      this.registration = registration;
    }

    public RegistrationEditor<H, S> withInventories(boolean hasPlayerInventory, boolean hasContainerInventory) {
      this.registration.hasPlayerInventory = (context) -> hasPlayerInventory;
      this.registration.hasContainerInventory = (context) -> hasContainerInventory;
      return this;
    }

    public RegistrationEditor<H, S> withPlayerAndContainer() {
      return this.withInventories(true, true);
    }

    public RegistrationEditor<H, S> withPlayer() {
      return this.withInventories((context) -> true, this.registration.hasContainerInventory);
    }

    public RegistrationEditor<H, S> withContainer() {
      return this.withInventories(this.registration.hasPlayerInventory, (context) -> true);
    }

    public RegistrationEditor<H, S> withInventories(
        Function<ButtonContext<H, S>, Boolean> hasPlayerInventory,
        Function<ButtonContext<H, S>, Boolean> hasContainerInventory
    ) {
      this.registration.hasPlayerInventory = hasPlayerInventory;
      this.registration.hasContainerInventory = hasContainerInventory;
      return this;
    }

    public RegistrationEditor<H, S> withPlayer(Function<ButtonContext<H, S>, Boolean> dynamicallyHasPlayerInventory) {
      return this.withInventories(dynamicallyHasPlayerInventory, this.registration.hasContainerInventory);
    }

    public RegistrationEditor<H, S> withContainer(Function<ButtonContext<H, S>, Boolean> dynamicallyHasContainerInventory) {
      return this.withInventories(this.registration.hasPlayerInventory, dynamicallyHasContainerInventory);
    }

    public RegistrationEditor<H, S> withPosition(PositioningFunction<H, S> positioningFunction) {
      this.registration.positioningFunction = positioningFunction;
      return this;
    }
  }
}
