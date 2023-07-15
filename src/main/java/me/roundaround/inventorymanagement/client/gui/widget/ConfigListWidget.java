package me.roundaround.inventorymanagement.client.gui.widget;

import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigListWidget extends VariableHeightListWidget<ConfigListWidget.Entry> {

  public ConfigListWidget(
      MinecraftClient client, int left, int top, int width, int height) {
    super(client, left, top, width, height);

    this.addEntry(new GroupEntry(this.client, this, Text.of("Enable/disable buttons")));
    this.addEntry(new GroupEntry(this.client, this, Text.of("Location")));
    this.addEntry(new GroupEntry(this.client, this, Text.of("Position offset")));
    this.addEntry(new GroupEntry(this.client, this, Text.of("Layout")));
  }

  public abstract static class Entry extends VariableHeightListWidget.Entry<Entry> {
    protected Entry(MinecraftClient client, ConfigListWidget parent, int height) {
      super(client, parent, height);
    }
  }

  public static class GroupEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected GroupEntry(MinecraftClient client, ConfigListWidget parent, Text label) {
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
}
