package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackComparator implements SerialComparator<ItemStack>, AutoCloseable {
  private final List<Comparator<ItemStack>> subComparators;

  private ItemStackComparator(Collection<Comparator<ItemStack>> subComparators) {
    this.subComparators = List.copyOf(subComparators);
  }

  @SafeVarargs
  private ItemStackComparator(Comparator<ItemStack>... subComparators) {
    this(List.of(subComparators));
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  @Override
  public void close() {
    this.clearCache();
  }

  public static ItemStackComparator containersFirst(Comparator<ItemStack> andThen) {
    return new ItemStackComparator(new ContainerFirstComparator(), andThen);
  }

  public static ItemStackComparator creativeInventoryOrder(UUID player) {
    return new ItemStackComparator(
        creativeIndex(), itemName(player), itemMetadata(), viaRegistry(), containerContents());
  }

  public static ItemStackComparator alphabetical(UUID player) {
    return new ItemStackComparator(itemName(player), itemMetadata(), viaRegistry(), containerContents());
  }

  private static String getCustomName(ItemStack stack) {
    Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
    return customName == null ? null : customName.getString();
  }

  private static String getPlayerHeadName(ItemStack stack) {
    ProfileComponent profile = stack.get(DataComponentTypes.PROFILE);
    return profile == null || profile.name().isEmpty() ? null : profile.name().get();
  }

  private static int getCountOrDurability(ItemStack stack) {
    if (stack.getCount() > 1) {
      return stack.getCount();
    }
    return stack.getMaxDamage() - stack.getDamage();
  }

  private static Comparator<ItemStack> itemMetadata() {
    return SerialComparator.comparing(customName(), playerHeadName(), enchantments(), storedEnchantments(),
        paintingVariant(), bannerPattern(), fireworkAndRocket(), instrumentType(), potionEffects(),
        suspiciousStewEffects(), countOrDurability()
    );
  }

  private static Comparator<ItemStack> creativeIndex() {
    return CreativeInventoryOrderItemStackComparator.getInstance();
  }

  private static Comparator<ItemStack> itemName(UUID player) {
    return new ItemNameComparator(player);
  }

  private static Comparator<ItemStack> customName() {
    return Comparator.comparing(ItemStackComparator::getCustomName, Comparator.nullsLast(String::compareToIgnoreCase));
  }

  private static Comparator<ItemStack> playerHeadName() {
    return Comparator.comparing(
        ItemStackComparator::getPlayerHeadName, Comparator.nullsLast(String::compareToIgnoreCase));
  }

  private static Comparator<ItemStack> countOrDurability() {
    return Comparator.comparingInt(ItemStackComparator::getCountOrDurability).reversed();
  }

  private static Comparator<ItemStack> containerContents() {
    // TODO: Order based on shulker and bundle contents
    // TODO: Registry/hook for mods to hook in to sort their own custom containers
    // TODO: Pass all the other comparators down to this one internally so that it matches top level algorithm
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> enchantments() {
    return new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS);
  }

  private static Comparator<ItemStack> storedEnchantments() {
    return new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS);
  }

  private static Comparator<ItemStack> paintingVariant() {
    return new PaintingComparator();
  }

  private static Comparator<ItemStack> bannerPattern() {
    return new BannerComparator();
  }

  private static Comparator<ItemStack> fireworkAndRocket() {
    // TODO: Order based on rocket duration or firework colors/patterns
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> instrumentType() {
    // TODO: Order based on instrument type (goat horn sound)
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> potionEffects() {
    // TODO: Order based on potion type, then effects
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> suspiciousStewEffects() {
    // TODO: Order based on suspicious stew effects
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> viaRegistry() {
    // TODO: Order based on the comparator registry
    // TODO: Create a comparator registry for mods to register custom comparators based on their own data
    return Comparator.comparingInt((stack) -> 0);
  }
}
