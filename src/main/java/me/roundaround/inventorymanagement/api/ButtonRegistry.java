package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ButtonRegistry {
  private static ButtonRegistry instance = null;

  private final HashMap<Class<? extends ScreenHandler>, Registration<?, ?>> store = new HashMap<>();

  private ButtonRegistry() {
  }

  public static ButtonRegistry getInstance() {
    if (instance == null) {
      instance = new ButtonRegistry();
    }
    return instance;
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> register(
      Class<? extends ScreenHandler> clazz
  ) {
    Registration<H, S> registration = new Registration<>();
    this.store.put(clazz, registration);
    return new RegistrationEditor<>(registration);
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerBothSides(
      Class<? extends ScreenHandler> clazz
  ) {
    return this.<H, S>register(clazz).withPlayerAndContainer();
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerBothSides(
      Class<? extends ScreenHandler> clazz, PositioningFunction<H, S> positioningFunction
  ) {
    return this.<H, S>registerBothSides(clazz).withPosition(positioningFunction);
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerPlayerSideOnly(
      Class<? extends ScreenHandler> clazz
  ) {
    return this.<H, S>register(clazz).withPlayer();
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerPlayerSideOnly(
      Class<? extends ScreenHandler> clazz, PositioningFunction<H, S> positioningFunction
  ) {
    return this.<H, S>registerPlayerSideOnly(clazz).withPosition(positioningFunction);
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerContainerSideOnly(
      Class<? extends ScreenHandler> clazz
  ) {
    return this.<H, S>register(clazz).withContainer();
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> RegistrationEditor<H, S> registerContainerSideOnly(
      Class<? extends ScreenHandler> clazz, PositioningFunction<H, S> positioningFunction
  ) {
    return this.<H, S>registerContainerSideOnly(clazz).withPosition(positioningFunction);
  }

  @SuppressWarnings("DuplicatedCode")
  public <H extends ScreenHandler, S extends HandledScreen<H>> ButtonVisibility getSortButtonVisibility(
      ButtonContext<H, S> context
  ) {
    Class<? extends ScreenHandler> clazz = context.getScreenHandlerClass();
    if (clazz == null) {
      return ButtonVisibility.HIDE;
    }

    Registration<H, S> registration = this.get(clazz);
    if (registration == null) {
      return ButtonVisibility.DEFAULT;
    }

    boolean isPlayerSide = context.isPlayerInventory();
    return ButtonVisibility.of(isPlayerSide && registration.getHasPlayerInventory(context) ||
        !isPlayerSide && registration.getHasContainerInventory(context));
  }

  @SuppressWarnings("DuplicatedCode")
  public <H extends ScreenHandler, S extends HandledScreen<H>> ButtonVisibility getTransferAndStackButtonVisibility(
      ButtonContext<H, S> context
  ) {
    Class<? extends ScreenHandler> clazz = context.getScreenHandlerClass();
    if (clazz == null) {
      return ButtonVisibility.HIDE;
    }

    Registration<H, S> registration = this.get(clazz);
    if (registration == null) {
      return ButtonVisibility.DEFAULT;
    }

    boolean isPlayerSide = context.isPlayerInventory();
    return ButtonVisibility.of(
        registration.getHasPlayerInventory(context) && registration.getHasContainerInventory(context));
  }

  public <H extends ScreenHandler, S extends HandledScreen<H>> Optional<PositioningFunction<H, S>> getPositioningFunction(
      ButtonContext<H, S> context
  ) {
    Class<? extends ScreenHandler> clazz = context.getScreenHandlerClass();
    if (clazz == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(this.<H, S>get(clazz)).map(Registration::getPositioningFunction);
  }

  @SuppressWarnings("unchecked")
  private <H extends ScreenHandler, S extends HandledScreen<H>> Registration<H, S> get(Class<? extends ScreenHandler> clazz) {
    // Unchecked cast is safe because as long as the registration exists, the types should match.
    return (Registration<H, S>) this.store.get(this.getAssignableClass(clazz));
  }

  @SuppressWarnings({"DuplicatedCode", "unchecked"})
  private <C extends Class<? extends ScreenHandler>> C getAssignableClass(C clazz) {
    HashSet<C> assignableClasses = new HashSet<>();
    for (Class<? extends ScreenHandler> registeredClass : this.store.keySet()) {
      if (registeredClass.isAssignableFrom(clazz)) {
        assignableClasses.add((C) registeredClass);
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
