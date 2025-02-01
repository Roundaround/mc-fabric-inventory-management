package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FireworkAndRocketComparator extends CachingComparatorImpl<ItemStack,
    FireworkAndRocketComparator.FireworkSummary> {
  public FireworkAndRocketComparator() {
    super(Comparator.naturalOrder());
  }

  @Override
  protected FireworkAndRocketComparator.FireworkSummary mapValue(ItemStack stack) {
    return FireworkAndRocketComparator.FireworkSummary.of(stack);
  }

  protected record FireworkSummary(int rocketDuration,
                                   List<ExplosionSummary> explosions) implements Comparable<FireworkSummary> {
    private static Comparator<FireworkSummary> comparator;

    public static FireworkSummary of(ItemStack stack) {
      FireworksComponent component = stack.get(DataComponentTypes.FIREWORKS);
      if (component == null) {
        FireworkExplosionComponent explosionComponent = stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
        if (explosionComponent == null) {
          return new FireworkSummary(0, List.of());
        }
        return of(explosionComponent);
      }
      return of(component);
    }

    private static FireworkSummary of(FireworksComponent component) {
      int rocketDuration = component.flightDuration();
      ArrayList<ExplosionSummary> explosions = component.explosions()
          .stream()
          .map(ExplosionSummary::of)
          .collect(Collectors.toCollection(ArrayList::new));

      return new FireworkSummary(rocketDuration, explosions);
    }

    private static FireworkSummary of(FireworkExplosionComponent component) {
      return new FireworkSummary(0, List.of(ExplosionSummary.of(component)));
    }

    @Override
    public int compareTo(@NotNull FireworkSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<FireworkSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(Comparator.comparingInt(FireworkSummary::rocketDuration),
            Comparator.comparing(FireworkSummary::explosions, LexicographicalListComparator.naturalOrder())
        );
      }
      return comparator;
    }
  }

  protected record ExplosionSummary(int index, int effects,
                                    List<Integer> colors) implements Comparable<ExplosionSummary> {
    private static Comparator<ExplosionSummary> comparator;

    public static ExplosionSummary of(FireworkExplosionComponent component) {
      int index = component.shape().getId();
      ArrayList<Integer> colors = List.copyOf(component.colors())
          .stream()
          .sorted(Comparator.comparingInt((color) -> color))
          .collect(Collectors.toCollection(ArrayList::new));

      int effects = 0;
      if (component.hasTrail()) {
        effects += 1;
      }
      if (component.hasTwinkle()) {
        effects += 2;
      }

      return new ExplosionSummary(index, effects, colors);
    }

    @Override
    public int compareTo(@NotNull FireworkAndRocketComparator.ExplosionSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<ExplosionSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(Comparator.comparingInt(ExplosionSummary::index),
            Comparator.comparingInt(ExplosionSummary::effects),
            Comparator.comparing(ExplosionSummary::colors, LexicographicalListComparator.naturalOrder())
        );
      }
      return comparator;
    }
  }
}
