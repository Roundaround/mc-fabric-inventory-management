package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.observable.Subscription;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AnywherePositionEditScreen extends PositionEditScreen {
  protected final Screen anywhereParent;

  public AnywherePositionEditScreen(Component title, Screen parent, PositionConfigOption configOption) {
    super(title, null, configOption);
    this.anywhereParent = parent;
    this.anywhereParent.setFocused(null);
  }

  @Override
  public void onClose() {
    this.subscriptions.forEach(Subscription::close);
    this.subscriptions.clear();
    this.minecraft.setScreen(this.anywhereParent);
  }
}
