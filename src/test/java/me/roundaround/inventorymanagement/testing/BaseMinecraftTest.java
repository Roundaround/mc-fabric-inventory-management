package me.roundaround.inventorymanagement.testing;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;

import java.util.Comparator;
import java.util.UUID;

public class BaseMinecraftTest {
  protected static final UUID PLAYER_UUID = UUID.randomUUID();
  protected static final Comparator<ItemStack> NOOP_COMPARATOR = new NoopComparator<>();

  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }
}
