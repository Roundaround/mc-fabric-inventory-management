package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ItemStackComparator implements Comparator<ItemStack> {
  // TODO: Create more sorting algorithms & options:
  // Item/block categories
  // Place materials together
  // Allow registering reserve slots
  // More advanced configurations?

  private static final List<Comparator<ItemStack>> SUB_COMPARATORS =
      List.of(Comparator.comparing(ItemStackComparator::getSortName),
      ConditionalComparator.comparing(s -> s.getItem() instanceof ToolItem,
          SerialComparator.comparing(Comparator.comparingInt(ItemStackComparator::getTieredItemDamage).reversed(),
              Comparator.comparingInt(ItemStackComparator::getTieredItemSpeed).reversed()
          )
      ),
      ConditionalComparator.comparing(s -> s.getItem() instanceof ArmorItem,
          SerialComparator.comparing(Comparator.comparingInt(ItemStackComparator::getArmorSlot).reversed(),
              Comparator.comparingInt(ItemStackComparator::getArmorValue).reversed()
          )
      ),
      ConditionalComparator.comparing(s -> s.getItem() instanceof AnimalArmorItem,
          Comparator.comparingInt(ItemStackComparator::getHorseArmorValue).reversed()
      ),
      ConditionalComparator.comparing(ItemStackComparator::isPotion,
          SerialComparator.comparing(Comparator.comparing(ItemStackComparator::getPotionEffectName),
              Comparator.comparingInt(ItemStackComparator::getPotionLevel).reversed(),
              Comparator.comparingInt(ItemStackComparator::getPotionLength).reversed()
          )
      ),
      Comparator.comparingInt(ItemStackComparator::getHasNameAsInt).reversed(),
      ConditionalComparator.comparing(ItemStackComparator::hasCustomName,
          Comparator.comparing(s -> s.getName().getString().toLowerCase(Locale.ROOT))
      ),
      Comparator.comparingInt(ItemStackComparator::getIsEnchantedAsInt).reversed(),
      ConditionalComparator.comparing(ItemStackComparator::isEnchantedBookOrEnchantedItem,
          Comparator.comparing(ItemStackComparator::getEnchantmentListAsString)
      ),
      Comparator.comparingInt(ItemStackComparator::getColor),
      Comparator.comparingInt(ItemStack::getCount).reversed(),
      Comparator.comparingInt(ItemStack::getDamage),
      Comparator.comparing(s -> s.getName().getString().toLowerCase(Locale.ROOT))
  );

  private static final List<String> COMMON_SUFFIXES = List.of("log",
      "wood",
      "leaves",
      "planks",
      "sign",
      "pressure_plate",
      "button",
      "door",
      "trapdoor",
      "fence",
      "fence_gate",
      "stairs",
      "ore",
      "boat",
      "spawn_egg",
      "soup",
      "seeds",
      "banner_pattern",
      "book",
      "map",
      "golden_apple",
      "minecart",
      "rail",
      "piston",
      "coral",
      "coral_wall_fan",
      "coral_block",
      "ice"
  );
  private static final List<String> COLOR_PREFIXES = Arrays.stream(DyeColor.values())
      .map(DyeColor::getName)
      .collect(Collectors.toList());
  private static final List<Pair<String, String>> REGEX_REPLACERS = List.of(new Pair<>("^stripped_(.+?)_(log|wood)$",
          "$2_stripped_$1"
      ),
      new Pair<>("(.+?)_vertical_slab$", "slab_vertical_$1"),
      // Roundaround's Vertical Slabs
      new Pair<>("(.+?)_slab$", "slab_horizontal_$1"),
      new Pair<>("^(.*?)concrete(?!_powder)(.*)$", "$1concrete_a$2"),
      new Pair<>("^cooked_(.+)$", "$1_cooked"),
      new Pair<>(String.format("^(.+?)_(%s)$", String.join("|", COMMON_SUFFIXES)), "$2_$1"),
      new Pair<>(String.format("^(%s)_(.+)$", String.join("|", COLOR_PREFIXES)), "$2")
  );

  private final SerialComparator<ItemStack> underlyingComparator;

  private ItemStackComparator(SerialComparator<ItemStack> underlyingComparator) {
    this.underlyingComparator = underlyingComparator;
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return this.underlyingComparator.compare(o1, o2);
  }

  private static String getSortName(ItemStack itemStack) {
    Item item = itemStack.getItem();
    String itemString = item.toString();

    if (item instanceof PotionItem) {
      if (item instanceof SplashPotionItem) {
        return "potion_splash";
      } else if (item instanceof LingeringPotionItem) {
        return "potion_lingering";
      }
      return "potion";
    }

    if (item instanceof ArrowItem) {
      if (item instanceof TippedArrowItem) {
        return "arrow_tipped";
      } else if (item instanceof SpectralArrowItem) {
        return "arrow_spectral";
      }
      return "arrow";
    }

    if (item instanceof BlockItem) {
      Block block = ((BlockItem) item).getBlock();

      if (block instanceof FlowerBlock) {
        return "flower_" + itemString;
      }
    }

    for (Pair<String, String> regexReplacer : REGEX_REPLACERS) {
      itemString = itemString.replaceAll(regexReplacer.getLeft(), regexReplacer.getRight());
    }

    return itemString;
  }

  private static int getIsEnchantedAsInt(ItemStack itemStack) {
    return itemStack.hasEnchantments() ? 1 : 0;
  }

  private static boolean isEnchantedBookOrEnchantedItem(ItemStack stack) {
    return stack.get(DataComponentTypes.ENCHANTMENTS) != null ||
           stack.get(DataComponentTypes.STORED_ENCHANTMENTS) != null;
  }

  // Only call when ^isEnchantedBookOrEnchantedItem
  private static String getEnchantmentListAsString(ItemStack stack) {
    ItemEnchantmentsComponent component = Optional.ofNullable(stack.get(DataComponentTypes.ENCHANTMENTS))
        .orElseGet(() -> stack.get(DataComponentTypes.STORED_ENCHANTMENTS));
    if (component == null) {
      return "";
    }
    return component.getEnchantments()
        .stream()
        .map(RegistryEntry::value)
        .map((enchantment) -> enchantment.getName(component.getLevel(enchantment)))
        .map(Text::getString)
        .collect(Collectors.joining(" "));
  }

  // Only call when item is ToolItem.
  private static int getTieredItemDamage(ItemStack itemStack) {
    return (int) (((ToolItem) itemStack.getItem()).getMaterial().getAttackDamage() * 100f);
  }

  // Only call when item is TieredItem.
  private static int getTieredItemSpeed(ItemStack itemStack) {
    return (int) (((ToolItem) itemStack.getItem()).getMaterial().getMiningSpeedMultiplier() * 100f);
  }

  // Only call when item is ArmorItem.
  private static int getArmorSlot(ItemStack itemStack) {
    EquipmentSlot slotType = ((ArmorItem) itemStack.getItem()).getSlotType();
    int groupValue = slotType.getType() == EquipmentSlot.Type.ARMOR ? 10 : 0;
    return groupValue + slotType.getEntitySlotId();
  }

  // Only call when item is ArmorItem.
  private static int getArmorValue(ItemStack itemStack) {
    return ((ArmorItem) itemStack.getItem()).getProtection();
  }

  // Only call when item is AnimalArmorItem.
  private static int getHorseArmorValue(ItemStack itemStack) {
    return ((AnimalArmorItem) itemStack.getItem()).getProtection();
  }

  private static boolean isPotion(ItemStack stack) {
    return stack.get(DataComponentTypes.POTION_CONTENTS) != null;
  }

  private static String getPotionEffectName(ItemStack itemStack) {
    return streamPotionStatusEffects(itemStack).map(StatusEffectInstance::getEffectType)
        .map(RegistryEntry::value)
        .map(StatusEffect::getName)
        .map(Text::getString)
        .min(Comparator.naturalOrder())
        .orElse("");
  }

  private static int getPotionLevel(ItemStack itemStack) {
    return streamPotionStatusEffects(itemStack).mapToInt(StatusEffectInstance::getAmplifier).max().orElse(0);
  }

  private static int getPotionLength(ItemStack itemStack) {
    return streamPotionStatusEffects(itemStack).mapToInt(StatusEffectInstance::getDuration).max().orElse(0);
  }

  private static Stream<StatusEffectInstance> streamPotionStatusEffects(ItemStack stack) {
    return StreamSupport.stream(getPotionComponent(stack).getEffects().spliterator(), false);
  }

  private static PotionContentsComponent getPotionComponent(ItemStack stack) {
    return Optional.ofNullable(stack.get(DataComponentTypes.POTION_CONTENTS)).orElse(PotionContentsComponent.DEFAULT);
  }

  private static int getColor(ItemStack itemStack) {
    Item item = itemStack.getItem();

    DyedColorComponent component = itemStack.get(DataComponentTypes.DYED_COLOR);
    if (component != null) {
      return component.rgb();
    }

    String itemString = item.toString();

    return Arrays.stream(DyeColor.values())
        .filter(dyeColor -> itemString.startsWith(dyeColor.getName()))
        .mapToInt(DyeColor::getId)
        .map(i -> i + 1) // Colorless < all colors
        .findFirst()
        .orElse(0);
  }

  private static boolean hasCustomName(ItemStack stack) {
    return stack.get(DataComponentTypes.CUSTOM_NAME) != null;
  }

  private static int getHasNameAsInt(ItemStack stack) {
    return hasCustomName(stack) ? 1 : 0;
  }

  public static ItemStackComparator comparator() {
    return new ItemStackComparator(SerialComparator.comparing(SUB_COMPARATORS));
  }
}
