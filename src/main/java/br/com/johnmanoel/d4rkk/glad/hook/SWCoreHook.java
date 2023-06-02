package br.com.johnmanoel.d4rkk.glad.hook;

import br.com.johnmanoel.d4rkk.glad.Language;
import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import br.com.johnmanoel.d4rkk.glad.game.object.GladiatorLeague;
import br.com.johnmanoel.d4rkk.glad.hook.hotbar.SWHotbarActionType;
import br.com.johnmanoel.d4rkk.glad.hook.protocollib.HologramAdapter;
import com.comphenix.protocol.ProtocolLibrary;
import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.game.GameState;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.player.hotbar.Hotbar;
import dev.slickcollections.kiwizin.player.hotbar.HotbarAction;
import dev.slickcollections.kiwizin.player.hotbar.HotbarActionType;
import dev.slickcollections.kiwizin.player.hotbar.HotbarButton;
import dev.slickcollections.kiwizin.player.scoreboard.KScoreboard;
import dev.slickcollections.kiwizin.player.scoreboard.scroller.ScoreboardScroller;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.utils.StringUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

public class SWCoreHook {

  public static void setupHook() {
    Core.minigame = "Gladiator";

    setupHotbars();
    new BukkitRunnable() {
      @Override
      public void run() {
        Profile.listProfiles().forEach(profile -> {
          if (profile.getScoreboard() != null) {
            profile.getScoreboard().scroll();
          }
        });
      }
    }.runTaskTimerAsynchronously(JavaGlad.getInstance(), 0, Language.scoreboards$scroller$every_tick);

    new BukkitRunnable() {
      @Override
      public void run() {
        Profile.listProfiles().forEach(profile -> {
          if (!profile.playingGame() && profile.getScoreboard() != null) {
            profile.update();
          }
        });
      }
    }.runTaskTimerAsynchronously(JavaGlad.getInstance(), 0, 20);

    ProtocolLibrary.getProtocolManager().addPacketListener(new HologramAdapter());
  }



  public static void reloadScoreboard(Profile profile) {
    if (!profile.playingGame()) {
    }
    Player player = profile.getPlayer();
    Gladiator game = profile.getGame(Gladiator.class);
    List<String> lines = game == null ?
            Language.scoreboards$lobby :
            game.getState() == GameState.AGUARDANDO ?
                    Language.scoreboards$waiting :
                    (game.getMode() == GladiatorMode.SOLO || game.getMode() == GladiatorMode.RANKED ? Language.scoreboards$ingame$solo : Language.scoreboards$ingame$solo);

    profile.setScoreboard(new KScoreboard() {
      @Override
      public void update() {

        for (int index = 0; index < Math.min(lines.size(), 15); index++) {
          String line = lines.get(index);
          if (game != null) {
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = line.replace("{map}", game.getMapName())
                    .replace("{server}", game.getGameName())
                    .replace("{next_event}", game.getEvent())
                    .replace("{players}", StringUtils.formatNumber(game.getOnline()))
                    .replace("{max_players}", StringUtils.formatNumber(game.getMaxPlayers()))
                    .replace("{time}", game.getTimer() == 46 ? Language.scoreboards$time$waiting : Language.scoreboards$time$starting.replace("{time}", StringUtils.formatNumber(game.getTimer())))
                    .replace("{date}", new SimpleDateFormat("dd/MM/YY").format(System.currentTimeMillis()));
          } else {
          }

          this.add(15 - index, line);
        }
      }


    }.scroller(new ScoreboardScroller(Language.scoreboards$scroller$titles)).to(player).build());
    if (game != null && game.getState() != GameState.AGUARDANDO) {
      profile.getScoreboard().health().updateHealth();
    }
    profile.update();
    profile.getScoreboard().scroll();
  }

  private static void setupHotbars() {
    HotbarActionType.addActionType("gladiator", new SWHotbarActionType());

    KConfig config = JavaGlad.getInstance().getConfig("hotbar");
    for (String id : new String[]{"lobby", "waiting", "spectator"}) {
      Hotbar hotbar = new Hotbar(id);

      ConfigurationSection hb = config.getSection(id);
      for (String button : hb.getKeys(false)) {
        try {
          hotbar.getButtons().add(new HotbarButton(hb.getInt(button + ".slot"), new HotbarAction(hb.getString(button + ".execute")), hb.getString(button + ".icon")));
        } catch (Exception ex) {
          JavaGlad.getInstance().getLogger().log(Level.WARNING, "Falha ao carregar o botao \"" + button + "\" da hotbar \"" + id + "\": ", ex);
        }
      }

      Hotbar.addHotbar(hotbar);
    }
  }
}
