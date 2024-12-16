package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Comparator;
import java.util.Iterator;

public class IntListComparator implements Comparator<Iterable<Integer>> {
  @Override
  public int compare(Iterable<Integer> o1, Iterable<Integer> o2) {
    Iterator<Integer> iter1 = o1.iterator();
    Iterator<Integer> iter2 = o2.iterator();

    while (iter1.hasNext() && iter2.hasNext()) {
      int comparison = Integer.compare(iter1.next(), iter2.next());
      if (comparison != 0) {
        return comparison;
      }
    }

    if (iter1.hasNext()) {
      return 1; // `o1` is longer, so it comes after `o2`
    } else if (iter2.hasNext()) {
      return -1; // `o2` is longer, so it comes after `o1`
    }

    return 0;
  }
}
