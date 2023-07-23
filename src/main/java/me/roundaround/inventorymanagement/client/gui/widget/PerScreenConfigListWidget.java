package me.roundaround.inventorymanagement.client.gui.widget;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class PerScreenConfigListWidget
    extends VariableHeightListWidget<PerScreenConfigListWidget.Entry> {
  private static final int BUTTON_WIDTH = 100;
  private static final int BUTTON_HEIGHT = 20;

  private final Screen parent;

  public PerScreenConfigListWidget(
      MinecraftClient client,
      Screen parent,
      int left,
      int top,
      int width,
      int height,
      PerScreenConfigOption configOption,
      Screen screen) {
    super(client, left, top, width, height);

    this.parent = parent;

    this.addEntry(new GroupEntry(this.client, this, Text.of("Enable/disable buttons")));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side sort"),
        screen,
        configOption,
        true,
        PerScreenConfigOption::getSortVisibility,
        PerScreenConfigOption::setSortVisibility,
        PerScreenConfigOption::clearSortVisibility));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side transfer"),
        screen,
        configOption,
        true,
        PerScreenConfigOption::getTransferVisibility,
        PerScreenConfigOption::setTransferVisibility,
        PerScreenConfigOption::clearTransferVisibility));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side stack"),
        screen,
        configOption,
        true,
        PerScreenConfigOption::getStackVisibility,
        PerScreenConfigOption::setStackVisibility,
        PerScreenConfigOption::clearStackVisibility));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side sort"),
        screen,
        configOption,
        false,
        PerScreenConfigOption::getSortVisibility,
        PerScreenConfigOption::setSortVisibility,
        PerScreenConfigOption::clearSortVisibility));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side transfer"),
        screen,
        configOption,
        false,
        PerScreenConfigOption::getTransferVisibility,
        PerScreenConfigOption::setTransferVisibility,
        PerScreenConfigOption::clearTransferVisibility));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side stack"),
        screen,
        configOption,
        false,
        PerScreenConfigOption::getStackVisibility,
        PerScreenConfigOption::setStackVisibility,
        PerScreenConfigOption::clearStackVisibility));

    this.addEntry(new GroupEntry(this.client, this, Text.of("Location")));

    this.addEntry(new GroupEntry(this.client, this, Text.of("Position offset")));
    this.addEntry(new PositionEntry(this.client,
        this.parent,
        this,
        Text.literal("Player side offset"),
        screen,
        configOption,
        true,
        PerScreenConfigOption::getPosition,
        PerScreenConfigOption::setPosition,
        PerScreenConfigOption::clearPosition));
    this.addEntry(new PositionEntry(this.client,
        this.parent,
        this,
        Text.literal("Container side offset"),
        screen,
        configOption,
        false,
        PerScreenConfigOption::getPosition,
        PerScreenConfigOption::setPosition,
        PerScreenConfigOption::clearPosition));

    this.addEntry(new GroupEntry(this.client, this, Text.of("Layout")));
  }

  public abstract static class Entry extends VariableHeightListWidget.Entry<Entry> {
    protected Entry(MinecraftClient client, PerScreenConfigListWidget parent, int height) {
      super(client, parent, height);
    }
  }

  public static class GroupEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected GroupEntry(MinecraftClient client, PerScreenConfigListWidget parent, Text label) {
      super(client, parent, HEIGHT);

      this.labelWidget = LabelWidget.builder(client,
              label,
              this.getLeft() + this.getWidth() / 2,
              this.getTop() + this.getHeight() / 2)
          .justifiedCenter()
          .alignedMiddle()
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();
    }

    @Override
    public List<? extends Element> children() {
      return List.of();
    }

    @Override
    public void renderContent(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2 - (int) scrollAmount);
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);
    }
  }

  public static class ButtonVisibilityEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final Screen screen;
    protected final PerScreenConfigOption configOption;
    protected final boolean isPlayerInventory;
    protected final TriFunction<PerScreenConfigOption, Screen, Boolean, ButtonVisibility> getter;
    protected final QuadConsumer<PerScreenConfigOption, Screen, Boolean, ButtonVisibility> setter;
    protected final TriConsumer<PerScreenConfigOption, Screen, Boolean> resetter;
    protected final LabelWidget labelWidget;
    protected final CyclingButtonWidget<ButtonVisibility> valueButtonWidget;

    protected ButtonVisibilityEntry(
        MinecraftClient client,
        PerScreenConfigListWidget parent,
        Text label,
        Screen screen,
        PerScreenConfigOption configOption,
        boolean isPlayerInventory,
        TriFunction<PerScreenConfigOption, Screen, Boolean, ButtonVisibility> getter,
        QuadConsumer<PerScreenConfigOption, Screen, Boolean, ButtonVisibility> setter,
        TriConsumer<PerScreenConfigOption, Screen, Boolean> resetter) {
      super(client, parent, HEIGHT);

      this.screen = screen;
      this.configOption = configOption;
      this.isPlayerInventory = isPlayerInventory;
      this.getter = getter;
      this.setter = setter;
      this.resetter = resetter;

      this.labelWidget =
          LabelWidget.builder(client, label, this.getLeft(), this.getTop() + this.getHeight() / 2)
              .justifiedLeft()
              .alignedMiddle()
              .shiftForPadding()
              .showTextShadow()
              .hideBackground()
              .build();

      ButtonVisibility initialValue =
          this.getter.apply(this.configOption, this.screen, this.isPlayerInventory);
      this.valueButtonWidget =
          new CyclingButtonWidget.Builder<ButtonVisibility>((value) -> value.getDisplayText(
              InventoryManagementMod.CONFIG)).values(ButtonVisibility.values())
              .initially(initialValue == null ? ButtonVisibility.DEFAULT : initialValue)
              .omitKeyText()
              .build(this.getLeft() + this.getWidth() - BUTTON_WIDTH - BUTTON_HEIGHT / 2,
                  this.getTop() + (this.getHeight() - BUTTON_HEIGHT) / 2,
                  BUTTON_WIDTH,
                  BUTTON_HEIGHT,
                  Text.of(""),
                  (button, value) -> {
                    if (value == ButtonVisibility.DEFAULT) {
                      this.resetter.accept(this.configOption, this.screen, this.isPlayerInventory);
                    } else {
                      this.setter.accept(this.configOption,
                          this.screen,
                          this.isPlayerInventory,
                          value);
                    }
                    button.setMessage(value.getDisplayText(InventoryManagementMod.CONFIG));
                  });
    }

    @Override
    public List<? extends Element> children() {
      return List.of(this.valueButtonWidget);
    }

    @Override
    public void renderContent(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2 - (int) scrollAmount);
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);

      this.valueButtonWidget.setY(this.getTop() + this.getHeight() / 2 - 10 - (int) scrollAmount);
      this.valueButtonWidget.render(drawContext, mouseX, mouseY, delta);
    }
  }

  public static class PositionEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final Screen screen;
    protected final Screen workingScreen;
    protected final PerScreenConfigOption configOption;
    protected final boolean isPlayerInventory;
    protected final TriFunction<PerScreenConfigOption, Screen, Boolean, Position> getter;
    protected final QuadConsumer<PerScreenConfigOption, Screen, Boolean, Position> setter;
    protected final TriConsumer<PerScreenConfigOption, Screen, Boolean> resetter;
    protected final LabelWidget labelWidget;
    protected final ButtonWidget editButtonWidget;

    protected PositionEntry(
        MinecraftClient client,
        Screen screen,
        PerScreenConfigListWidget parent,
        Text label,
        Screen workingScreen,
        PerScreenConfigOption configOption,
        boolean isPlayerInventory,
        TriFunction<PerScreenConfigOption, Screen, Boolean, Position> getter,
        QuadConsumer<PerScreenConfigOption, Screen, Boolean, Position> setter,
        TriConsumer<PerScreenConfigOption, Screen, Boolean> resetter) {
      super(client, parent, HEIGHT);

      this.screen = screen;
      this.workingScreen = workingScreen;
      this.configOption = configOption;
      this.isPlayerInventory = isPlayerInventory;
      this.getter = getter;
      this.setter = setter;
      this.resetter = resetter;

      this.labelWidget =
          LabelWidget.builder(client, label, this.getLeft(), this.getTop() + this.getHeight() / 2)
              .justifiedLeft()
              .alignedMiddle()
              .shiftForPadding()
              .showTextShadow()
              .hideBackground()
              .build();

      Position initialValue =
          this.getter.apply(this.configOption, this.workingScreen, this.isPlayerInventory);
      this.editButtonWidget = ButtonWidget.builder(getValueText(initialValue), (button) -> {
            GuiUtil.setScreen(new PerScreenPositionEditScreen(this.screen,
                this.workingScreen,
                this.isPlayerInventory));
          })
          .position(this.getLeft() + this.getWidth() - BUTTON_WIDTH - BUTTON_HEIGHT / 2,
              this.getTop() + (this.getHeight() - BUTTON_HEIGHT) / 2)
          .size(BUTTON_WIDTH, BUTTON_HEIGHT)
          .build();
    }

    @Override
    public List<? extends Element> children() {
      return List.of(this.editButtonWidget);
    }

    @Override
    public void renderContent(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2 - (int) scrollAmount);
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);

      this.editButtonWidget.setMessage(getValueText(this.getter.apply(this.configOption,
          this.workingScreen,
          this.isPlayerInventory)));
      this.editButtonWidget.setY(this.getTop() + this.getHeight() / 2 - 10 - (int) scrollAmount);
      this.editButtonWidget.render(drawContext, mouseX, mouseY, delta);
    }

    private static Text getValueText(Position value) {
      if (value == null) {
        return Text.translatable("inventorymanagement.perscreen.value.none");
      }
      return Text.of(value.toString());
    }
  }

  @FunctionalInterface
  interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
  }

  @FunctionalInterface
  interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
  }

  @FunctionalInterface
  interface QuadConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);
  }
}
