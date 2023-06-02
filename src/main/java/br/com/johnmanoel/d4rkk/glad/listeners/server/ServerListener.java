package br.com.johnmanoel.d4rkk.glad.listeners.server;

import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import dev.slickcollections.kiwizin.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ServerListener implements Listener {
  
  @EventHandler
  public void onBlockIgnite(BlockIgniteEvent evt) {
    Gladiator game = Gladiator.getByWorldName(evt.getBlock().getWorld().getName());
    if (game == null) {
      evt.setCancelled(true);
    } else if (game.getState() != GameState.EMJOGO) {
      evt.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onBlockBurn(BlockBurnEvent evt) {
    Gladiator game = Gladiator.getByWorldName(evt.getBlock().getWorld().getName());
    if (game == null) {
      evt.setCancelled(true);
    } else if (game.getState() != GameState.EMJOGO) {
      evt.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent evt) {
    evt.setCancelled(true);
  }
  
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent evt) {
    Gladiator game = Gladiator.getByWorldName(evt.getEntity().getWorld().getName());
    if (game == null) {
      evt.setCancelled(true);
    } else if (game.getState() != GameState.EMJOGO) {
      evt.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onWeatherChange(WeatherChangeEvent evt) {
    evt.setCancelled(evt.toWeatherState());
  }
}