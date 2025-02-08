package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemStackComparatorTest extends BaseMinecraftTest {
  private static ArrayList<Item> allItems;
  private static int maxIndex;

  @BeforeAll
  static void beforeAll() {
    allItems = Registries.ITEM.stream().collect(Collectors.toCollection(ArrayList::new));
    maxIndex = allItems.size() - 1;
  }

  @Test
  void doesNotTakeForeverForLargeInventories() {
    final int inventorySize = 300;
    final int runs = 100;
    final Duration threshold90 = Duration.ofMillis(40);
    final Duration threshold99 = Duration.ofMillis(70);
    final Duration threshold100 = Duration.ofMillis(150);
    final long thresholdAvg = 40;

    ArrayList<Duration> executionTimes = new ArrayList<>(runs);

    // Warm up pass to get i18n initialized
    ArrayList<ItemStack> warmup = genRandomLargeInventory(inventorySize);
    warmup.sort(ItemStackComparator.get(PLAYER_UUID));

    for (int i = 0; i < runs; i++) {
      ArrayList<ItemStack> actual = genRandomLargeInventory(inventorySize);
      long startTime = System.nanoTime();
      actual.sort(ItemStackComparator.get(PLAYER_UUID));
      long endTime = System.nanoTime();

      Duration duration = Duration.ofNanos(endTime - startTime);
      assertTrue(duration.compareTo(threshold100) <= 0,
          String.format("Execution %d exceeded the maximum allowed duration of %d ms (actual: %d ms)", i + 1,
              threshold100.toMillis(), duration.toMillis()
          )
      );

      executionTimes.add(duration);
    }

    long within90 = executionTimes.stream().filter((duration) -> duration.compareTo(threshold90) <= 0).count();

    assertTrue(within90 >= runs * 0.9,
        String.format("Only %d out of %d executions completed within the desired 90%% of %d ms", within90, runs,
            threshold90.toMillis()
        )
    );

    long within99 = executionTimes.stream().filter((duration) -> duration.compareTo(threshold99) <= 0).count();

    assertTrue(within99 >= runs * 0.99,
        String.format("Only %d out of %d executions completed within the desired 99%% of %d ms", within99, runs,
            threshold99.toMillis()
        )
    );

    long avg = Math.round(executionTimes.stream().mapToLong(Duration::toMillis).average().orElse(1000.0));

    assertTrue(avg <= thresholdAvg,
        String.format("Average execution too high: expected=%dms, actual=%dms", thresholdAvg, avg)
    );
  }

  private static ArrayList<ItemStack> genRandomLargeInventory(int size) {
    ArrayList<ItemStack> inventory = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      inventory.add(genStack(randItem(allItems, maxIndex)));
    }
    return inventory;
  }

  private static Item randItem(ArrayList<Item> allItems, int maxIndex) {
    return allItems.get(randInt(0, maxIndex));
  }

  private static ItemStack genStack(Item item) {
    int count = randInt(1, item.getMaxCount());
    return new ItemStack(item, count);
  }

  private static int randInt(int min, int max) {
    assert max >= min;
    return min + (int) Math.round(Math.random() * (max - min));
  }
}
