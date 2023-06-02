package br.com.johnmanoel.d4rkk.glad.menus;

import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.libraries.menu.PlayerMenu;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.utils.BukkitUtils;
import dev.slickcollections.kiwizin.utils.StringUtils;
import dev.slickcollections.kiwizin.utils.enums.EnumSound;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MenuStatsNPC extends PlayerMenu {
  
  public MenuStatsNPC(Profile profile) {
    super(profile.getPlayer(), "Estatísticas - Sky Wars", 5);
    
    long kills = profile.getStats("kCoreGladiator", "1v1kills", "2v2kills", "rankedkills") == 0
        ? 1 : profile.getStats("kCoreGladiator", "1v1kills", "2v2kills", "rankedkills"), deaths =
        profile.getStats("kCoreGladiator", "1v1deaths", "2v2deaths", "rankeddeaths") == 0 ? 1 :
            profile.getStats("kCoreGladiator", "1v1deaths", "2v2deaths", "rankeddeaths"), skills =
        profile.getStats("kCoreGladiator", "1v1kills"), sdeaths = profile.getStats("kCoreGladiator", "1v1deaths"),
        tkills = profile.getStats("kCoreGladiator", "2v2kills"), rkills = profile.getStats("kCoreGladiator", "rankedkills"),
        rdeaths = profile.getStats("kCoreGladiator", "rankeddeaths"), tdeaths = profile.getStats("kCoreGladiator", "2v2deaths");
    
    this.setItem(4, BukkitUtils.deserializeItemStack(PlaceholderAPI.setPlaceholders(this.player,
        "PAPER : 1 : nome>&aTodos os Modos : desc>&fAbates: &7%kCore_Gladiator_kills%\n&fMortes: &7%kCore_Gladiator_deaths%\n&fVitórias: &7%kCore_Gladiator_wins%\n&fPartidas: &7%kCore_Gladiator_games%\n&fAssistências: &7%kCore_Gladiator_assists%\n \n&fKDR: &7" + StringUtils.formatNumber(kills / deaths) + "\n \n&fCoins: &6%kCore_Gladiator_coins%")));

    this.setItem(40, BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cFechar"));
    
    this.register(Core.getInstance());
    this.open();
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getInventory())) {
      evt.setCancelled(true);
      
      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }
        
        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getInventory())) {
          ItemStack item = evt.getCurrentItem();
          
          if (item != null && item.getType() != Material.AIR) {
            if (evt.getSlot() == 40) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              this.player.closeInventory();
            }
          }
        }
      }
    }
  }
  
  public void cancel() {
    HandlerList.unregisterAll(this);
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(this.player)) {
      this.cancel();
    }
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}
