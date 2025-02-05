package me.roundaround.inventorymanagement.api.sorting;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.minecraft.util.Identifier;

import java.util.*;

public class ItemVariantRegistry {
  private static final HashMap<Identifier, ItemVariantRegistry> REGISTRIES = new HashMap<>();
  public static final ItemVariantRegistry COLOR = register("color");
  public static final ItemVariantRegistry MATERIAL = register("material");
  public static final ItemVariantRegistry SHAPE = register("shape");

  private final Identifier id;
  private final ArrayList<VariantGroup> groups = new ArrayList<>();

  private ItemVariantRegistry(Identifier id) {
    this.id = id;
  }

  public Identifier getId() {
    return this.id;
  }

  public void register(VariantGroup group) {
    this.groups.add(group);
  }

  public List<VariantGroup> list() {
    return Collections.unmodifiableList(this.groups);
  }

  private static ItemVariantRegistry register(String id) {
    return register(new Identifier(InventoryManagementMod.MOD_ID, id));
  }

  public static ItemVariantRegistry register(Identifier id) {
    return register(id, List.of());
  }

  public static ItemVariantRegistry register(Identifier id, Collection<VariantGroup> initialEntries) {
    ItemVariantRegistry registry = new ItemVariantRegistry(id);
    registry.groups.addAll(initialEntries);

    REGISTRIES.put(id, registry);

    return registry;
  }

  public static ItemVariantRegistry get(Identifier id) {
    return REGISTRIES.get(id);
  }
}
