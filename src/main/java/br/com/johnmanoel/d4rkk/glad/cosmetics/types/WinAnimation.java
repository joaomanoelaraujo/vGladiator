package br.com.johnmanoel.d4rkk.glad.cosmetics.types;


import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.cosmetics.Cosmetic;
import br.com.johnmanoel.d4rkk.glad.cosmetics.CosmeticType;
import br.com.johnmanoel.d4rkk.glad.cosmetics.container.SelectedContainer;
import br.com.johnmanoel.d4rkk.glad.cosmetics.object.AbstractExecutor;
import br.com.johnmanoel.d4rkk.glad.cosmetics.object.winanimations.FireworksExecutor;
import br.com.johnmanoel.d4rkk.glad.cosmetics.types.winanimations.Fireworks;

import dev.slickcollections.kiwizin.cash.CashManager;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.player.role.Role;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.utils.BukkitUtils;
import dev.slickcollections.kiwizin.utils.StringUtils;
import dev.slickcollections.kiwizin.utils.enums.EnumRarity;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class WinAnimation extends Cosmetic {

  private static final KConfig CONFIG = JavaGlad.getInstance().getConfig("cosmetics", "winanimations");
  private final String name;


  public WinAnimation(long id, String key, double coins, String permission, String name, String icon) {
    super(id, CosmeticType.WIN_ANIMATION, coins, permission);
    this.name = name;
    if (id != 0) {
      this.rarity = this.getRarity(key);
      this.cash = this.getCash(key);
    } else {
      this.rarity = EnumRarity.COMUM;
    }
  }

  public static void setupAnimations() {
    checkIfAbsent("fireworks");


    new Fireworks(CONFIG.getSection("fireworks"));

  }

  private static void checkIfAbsent(String key) {
    if (CONFIG.contains(key)) {
      return;
    }

    FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(JavaGlad.getInstance().getResource("winanimations.yml"), StandardCharsets.UTF_8));
    for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
      CONFIG.set(key + "." + dataKey, config.get(key + "." + dataKey));
    }
  }

  protected long getCash(String key) {
    if (!CONFIG.contains(key + ".cash")) {
      CONFIG.set(key + ".cash", getAbsentProperty("winanimations", key + ".cash"));
    }

    return CONFIG.getInt(key + ".cash");
  }

  protected EnumRarity getRarity(String key) {
    if (!CONFIG.contains(key + ".rarity")) {
      CONFIG.set(key + ".rarity", getAbsentProperty("winanimations", key + ".rarity"));
    }

    return EnumRarity.fromName(CONFIG.getString(key + ".rarity"));
  }

  public abstract AbstractExecutor execute(Player player);

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public ItemStack getIcon(Profile profile) {
    return null;
  }
}



