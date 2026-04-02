package me.roundaround.inventorymanagement.client.network;

/**
 * Bridge for sending client-to-server packets. The platform module must call
 * {@link #init(Sender)} before any buttons are pressed.
 */
public final class ClientNetworking {
  private static Sender sender;

  private ClientNetworking() {
  }

  public static void init(Sender sender) {
    ClientNetworking.sender = sender;
  }

  public static void sendStack(boolean fromPlayerInventory) {
    sender.sendStack(fromPlayerInventory);
  }

  public static void sendSort(boolean isPlayerInventory) {
    sender.sendSort(isPlayerInventory);
  }

  public static void sendTransfer(boolean fromPlayerInventory) {
    sender.sendTransfer(fromPlayerInventory);
  }

  public interface Sender {
    void sendStack(boolean fromPlayerInventory);

    void sendSort(boolean isPlayerInventory);

    void sendTransfer(boolean fromPlayerInventory);
  }
}
