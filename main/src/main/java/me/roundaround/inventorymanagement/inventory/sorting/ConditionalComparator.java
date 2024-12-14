package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.function.Predicate;

public abstract class ConditionalComparator<T> extends AbstractComparator<T> {
  private final Predicate<? super T> predicate;

  protected ConditionalComparator(Predicate<? super T> predicate) {
    super();
    this.predicate = predicate;
  }

  @Override
  public int compare(T o1, T o2) {
    if (!this.predicate.test(o1) || !this.predicate.test(o2)) {
      return 0;
    }
    return super.compare(o1, o2);
  }
}
