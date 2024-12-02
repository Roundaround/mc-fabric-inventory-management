package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Comparator;

public abstract class AbstractComparator<T> implements Comparator<T> {
  private final Comparator<T> base;

  protected AbstractComparator() {
    this.base = this.init();
  }

  @Override
  public int compare(T o1, T o2) {
    return this.base.compare(o1, o2);
  }

  protected abstract Comparator<T> init();
}
