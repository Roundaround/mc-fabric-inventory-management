package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SerialComparator<T> implements Comparator<T> {
  private final List<Comparator<T>> subComparators;

  protected SerialComparator(Collection<Comparator<T>> subComparators) {
    this.subComparators = List.copyOf(subComparators);
  }

  @SafeVarargs
  protected SerialComparator(Comparator<T>... subComparators) {
    this.subComparators = List.of(subComparators);
  }

  @Override
  public int compare(T o1, T o2) {
    for (Comparator<T> comparator : this.subComparators) {
      int result = comparator.compare(o1, o2);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  @SafeVarargs
  public static <T> SerialComparator<T> comparing(
      Comparator<T>... baseComparators
  ) {
    return comparing(List.of(baseComparators));
  }

  public static <T> SerialComparator<T> comparing(
      Collection<Comparator<T>> baseComparators
  ) {
    return new SerialComparator<>(baseComparators);
  }
}