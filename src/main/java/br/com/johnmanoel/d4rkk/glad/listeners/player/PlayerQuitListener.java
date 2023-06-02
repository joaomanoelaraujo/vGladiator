package br.com.johnmanoel.d4rkk.glad.listeners.player;

import br.com.johnmanoel.d4rkk.glad.cmd.gl.BuildCommand;
import br.com.johnmanoel.d4rkk.glad.cmd.gl.CreateCommand;

import br.com.johnmanoel.d4rkk.glad.tagger.TagUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    evt.setQuitMessage(null);
    BuildCommand.remove(evt.getPlayer());
    TagUtils.reset(evt.getPlayer().getName());
    CreateCommand.CREATING.remove(evt.getPlayer());

  }
}
