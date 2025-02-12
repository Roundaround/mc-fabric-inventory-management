package me.roundaround.inventorymanagement.inventory;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class SlotRangeTest {
  @Test
  void projectWhenPerfectMatch() {
    SlotRange slotRange = SlotRange.bySize(0, 3);
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of(1, 2, 3);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenValuesSmaller() {
    SlotRange slotRange = SlotRange.bySize(0, 5);
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of(1, 2, 3, -1, -1);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenValuesLarger() {
    SlotRange slotRange = SlotRange.bySize(0, 3);
    List<Integer> values = List.of(1, 2, 3, 4, 5);

    List<Integer> expected = List.of(1, 2, 3);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenMinNonZero() {
    SlotRange slotRange = SlotRange.bySize(1, 5);
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of(-1, 1, 2, 3, -1, -1);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenMinNonZeroAndValuesLarger() {
    SlotRange slotRange = SlotRange.bySize(1, 3);
    List<Integer> values = List.of(1, 2, 3, 4, 5);

    List<Integer> expected = List.of(-1, 1, 2, 3);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenRangeIsEmpty() {
    SlotRange slotRange = SlotRange.empty();
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of();
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenRangeHasExclusions() {
    SlotRange slotRange = SlotRange.bySize(0, 5).withExclusions(List.of(2));
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of(1, 2, -1, 3, -1);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }

  @Test
  void projectWhenEntireRangeIsExcluded() {
    SlotRange slotRange = SlotRange.bySize(0, 3).withExclusions(List.of(0, 1, 2));
    List<Integer> values = List.of(1, 2, 3);

    List<Integer> expected = List.of(-1, -1, -1);
    assertIterableEquals(expected, slotRange.project(values, -1));
  }
}
