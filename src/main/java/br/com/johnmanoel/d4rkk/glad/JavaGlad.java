package br.com.johnmanoel.d4rkk.glad;

import br.com.johnmanoel.d4rkk.glad.cosmetics.Cosmetic;
import br.com.johnmanoel.d4rkk.glad.lobby.*;
import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.libraries.MinecraftVersion;
import dev.slickcollections.kiwizin.plugin.KPlugin;
import br.com.johnmanoel.d4rkk.glad.cmd.Commands;

import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.object.GladiatorLeague;
import br.com.johnmanoel.d4rkk.glad.hook.SWCoreHook;
import br.com.johnmanoel.d4rkk.glad.listeners.Listeners;

import br.com.johnmanoel.d4rkk.glad.tagger.TagUtils;
import dev.slickcollections.kiwizin.utils.BukkitUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;

public class JavaGlad extends KPlugin {
  
  public static boolean kMysteryBox, kCosmetics, kClans;
  public static String currentServerName;
  private static JavaGlad instance;
  private static boolean validInit;
  
  public static JavaGlad getInstance() {
    return instance;
  }
  
  @Override
  public void start() {
    instance = this;
  }
  
  @Override
  public void load() {}
  
  @Override
  public void enable() {
    if (MinecraftVersion.getCurrentVersion().getCompareId() != 183) {
      this.setEnabled(false);
      this.getLogger().warning("O plugin apenas funciona na versao 1_8_R3 (Atual: " + MinecraftVersion.getCurrentVersion().getVersion() + ")");
      return;
    }
    
    saveDefaultConfig();
    currentServerName = getConfig().getString("lobby");
    kMysteryBox = Bukkit.getPluginManager().getPlugin("kMysteryBox") != null;
    kCosmetics = Bukkit.getPluginManager().getPlugin("kCosmetics") != null;
    kClans = Bukkit.getPluginManager().getPlugin("kClans") != null;
    if (getConfig().getString("spawn") != null) {
      Core.setLobby(BukkitUtils.deserializeLocation(getConfig().getString("spawn")));
    }
    
    Gladiator.setupGames();
    Language.setupLanguage();
    SWCoreHook.setupHook();
    Listeners.setupListeners();
    GladiatorLeague.setupLeagues();
    Commands.setupCommands();
    Leaderboard.setupLeaderboards();
    Cosmetic.setupCosmetics();
    DeliveryNPC.setupNPCs();

    PlayNPC.setupNPCs();
    StatsNPC.setupNPCs();
    Lobby.setupLobbies();

    validInit = true;
    this.getLogger().info("O plugin foi ativado.");
  }
  
  @Override
  public void disable() {
    if (validInit) {
      DeliveryNPC.listNPCs().forEach(DeliveryNPC::destroy);
      PlayNPC.listNPCs().forEach(PlayNPC::destroy);
      StatsNPC.listNPCs().forEach(StatsNPC::destroy);
      Leaderboard.listLeaderboards().forEach(Leaderboard::destroy);
      TagUtils.reset();
    }
    
    File update = new File("plugins/vGladiator/update", "vGladiator.jar");
    if (update.exists()) {
      try {
        this.getFileUtils().deleteFile(new File("plugins/" + update.getName()));
        this.getFileUtils().copyFile(new FileInputStream(update), new File("plugins/" + update.getName()));
        this.getFileUtils().deleteFile(update.getParentFile());
        this.getLogger().info("Update aplicada.");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    this.getLogger().info("O plugin foi desativado.");
  }
  
  private static class Checker {
    public static void check() {}
  }
}