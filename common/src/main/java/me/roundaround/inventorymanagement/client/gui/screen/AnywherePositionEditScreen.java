package me.roundaround.inventorymanagement.client.gui.screen;

import java.util.Objects;

import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.observable.Subscription;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AnywherePositionEditScreen extends PositionEditScreen {
  protected final Screen anywhereParent;

  public AnywherePositionEditScreen(Text title, Screen parent, PositionConfigOption configOption) {
    super(title, null, configOption);
    this.anywhereParent = parent;
    this.anywhereParent.setFocused(null);
  }

  @Override
  public void close() {
    this.subscriptions.forEach(Subscription::close);
    this.subscriptions.clear();

    Objects.requireNonNull(this.client).setScreen(this.anywhereParent);
  }
}
