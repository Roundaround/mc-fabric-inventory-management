package me.roundaround.inventorymanagement.testing;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

public class BaseMinecraftTest {
  protected static final UUID PLAYER_UUID = UUID.randomUUID();

  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }
}
