package br.com.johnmanoel.d4rkk.glad.cosmetics;


import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.cosmetics.container.CosmeticsContainer;
import br.com.johnmanoel.d4rkk.glad.cosmetics.container.SelectedContainer;
import br.com.johnmanoel.d4rkk.glad.cosmetics.types.WinAnimation;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.utils.enums.EnumRarity;
import jdk.nashorn.internal.runtime.linker.JavaAdapterFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class Cosmetic {

  private static final List<Cosmetic> COSMETICS = new ArrayList<>();
  protected long cash;
  protected EnumRarity rarity;
  private final long id;
  private final double coins;
  private final String permission;
  private final CosmeticType type;

  public Cosmetic(long id, CosmeticType type, double coins, String permission) {
    this.id = id;
    this.coins = coins;
    this.permission = permission;
    this.type = type;
    COSMETICS.add(this);
  }

  public static void setupCosmetics() {
    WinAnimation.setupAnimations();
  }

  public static void removeCosmetic(Cosmetic cosmetic) {
    COSMETICS.remove(cosmetic);
  }

  public static <T extends Cosmetic> T findById(Class<T> cosmeticClass, long id) {
    return COSMETICS.stream()
            .filter(cosmetic -> (cosmetic.getClass().isAssignableFrom(cosmeticClass) || cosmetic.getClass().getSuperclass().equals(cosmeticClass)) && cosmetic.getId() == id)
            .map(cosmetic -> (T) cosmetic).findFirst().orElse(null);
  }

  public static Cosmetic findById(String lootChestID) {
    return COSMETICS.stream().filter(cosmetic -> cosmetic.getLootChestsID().equals(lootChestID)).findFirst().orElse(null);
  }

  public static List<Cosmetic> listCosmetics() {
    return COSMETICS;
  }

  public static <T extends Cosmetic> List<T> listByType(Class<T> cosmeticClass) {
    return COSMETICS.stream().filter(cosmetic -> cosmetic.getClass().isAssignableFrom(cosmeticClass) || cosmetic.getClass().getSuperclass().equals(cosmeticClass))
            .sorted(Comparator.comparingLong(Cosmetic::getId)).map(cosmetic -> (T) cosmetic).collect(Collectors.toList());
  }

  protected static Object getAbsentProperty(String file, String property) {
    InputStream stream = JavaGlad.getInstance().getResource(file + ".yml");
    if (stream == null) {
      return null;
    }

    return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)).get(property);
  }

  public void give(Profile profile) {
    profile.getAbstractContainer("kCoreGladiator", "cosmetics", CosmeticsContainer.class).addCosmetic(this);
  }

  public boolean has(Profile profile) {
    return profile.getPlayer().hasPermission("vskywars.cosmetics.unlockall") || profile.getAbstractContainer("kCoreGladiator", "cosmetics", CosmeticsContainer.class).hasCosmetic(this);
  }

  public boolean isSelected(Profile profile) {
    return profile.getAbstractContainer("kCoreGladiator", "selected", SelectedContainer.class).isSelected(this);
  }

  public long getId() {
    return this.id;
  }

  public String getLootChestsID() {
    return "sw" + this.type.ordinal() + "-" + this.id;
  }

  public long getIndex() {
    return 1;
  }

  public EnumRarity getRarity() {
    return this.rarity;
  }

  public double getCoins() {
    return this.coins;
  }

  public long getCash() {
    return this.cash;
  }

  public String getPermission() {
    return this.permission;
  }

  public CosmeticType getType() {
    return this.type;
  }

  public boolean canBuy(Player player) {
    return this.permission.isEmpty() || player.hasPermission(this.permission);
  }

  public abstract String getName();

  public abstract ItemStack getIcon(Profile profile);
}
