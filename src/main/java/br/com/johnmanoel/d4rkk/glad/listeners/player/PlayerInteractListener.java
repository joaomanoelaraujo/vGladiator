package br.com.johnmanoel.d4rkk.glad.listeners.player;

import br.com.johnmanoel.d4rkk.glad.Language;
import br.com.johnmanoel.d4rkk.glad.cmd.gl.CreateCommand;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import br.com.johnmanoel.d4rkk.glad.menus.Connection;
import br.com.johnmanoel.d4rkk.glad.menus.MenuPlay;
import br.com.johnmanoel.d4rkk.glad.menus.MenuStatsNPC;
import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.game.GameState;
import dev.slickcollections.kiwizin.libraries.npclib.api.event.NPCLeftClickEvent;
import dev.slickcollections.kiwizin.libraries.npclib.api.event.NPCRightClickEvent;
import dev.slickcollections.kiwizin.libraries.npclib.api.npc.NPC;
import dev.slickcollections.kiwizin.menus.MenuDeliveries;
import dev.slickcollections.kiwizin.nms.NMS;
import dev.slickcollections.kiwizin.player.Profile;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {


  @EventHandler
  public void onNPCLeftClick(NPCLeftClickEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null) {
      NPC npc = evt.getNPC();
      if (npc.data().has("play-npc")) {
        new MenuPlay(profile, GladiatorMode.fromName(npc.data().get("play-npc")));
        }
      }
  }

  @EventHandler
  public void onNPCRightClick(NPCRightClickEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());
    if (profile != null) {
      NPC npc = evt.getNPC();
      if (npc.data().has("play-npc")) {
        new MenuPlay(profile, GladiatorMode.fromName(npc.data().get("play-npc")));
      } else if (npc.data().has("delivery-npc")) {
        new MenuDeliveries(profile);
      } else if (npc.data().has("stats-npc")) {
        new MenuStatsNPC(profile);
      }
    }
  }



  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(PlayerInteractEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null) {
      if (CreateCommand.CREATING.containsKey(player) && CreateCommand.CREATING.get(player)[0].equals(player.getWorld())) {
        ItemStack item = player.getItemInHand();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
          CreateCommand.handleClick(profile, item.getItemMeta().getDisplayName(), evt);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerInteractBow(final PlayerInteractEvent e) {
    final Damageable hp;
    final Player p = (Player) (hp = (Damageable) e.getPlayer());
    final ItemStack tigela = new ItemStack(Material.BOWL);
    final ItemMeta tigelam = tigela.getItemMeta();
    tigela.setItemMeta(tigelam);
    if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand().getType() == Material.MUSHROOM_SOUP && hp.getHealth() != hp.getMaxHealth()) {
      double last = hp.getHealth();
      p.setHealth((hp.getHealth() + 8.0 > hp.getMaxHealth()) ? hp.getMaxHealth() : (hp.getHealth() + 8.0));
      p.getItemInHand().setType(Material.BOWL);
      p.getItemInHand().setItemMeta(tigelam);
      NMS.sendActionBar(p, "§c +4❤");
      String result = (last - 20) / 2 + "";
      result = result.substring(0, 4);
    }
  }
}
