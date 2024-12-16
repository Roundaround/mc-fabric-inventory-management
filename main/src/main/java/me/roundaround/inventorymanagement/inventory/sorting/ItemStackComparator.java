package me.roundaround.inventorymanagement.inventory.sorting;

import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ContainerFirstComparator;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.CreativeInventoryOrderItemStackComparator;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.EnchantmentComparator;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ItemStackComparator extends SerialComparator<ItemStack> {
  private static Comparator<ItemStack> cachedItemMetadataComparator;

  private ItemStackComparator(Collection<Comparator<ItemStack>> subComparators) {
    super(subComparators);
  }

  @SafeVarargs
  private ItemStackComparator(Comparator<ItemStack>... subComparators) {
    this(List.of(subComparators));
  }

  public static ItemStackComparator containersFirst(Comparator<ItemStack> andThen) {
    return new ItemStackComparator(containersFirst(), andThen);
  }

  public static ItemStackComparator creativeInventoryOrder(UUID player) {
    return new ItemStackComparator(creativeIndex(), itemName(player), itemMetadata(), viaRegistry());
  }

  public static ItemStackComparator alphabetical(UUID player) {
    return new ItemStackComparator(itemName(player), itemMetadata(), viaRegistry());
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
    if (cachedItemMetadataComparator == null) {
      cachedItemMetadataComparator = SerialComparator.comparing(customName(), playerHeadName(), containerContents(),
          enchantments(), storedEnchantments(), paintingInfo(), bannerPattern(), fireworkAndRocket(), instrumentType(),
          potionEffects(), suspiciousStewEffects(), countOrDurability()
      );
    }
    return cachedItemMetadataComparator;
  }

  private static Comparator<ItemStack> containersFirst() {
    return new ContainerFirstComparator();
  }

  private static Comparator<ItemStack> creativeIndex() {
    return CreativeInventoryOrderItemStackComparator.getInstance();
  }

  private static Comparator<ItemStack> itemName(UUID player) {
    ServerI18nTracker.Snapshot i18nSnapshot = ServerI18nTracker.getInstance(player).snapshot();
    return Comparator.comparing(i18nSnapshot::get, String::compareToIgnoreCase);
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
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> enchantments() {
    return new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS);
  }

  private static Comparator<ItemStack> storedEnchantments() {
    return new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS);
  }

  private static Comparator<ItemStack> paintingInfo() {
    // TODO: Order based on painting artist & name
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> bannerPattern() {
    // TODO: Order based on banner pattern
    return Comparator.comparingInt((stack) -> 0);
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
