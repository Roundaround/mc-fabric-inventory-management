package me.roundaround.inventorymanagement.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.positioning.Coords;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.api.positioning.SlotPositionReference;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.client.option.KeyBindings;
import me.roundaround.inventorymanagement.event.ResourcesReloadedEvent;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.mixin.HorseScreenHandlerAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ButtonManager.init();
    //    ConfigControlRegister.init();
    KeyBindings.init();

    FabricLoader.getInstance()
        .getModContainer(InventoryManagementMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(
            new Identifier(InventoryManagementMod.MOD_ID, "inventorymanagement-dark-ui"), container,
            // TODO: i18n
            Text.literal("Inventory Management Dark UI"), ResourcePackActivationType.NORMAL
        ));

    this.initButtonRegistry();

    ResourcesReloadedEvent.EVENT.register((client) -> {
      ClientNetworking.trySendRecalculatePacket();

      // TODO: Figure out if all languages have a clean way to "trim" colors from the strings...
      LinkedHashMap<String, LinkedHashMap<String, String>> translations = new LinkedHashMap<>();

      LanguageManager languageManager = client.getLanguageManager();
      ResourceManager resourceManager = client.getResourceManager();
      languageManager.getAllLanguages().forEach((languageCode, languageDef) -> {
        ArrayList<String> languageCodes = new ArrayList<>(2);
        languageCodes.add("en_us");
        boolean rightToLeft = false;
        if (!languageCode.equals("en_us")) {
          languageCodes.add(languageCode);
          rightToLeft = languageDef.rightToLeft();
        }

        TranslationStorage translationStorage = TranslationStorage.load(resourceManager, languageCodes, rightToLeft);
        Registries.ITEM.forEach((item) -> {
          String translationKey = item.getTranslationKey();
          if (translationKey.contains("_wool")) {
            translations.computeIfAbsent(languageCode, (key) -> new LinkedHashMap<>())
                .put(translationKey, translationStorage.get(translationKey).toLowerCase());
          }
        });
      });

      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      Path msSuppLangsFile = FabricLoader.getInstance().getGameDir().resolve("microsoft_supported_langs.json");
      MicrosoftSupportedLanguages msSuppLangs;
      try {
        msSuppLangs = gson.fromJson(Files.newBufferedReader(msSuppLangsFile), MicrosoftSupportedLanguages.class);

        for (var langEntry : translations.entrySet()) {
          HashMap<String, String> langTranslations = langEntry.getValue();
          List<String> strings = List.copyOf(langTranslations.values());
          String longestCommon = findLongestCommonSubstring(strings);
          langTranslations.put("Longest Common Substring", longestCommon.trim());
          boolean supportedByMs = msSuppLangs.translation.containsKey(getLangWithoutCountry(langEntry.getKey()));
          langTranslations.put("Supported by MS", String.valueOf(supportedByMs));
        }

        Path outFile = FabricLoader.getInstance().getGameDir().resolve("language_dump_wool.json");
        try (var writer = Files.newBufferedWriter(outFile)) {
          writer.write(gson.toJson(translations));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private static String getLangWithoutCountry(String withCountry) {
    int index = withCountry.indexOf("_");
    if (index == -1) {
      return withCountry;
    }
    return withCountry.substring(0, index);
  }

  private static String findLongestCommonSubstring(List<String> strings) {
    if (strings.isEmpty()) {
      return "";
    }

    String first = strings.getFirst();
    int len = first.length();

    for (int substringLength = len; substringLength > 0; substringLength--) {
      for (int start = 0; start <= len - substringLength; start++) {
        String substring = first.substring(start, start + substringLength);

        boolean allContain = true;
        for (String str : strings) {
          if (!str.contains(substring)) {
            allContain = false;
            break;
          }
        }

        if (allContain) {
          return substring;
        }
      }
    }

    return "";
  }

  private void initButtonRegistry() {
    ButtonRegistry registry = ButtonRegistry.getInstance();

    registry.registerBothSides(ShulkerBoxScreenHandler.class);
    registry.registerBothSides(GenericContainerScreenHandler.class);
    registry.registerBothSides(Generic3x3ContainerScreenHandler.class);

    // Not all horses have an inventory
    registry.register(HorseScreenHandler.class).withPlayer().withContainer((context) -> {
      HorseScreenHandlerAccessor accessor = ((HorseScreenHandlerAccessor) context.getScreenHandler());
      return accessor.invokeHasChest(accessor.getEntity());
    });

    // Hopper's container-side is only 1 slot tall, so we need to bump the buttons up a bit to make room
    registry.registerBothSides(HopperScreenHandler.class, (context) -> {
      Coords base = PositioningFunction.refSlotYAndBgRight().apply(context);
      if (context.isPlayerInventory()) {
        return base;
      }

      int containerBot =
          base.y() + context.getButtonsTotalHeight(ButtonManager.BUTTON_HEIGHT, ButtonManager.BUTTON_SPACING);
      int playerTop =
          SlotPositionReference.top(InventoryHelper.getReferenceSlot(context.getScreen(), true)).get(context) +
          PositioningFunction.DEFAULT_SLOT_TOP_OFFSET;

      int adjustment = Math.min(0, playerTop - containerBot - GuiUtil.PADDING);
      return base.movedDown(adjustment);
    });

    registry.registerPlayerSideOnly(PlayerScreenHandler.class);
    // Furnace, smoker, blast furnace
    registry.registerPlayerSideOnly(AbstractFurnaceScreenHandler.class);
    // Anvil, smithing table
    registry.registerPlayerSideOnly(ForgingScreenHandler.class);
    registry.registerPlayerSideOnly(CraftingScreenHandler.class);
    registry.registerPlayerSideOnly(CrafterScreenHandler.class);
    registry.registerPlayerSideOnly(BrewingStandScreenHandler.class);
    registry.registerPlayerSideOnly(StonecutterScreenHandler.class);
    registry.registerPlayerSideOnly(GrindstoneScreenHandler.class);
    registry.registerPlayerSideOnly(CartographyTableScreenHandler.class);
    registry.registerPlayerSideOnly(LoomScreenHandler.class);
    registry.registerPlayerSideOnly(EnchantmentScreenHandler.class);
    registry.registerPlayerSideOnly(BeaconScreenHandler.class);
    registry.registerPlayerSideOnly(MerchantScreenHandler.class);

    // Creative screen dynamically needs to update its reference slot and thus position
    AtomicBoolean wasPreviouslyInventoryTab = new AtomicBoolean(false);
    registry.<CreativeInventoryScreen.CreativeScreenHandler, CreativeInventoryScreen>registerPlayerSideOnly(
        CreativeInventoryScreen.CreativeScreenHandler.class, (context) -> {
          boolean isInventoryTab = context.getScreen().isInventoryTabSelected();
          if (isInventoryTab != wasPreviouslyInventoryTab.get()) {
            context.setReferenceSlot(isInventoryTab ? context.getDefaultReferenceSlot() : null);
          }
          wasPreviouslyInventoryTab.set(isInventoryTab);

          if (!context.hasReferenceSlot()) {
            return null;
          }

          PositioningFunction<CreativeInventoryScreen.CreativeScreenHandler, CreativeInventoryScreen> base =
              PositioningFunction.refSlotYAndBgRight();
          return base.apply(context);
        });

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit());
  }

  private record MicrosoftSupportedLanguages(HashMap<String, Object> translation,
                                             HashMap<String, Object> transliteration,
                                             HashMap<String, Object> dictionary) {
  }
}
