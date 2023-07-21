package me.roundaround.inventorymanagement.client.gui.widget;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PerScreenConfigListWidget
    extends VariableHeightListWidget<PerScreenConfigListWidget.Entry> {
  private final PerScreenConfigOption configOption;
  private final Screen screen;

  public PerScreenConfigListWidget(
      MinecraftClient client,
      int left,
      int top,
      int width,
      int height,
      PerScreenConfigOption configOption,
      Screen screen) {
    super(client, left, top, width, height);

    this.configOption = configOption;
    this.screen = screen;

    this.addEntry(new GroupEntry(this.client, this, Text.of("Enable/disable buttons")));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side sort"),
        () -> this.configOption.getPlayerSideSortVisibility(this.screen),
        (value) -> this.configOption.setPlayerSideSortVisibility(this.screen, value),
        () -> this.configOption.clearPlayerSideSortVisibility(this.screen)));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side transfer"),
        () -> this.configOption.getPlayerSideTransferVisibility(this.screen),
        (value) -> this.configOption.setPlayerSideTransferVisibility(this.screen, value),
        () -> this.configOption.clearPlayerSideTransferVisibility(this.screen)));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Player side stack"),
        () -> this.configOption.getPlayerSideStackVisibility(this.screen),
        (value) -> this.configOption.setPlayerSideStackVisibility(this.screen, value),
        () -> this.configOption.clearPlayerSideStackVisibility(this.screen)));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side sort"),
        () -> this.configOption.getContainerSideSortVisibility(this.screen),
        (value) -> this.configOption.setContainerSideSortVisibility(this.screen, value),
        () -> this.configOption.clearContainerSideSortVisibility(this.screen)));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side transfer"),
        () -> this.configOption.getContainerSideTransferVisibility(this.screen),
        (value) -> this.configOption.setContainerSideTransferVisibility(this.screen, value),
        () -> this.configOption.clearContainerSideTransferVisibility(this.screen)));
    this.addEntry(new ButtonVisibilityEntry(this.client,
        this,
        Text.literal("Container side stack"),
        () -> this.configOption.getContainerSideStackVisibility(this.screen),
        (value) -> this.configOption.setContainerSideStackVisibility(this.screen, value),
        () -> this.configOption.clearContainerSideStackVisibility(this.screen)));

    this.addEntry(new GroupEntry(this.client, this, Text.of("Location")));
    this.addEntry(new GroupEntry(this.client, this, Text.of("Position offset")));
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

    protected final Supplier<ButtonVisibility> getter;
    protected final Consumer<ButtonVisibility> setter;
    protected final Runnable resetter;
    protected final LabelWidget labelWidget;
    protected final CyclingButtonWidget<ButtonVisibility> valueButtonWidget;

    protected ButtonVisibilityEntry(
        MinecraftClient client,
        PerScreenConfigListWidget parent,
        Text label,
        Supplier<ButtonVisibility> getter,
        Consumer<ButtonVisibility> setter,
        Runnable resetter) {
      super(client, parent, HEIGHT);

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

      ButtonVisibility initialValue = this.getter.get();
      this.valueButtonWidget =
          new CyclingButtonWidget.Builder<ButtonVisibility>((value) -> value.getDisplayText(
              InventoryManagementMod.CONFIG)).values(ButtonVisibility.values())
              .initially(initialValue == null ? ButtonVisibility.DEFAULT : initialValue)
              .omitKeyText()
              .build(this.getLeft() + this.getWidth() - 200 - 10,
                  this.getTop() + this.getHeight() / 2 - 10,
                  200,
                  20,
                  Text.of(""),
                  (button, value) -> {
                    if (value == ButtonVisibility.DEFAULT) {
                      this.resetter.run();
                    } else {
                      this.setter.accept(value);
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
}
