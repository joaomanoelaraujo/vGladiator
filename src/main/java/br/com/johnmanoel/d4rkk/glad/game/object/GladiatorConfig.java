package br.com.johnmanoel.d4rkk.glad.game.object;

import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.GladiatorTeam;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.utils.CubeID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static br.com.johnmanoel.d4rkk.glad.utils.VoidChunkGenerator.VOID_CHUNK_GENERATOR;

public class GladiatorConfig {
  
  private Gladiator game;
  private KConfig config;
  
  private String yaml;
  private World world;
  private String name;
  private GladiatorMode mode;
  private List<GladiatorTeam> teams;
  private CubeID cubeId;
  private final int minPlayers;
  
  public GladiatorConfig(Gladiator game) {
    this.game = game;
    this.yaml = game.getGameName();
    this.config = JavaGlad.getInstance().getConfig("arenas", this.yaml);
    this.name = this.config.getString("name");
    this.mode = GladiatorMode.fromName(this.config.getString("mode"));
    this.minPlayers = config.getInt("minPlayers");
    this.cubeId = new CubeID(config.getString("cubeId"));
    this.teams = new ArrayList<>();
    this.reload(null);
  }
  
  public void setupSpawns() {
    this.config.getStringList("spawns").forEach(spawn -> this.teams.add(new GladiatorTeam(this.game, spawn, this.mode.getSize())));
  }
  
  public void destroy() {
    if ((this.world = Bukkit.getWorld(this.yaml)) != null) {
      Bukkit.unloadWorld(this.world, false);
    }
    
    JavaGlad.getInstance().getFileUtils().deleteFile(new File(this.yaml));
    this.game = null;
    this.yaml = null;
    this.name = null;
    this.mode = null;
    this.teams.clear();
    this.teams = null;
    this.cubeId = null;
    this.world = null;
    this.config = null;
  }
  
  public void reload(final Runnable async) {
    File file = new File("plugins/vGladiator/mundos/" + this.yaml);
    if (Bukkit.getWorld(file.getName()) != null) {
      Bukkit.unloadWorld(file.getName(), false);
    }
    
    Runnable delete = () -> {
      JavaGlad.getInstance().getFileUtils().deleteFile(new File(file.getName()));
      JavaGlad.getInstance().getFileUtils().copyFiles(file, new File(file.getName()));
      
      Runnable newWorld = () -> {
        WorldCreator wc = WorldCreator.name(file.getName());
        wc.generator(VOID_CHUNK_GENERATOR);
        wc.generateStructures(false);
        this.world = wc.createWorld();
        this.world.setTime(0L);
        this.world.setStorm(false);
        this.world.setThundering(false);
        this.world.setAutoSave(false);
        this.world.setAnimalSpawnLimit(0);
        this.world.setWaterAnimalSpawnLimit(0);
        this.world.setKeepSpawnInMemory(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("mobGriefing", "false");
        this.world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        if (async != null) {
          async.run();
        }
      };
      
      if (async == null) {
        newWorld.run();
        return;
      }
      
      Bukkit.getScheduler().runTask(JavaGlad.getInstance(), newWorld);
    };
    
    if (async == null) {
      delete.run();
      return;
    }
    
    Bukkit.getScheduler().runTaskAsynchronously(JavaGlad.getInstance(), delete);
  }
  

  

  
  public World getWorld() {
    return this.world;
  }
  
  public KConfig getConfig() {
    return this.config;
  }
  
  public String getMapName() {
    return this.name;
  }
  
  public GladiatorMode getMode() {
    return this.mode;
  }
  
  public List<GladiatorTeam> listTeams() {
    return this.teams;
  }

  public CubeID getCubeId() {
    return this.cubeId;
  }
  
  public int getMinPlayers() {
    return minPlayers;
  }
}
