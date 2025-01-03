package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CreativeIndexComparator implements SerialComparator<ItemStack> {
  private static CreativeIndexComparator instance;

  private final List<Comparator<ItemStack>> delegates;

  private final List<ItemGroup> itemGroups = ItemGroups.getGroups();
  private final HashMap<ItemGroup, LinkedHashSet<Item>> itemsByGroup = new HashMap<>(this.itemGroups.size());
  private final HashMap<Item, Integer> groupIndexByItem = new HashMap<>();
  private final HashMap<Item, ItemGroup> groupByItem = new HashMap<>();

  private CreativeIndexComparator() {
    this.itemGroups.forEach((group) -> {
      LinkedHashSet<Item> items = new LinkedHashSet<>();
      group.getDisplayStacks().forEach((stack) -> {
        items.add(stack.getItem());
      });
      this.itemsByGroup.put(group, items);
    });

    Registries.ITEM.forEach((item) -> {
      int index = 0;
      for (var entry : this.itemsByGroup.entrySet()) {
        if (entry.getValue().contains(item)) {
          this.groupIndexByItem.put(item, index);
          this.groupByItem.put(item, entry.getKey());
          return;
        }
        index++;
      }
      this.groupIndexByItem.put(item, null);
      this.groupByItem.put(item, null);
    });

    this.delegates = List.of(
        Comparator.comparing(this::getGroupIndexOrNull, Comparator.nullsLast(Integer::compareTo)),
        Comparator.comparing(this::getIndexInGroupOrNull, Comparator.nullsLast(Integer::compareTo))
    );
  }

  public static CreativeIndexComparator getInstance() {
    if (instance == null) {
      instance = new CreativeIndexComparator();
    }
    return instance;
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.delegates.iterator();
  }

  private Integer getGroupIndexOrNull(ItemStack stack) {
    return this.groupIndexByItem.get(stack.getItem());
  }

  private Integer getIndexInGroupOrNull(ItemStack stack) {
    return Optional.ofNullable(this.groupByItem.get(stack.getItem())).map(this.itemsByGroup::get).map((items) -> {
      int index = 0;
      for (Item item : items) {
        if (Objects.equals(item, stack.getItem())) {
          return index;
        }
        index++;
      }
      return null;
    }).orElse(null);
  }
}
