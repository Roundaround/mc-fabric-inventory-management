package me.roundaround.inventorymanagement.testing;

import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DataGen {
  private DataGen() {
  }

  public static ItemStack createEmpty(Item item, DataComponentType<?> componentToEmpty) {
    ItemStack stack = new ItemStack(item);
    stack.remove(componentToEmpty);
    return stack;
  }

  public static List<ItemStack> createListOfEmpty(DataComponentType<?> componentToEmpty, Item... items) {
    return Arrays.stream(items).map((item) -> createEmpty(item, componentToEmpty)).toList();
  }

  public static <T extends Iterable<ItemStack>> T nameStacks(T stacks) {
    int id = 1;
    for (ItemStack stack : stacks) {
      stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(Integer.toString(id++)));
    }
    return stacks;
  }

  public static <T> Stream<Arguments> getUniquePairs(List<T> samples) {
    return IntStream.range(0, samples.size())
        .boxed()
        .flatMap(i -> IntStream.range(i + 1, samples.size())
            .mapToObj(j -> Arguments.of(samples.get(i), samples.get(j))));
  }

  public static <T> Stream<Arguments> getAllPairs(List<T> samplesA, List<T> samplesB) {
    return samplesA.stream().flatMap((a) -> samplesB.stream().map((b) -> Arguments.of(a, b)));
  }
}
