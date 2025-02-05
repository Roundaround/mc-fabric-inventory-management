package me.roundaround.inventorymanagement.api.sorting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Predicate;

public record VariantGroup(Predicate<ItemStack> predicate, VariantGroupProducer groupProducer) {
  public static VariantGroup by(Item root, TagKey<Item> tag) {
    return new VariantGroup((stack) -> stack.isIn(tag), groupUnderItem(root));
  }

  public static VariantGroup by(TagKey<Item> tag) {
    return new VariantGroup((stack) -> stack.isIn(tag), groupUnderName(tag.getTranslationKey()));
  }

  public static VariantGroup by(String root, TagKey<Item> tag) {
    return new VariantGroup((stack) -> stack.isIn(tag), groupUnderName(root));
  }

  private static VariantGroupProducer groupUnderItem(Item item) {
    return (context, stack) -> List.of(
        getTranslationKey(stack.copyComponentsToNewStack(item, stack.getCount())),
        getTranslationKey(stack.isOf(item) ? ItemStack.EMPTY : stack)
    );
  }

  private static VariantGroupProducer groupUnderName(String root) {
    return (context, stack) -> List.of(root, getTranslationKey(stack));
  }

  private static String getTranslationKey(ItemStack stack) {
    if (stack.isEmpty()) {
      return "";
    }
    return stack.getTranslationKey();
  }
}
