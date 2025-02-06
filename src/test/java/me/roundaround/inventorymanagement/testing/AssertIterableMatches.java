package me.roundaround.inventorymanagement.testing;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

public class AssertIterableMatches {
  private AssertIterableMatches() {
  }

  public static <T> void assertIterableMatches(
      Iterable<T> expected, Iterable<T> actual
  ) {
    assertIterableMatches(expected, actual, Objects::equals, Object::toString);
  }

  public static <T> void assertIterableMatches(
      Iterable<T> expected, Iterable<T> actual, BiPredicate<T, T> predicate, Function<T, String> toString
  ) {
    assertIterableMatches(expected, actual, predicate, Function.identity(), Function.identity(), toString, toString);
  }

  public static <E, A, T> void assertIterableMatches(
      Iterable<E> expected, Iterable<A> actual, Function<E, T> expectedSelector, Function<A, T> actualSelector
  ) {
    assertIterableMatches(expected, actual, expectedSelector, actualSelector, Object::toString, Object::toString);
  }

  public static <E, A, T> void assertIterableMatches(
      Iterable<E> expected,
      Iterable<A> actual,
      Function<E, T> expectedSelector,
      Function<A, T> actualSelector,
      Function<E, String> expectedToString,
      Function<A, String> actualToString
  ) {
    assertIterableMatches(
        expected, actual, Objects::equals, expectedSelector, actualSelector, expectedToString, actualToString);
  }

  public static <E, A, T> void assertIterableMatches(
      Iterable<E> expected,
      Iterable<A> actual,
      BiPredicate<T, T> predicate,
      Function<E, T> expectedSelector,
      Function<A, T> actualSelector,
      Function<E, String> expectedToString,
      Function<A, String> actualToString
  ) {
    Iterator<E> expectedIterator = expected.iterator();
    Iterator<A> actualIterator = actual.iterator();

    int index = 0;
    while (expectedIterator.hasNext() && actualIterator.hasNext()) {
      E expectedElement = expectedIterator.next();
      A actualElement = actualIterator.next();

      T expectedSelected = expectedSelector.apply(expectedElement);
      T actualSelected = actualSelector.apply(actualElement);

      if (!predicate.test(expectedSelected, actualSelected)) {
        fail(String.format(
            "Elements at index %d are not equal according to the provided predicate: expected=%s, actual=%s", index,
            expectedToString.apply(expectedElement), actualToString.apply(actualElement)
        ));
      }
      index++;
    }

    if (expectedIterator.hasNext() || actualIterator.hasNext()) {
      fail("Iterables are of different lengths.");
    }
  }

  public static <T, U> List<U> select(List<T> source, Function<T, U> map) {
    return source.stream().map(map).toList();
  }

  public static List<String> selectNames(List<ItemStack> source) {
    return select(source, (stack) -> Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_NAME)).getString());
  }
}
