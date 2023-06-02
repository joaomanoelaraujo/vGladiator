package br.com.johnmanoel.d4rkk.glad.listeners.player;

import br.com.johnmanoel.d4rkk.glad.cmd.gl.BuildCommand;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import dev.slickcollections.kiwizin.game.GameState;
import dev.slickcollections.kiwizin.player.Profile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerRestListener implements Listener {
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent evt) {
    Profile profile = Profile.getProfile(evt.getPlayer().getName());
    if (profile != null) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game == null) {
        evt.setCancelled(true);
      } else {
        evt.setCancelled(game.getState() != GameState.EMJOGO || game.isSpectator(evt.getPlayer()));
      }
    } else {
      evt.setCancelled(evt.getItemDrop().getType().equals(Material.GOLD_PLATE));
    }
  }
  
  @EventHandler
  public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
    Profile profile = Profile.getProfile(evt.getPlayer().getName());
    if (profile != null) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game == null) {
        evt.setCancelled(true);
      } else {
        evt.setCancelled(game.getState() != GameState.EMJOGO || game.isSpectator(evt.getPlayer()));
      }
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent evt) {
    Block block = evt.getBlock();
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());
    if (profile != null) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game == null) {
        evt.setCancelled(!BuildCommand.hasBuilder(player));
      } else {
        if (evt.getBlock().getType() == Material.GLASS) {
          evt.setCancelled(game.getState() != GameState.EMJOGO || game.isSpectator(evt.getPlayer()) || !game.getCubeId().contains(evt.getBlock().getLocation()));
          evt.setCancelled(true);
          return;
        }
      }
     }
    }
}
