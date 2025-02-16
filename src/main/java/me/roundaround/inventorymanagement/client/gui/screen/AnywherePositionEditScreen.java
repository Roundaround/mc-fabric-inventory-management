package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.util.Observable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Objects;

public class AnywherePositionEditScreen extends PositionEditScreen {
  protected final Screen anywhereParent;

  public AnywherePositionEditScreen(Text title, Screen parent, PositionConfigOption configOption) {
    super(title, null, configOption);
    this.anywhereParent = parent;
  }

  @Override
  public void close() {
    this.subscriptions.forEach(Observable.Subscription::unsubscribe);
    this.subscriptions.clear();

    Objects.requireNonNull(this.client).setScreen(this.anywhereParent);
  }
}
