package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FireworkRocketComparator extends CachingComparatorImpl<ItemStack,
    FireworkRocketComparator.FireworkSummary> {
  public FireworkRocketComparator() {
    super(Comparator.naturalOrder());
  }

  @Override
  protected FireworkRocketComparator.FireworkSummary mapValue(ItemStack stack) {
    return FireworkRocketComparator.FireworkSummary.of(stack);
  }

  protected record FireworkSummary(int rocketDuration,
                                   List<FireworkExplosionComparator.ExplosionSummary> explosions) implements Comparable<FireworkSummary> {
    private static Comparator<FireworkSummary> comparator;

    public static FireworkSummary of(ItemStack stack) {
      FireworksComponent component = stack.get(DataComponentTypes.FIREWORKS);
      if (component == null) {
        return new FireworkSummary(0, List.of());
      }

      int rocketDuration = component.flightDuration();
      ArrayList<FireworkExplosionComparator.ExplosionSummary> explosions = component.explosions()
          .stream()
          .map(FireworkExplosionComparator.ExplosionSummary::of)
          .collect(Collectors.toCollection(ArrayList::new));

      return new FireworkSummary(rocketDuration, explosions);
    }

    @Override
    public int compareTo(@NotNull FireworkSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<FireworkSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(FireworkSummary::rocketDuration).reversed(),
            Comparator.comparing(FireworkSummary::explosions, LexicographicalListComparator.naturalOrder())
        );
      }
      return comparator;
    }
  }
}
