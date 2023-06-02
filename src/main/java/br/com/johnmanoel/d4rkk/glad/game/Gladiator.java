package br.com.johnmanoel.d4rkk.glad.game;

import br.com.johnmanoel.d4rkk.glad.Language;
import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.game.api.GDEvent;
import br.com.johnmanoel.d4rkk.glad.game.api.event.game.GDGameStartEvent;
import br.com.johnmanoel.d4rkk.glad.game.api.event.player.GDPlayerDeathEvent;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import dev.slickcollections.kiwizin.Manager;
import dev.slickcollections.kiwizin.bukkit.BukkitParty;
import dev.slickcollections.kiwizin.bukkit.BukkitPartyManager;

import dev.slickcollections.kiwizin.game.FakeGame;
import dev.slickcollections.kiwizin.game.Game;
import dev.slickcollections.kiwizin.game.GameState;
import dev.slickcollections.kiwizin.game.GameTeam;
import dev.slickcollections.kiwizin.nms.NMS;
import dev.slickcollections.kiwizin.party.PartyPlayer;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.player.hotbar.Hotbar;
import dev.slickcollections.kiwizin.player.role.Role;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.plugin.logger.KLogger;
import br.com.johnmanoel.d4rkk.glad.game.events.AnnounceEvent;
import br.com.johnmanoel.d4rkk.glad.game.interfaces.LoadCallback;
import br.com.johnmanoel.d4rkk.glad.game.object.GladiatorBlock;

import br.com.johnmanoel.d4rkk.glad.game.object.GladiatorConfig;
import br.com.johnmanoel.d4rkk.glad.game.object.GladiatorTask;
import br.com.johnmanoel.d4rkk.glad.tagger.TagUtils;
import dev.slickcollections.kiwizin.utils.BukkitUtils;
import dev.slickcollections.kiwizin.utils.CubeID;
import dev.slickcollections.kiwizin.utils.StringUtils;
import dev.slickcollections.kiwizin.utils.enums.EnumSound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static br.com.johnmanoel.d4rkk.glad.hook.SWCoreHook.reloadScoreboard;

public abstract class Gladiator implements Game<GladiatorTeam> {

  public static final KLogger LOGGER = ((KLogger) JavaGlad.getInstance().getLogger()).getModule("GAME");
  public static final List<Gladiator> QUEUE = new ArrayList<>();
  private static final SimpleDateFormat SDF = new SimpleDateFormat("mm:ss");
  private static final Map<String, Gladiator> GAMES = new HashMap<>();
  private String name;
  private GladiatorConfig config;
  private int timer;
  private GameState state;
  private GladiatorTask task;

  protected List<Block> placedBlocks;
  private List<UUID> players;
  private List<UUID> spectators;
  private Map<String, Integer> points;
  private Map<String, Integer> kills;
  private final Map<String, GladiatorBlock> blocks = new HashMap<>();
  private List<Map.Entry<String, Integer>> topKills = new ArrayList<>();
  private final Map<String, Object[]> streak = new HashMap<>();
  private Map.Entry<Integer, GladiatorEvent> event;
  private Map.Entry<Integer, GladiatorEvent> nextEvent;


  public Gladiator(String name, LoadCallback callback) {
    this.name = name;
    this.timer = Language.options$start$waiting + 1;
    this.task = new GladiatorTask(this);
    this.config = new GladiatorConfig(this);
    this.config.setupSpawns();
    this.state = GameState.AGUARDANDO;
    this.players = new ArrayList<>();
    this.spectators = new ArrayList<>();
    this.kills = new HashMap<>();
    this.task.reset();
    this.placedBlocks = new ArrayList<>();

    if (!Language.options$regen$world_reload) {
      KConfig config = JavaGlad.getInstance().getConfig("blocos", name);
      if (config.contains("dataBlocks")) {
        for (String blockdata : config.getStringList("dataBlocks")) {
          blocks.put(blockdata.split(" : ")[0],
                  new GladiatorBlock(Material.matchMaterial(blockdata.split(" : ")[1].split(", ")[0]), Byte.parseByte(blockdata.split(" : ")[1].split(", ")[1])));
        }
      } else {
        this.state = GameState.ENCERRADO;
        ArenaRollbackerTask.scan(this, config, callback);
      }
    } else if (callback != null) {
      callback.finish();
    }
  }

  public static void addToQueue(Gladiator game) {
    if (QUEUE.contains(game)) {
      return;
    }

    QUEUE.add(game);
  }

  public static void setupGames() {
    GladiatorEvent.setupEvents();
    new ArenaRollbackerTask().runTaskTimer(JavaGlad.getInstance(), 0, Language.options$regen$world_reload ? 100 : 1);

    File ymlFolder = new File("plugins/vGladiator/arenas");
    File mapFolder = new File("plugins/vGladiator/mundos");

    if (!ymlFolder.exists() || !mapFolder.exists()) {
      if (!ymlFolder.exists()) {
        ymlFolder.mkdirs();
      }
      if (!mapFolder.exists()) {
        mapFolder.mkdirs();
      }
    }

    for (File file : ymlFolder.listFiles()) {
      load(file, null);
    }

    LOGGER.info(GAMES.size() + " salas carregadas.");
  }

  public static void load(File yamlFile, LoadCallback callback) {
    String arenaName = yamlFile.getName().split("\\.")[0];

    try {
      File backup = new File("plugins/vGladiator/mundos", arenaName);
      if (!backup.exists() || !backup.isDirectory()) {
        throw new IllegalArgumentException("Backup do mapa nao encontrado para a arena \"" + yamlFile.getName() + "\"");
      }

      GladiatorMode mode = GladiatorMode.fromName(JavaGlad.getInstance().getConfig("arenas", arenaName).getString("mode"));
      if (mode == null) {
        throw new IllegalArgumentException("Modo do mapa \"" + yamlFile.getName() + "\" nao e valido");
      }

      GAMES.put(arenaName, mode.buildGame(arenaName, callback));
    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, "load(\"" + yamlFile.getName() + "\"): ", ex);
    }
  }

  public static Gladiator getByWorldName(String worldName) {
    return GAMES.get(worldName);
  }

  public static int getWaiting(GladiatorMode mode) {
    int waiting = 0;
    List<Gladiator> games = listByMode(mode);
    for (Gladiator game : games) {
      if (game.getState() != GameState.EMJOGO) {
        waiting += game.getOnline();
      }
    }

    return waiting;
  }

  public static int getPlaying(GladiatorMode mode) {
    int playing = 0;
    List<Gladiator> games = listByMode(mode);
    for (Gladiator game : games) {
      if (game.getState() == GameState.EMJOGO) {
        playing += game.getOnline();
      }
    }

    return playing;
  }

  public static Gladiator findRandom(GladiatorMode mode) {
    List<Gladiator> games = GAMES.values().stream().filter(game -> game.getMode().equals(mode)
                    && game.getState().canJoin() && game.getOnline() < game.getMaxPlayers())
            .sorted((g1, g2) -> Integer.compare(g2.getOnline(), g1.getOnline())).collect(Collectors.toList());
    Gladiator game = games.stream().findFirst().orElse(null);
    if (game != null && game.getOnline() == 0) {
      game = games.get(ThreadLocalRandom.current().nextInt(games.size()));
    }

    return game;
  }

  public static Map<String, List<Gladiator>> getAsMap(GladiatorMode mode) {
    Map<String, List<Gladiator>> result = new HashMap<>();
    GAMES.values().stream().filter(game -> game.getMode().equals(mode) && game.getState().canJoin()
            && game.getOnline() < game.getMaxPlayers()).forEach(game -> {
      List<Gladiator> list = result.computeIfAbsent(game.getMapName(), k -> new ArrayList<>());

      if (game.getState().canJoin() && game.getOnline() < game.getMaxPlayers()) {
        list.add(game);
      }
    });

    return result;
  }

  public static List<Gladiator> listByMode(GladiatorMode mode) {
    return GAMES.values().stream().filter(sw -> sw.getMode().equals(mode))
            .collect(Collectors.toList());
  }

  public void destroy() {
    this.name = null;
    this.config.destroy();
    this.config = null;
    this.timer = 0;
    this.state = null;
    this.task.cancel();
    this.task = null;
    this.placedBlocks.clear();
    this.placedBlocks = null;
    this.players.clear();
    this.players = null;
    this.spectators.clear();
    this.spectators = null;
    this.kills.clear();
    this.kills = null;
    this.topKills.clear();
    this.topKills = null;
  }

  @Override
  public void broadcastMessage(String message) {
    this.broadcastMessage(message, true);
  }

  @Override
  public void broadcastMessage(String message, boolean spectators) {
    this.listPlayers().forEach(player -> player.sendMessage(message));
  }

  public void spectate(Player player, Player target) {
    if (this.getState() == GameState.AGUARDANDO) {
      player.sendMessage("§cA partida ainda não começou.");
      return;
    }

    Profile profile = Profile.getProfile(player.getName());
    if (profile.playingGame()) {
      if (profile.getGame().equals(this)) {
        return;
      }

      profile.getGame().leave(profile, this);
    }

    profile.setGame(this);
    spectators.add(player.getUniqueId());

    player.teleport(target.getLocation());
    reloadScoreboard(profile);
    for (Player players : Bukkit.getOnlinePlayers()) {
      if (!players.getWorld().equals(player.getWorld())) {
        player.hidePlayer(players);
        players.hidePlayer(player);
        continue;
      }

      if (isSpectator(players)) {
        players.showPlayer(player);
      } else {
        players.hidePlayer(player);
      }
      player.showPlayer(players);
    }

    profile.setHotbar(Hotbar.getHotbarById("spectator"));
    profile.refresh();
    player.setGameMode(GameMode.ADVENTURE);
    player.spigot().setCollidesWithEntities(false);
    player.setAllowFlight(true);
    player.setFlying(true);
    this.updateTags();
  }
  public boolean isPlacedBlock(Block block) {
    return !placedBlocks.contains(block);
  }

  public void addPlacedBlock(Block block) {
    placedBlocks.add(block);
  }
  private void joinParty(Profile profile, boolean ignoreLeader) {
    Player player = profile.getPlayer();
    if (player == null || !this.state.canJoin() || this.players.size() >= this.getMaxPlayers()) {
      return;
    }

    if (profile.getGame() != null && profile.getGame().equals(this)) {
      return;
    }

    GladiatorTeam team = null;
    boolean fullSize = false;
    BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
    if (party != null) {
      if (!ignoreLeader) {
        if (!party.isLeader(player.getName())) {
          player.sendMessage("§cApenas o líder da Party pode buscar por partidas.");
          return;
        }

        if (party.onlineCount() + players.size() > getMaxPlayers()) {
          return;
        }

        fullSize = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaGlad.getInstance(),
                () -> party.listMembers().stream().filter(PartyPlayer::isOnline).map(pp -> Profile.getProfile(pp.getName()))
                        .filter(pp -> pp != null && pp.getGame(FakeGame.class) == null).forEach(pp -> joinParty(pp, true)), 5);
      } else {
        team =
                listTeams().stream().filter(st -> st.canJoin() && (party.listMembers().stream().anyMatch(pp -> pp.isOnline() && st.hasMember((Player) Manager.getPlayer(pp.getName())))))
                        .findAny().orElse(null);
      }
    }

    team = team == null ? getAvailableTeam(fullSize ? this.getMode().getSize() : 1) : team;
    if (team == null) {
      return;
    }

    team.addMember(player);
    if (profile.getGame() != null) {
      profile.getGame().leave(profile, profile.getGame());
    }

    this.players.add(player.getUniqueId());
    profile.setGame(this);

    if (team.listPlayers().size() == 1) {
    }
    player.teleport(team.getLocation());
    reloadScoreboard(profile);

    profile.setHotbar(Hotbar.getHotbarById("waiting"));
    profile.refresh();
    for (Player players : Bukkit.getOnlinePlayers()) {
      if (!players.getWorld().equals(player.getWorld())) {
        player.hidePlayer(players);
        players.hidePlayer(player);
        continue;
      }

      if (isSpectator(players)) {
        player.hidePlayer(players);
      } else {
        player.showPlayer(players);
      }
      players.showPlayer(player);
    }

    this.broadcastMessage(Language.ingame$broadcast$join.replace("{player}", Role.getColored(player.getName())).replace("{players}", String.valueOf(this.getOnline()))
            .replace("{max_players}", String.valueOf(this.getMaxPlayers())));
    if (this.getOnline() == this.getMaxPlayers() && this.timer > Language.options$start$full) {
      this.timer = Language.options$start$full;
    }
  }

  boolean able(String pp) {
    List<Player> playerStream = listTeams().stream().findAny().orElse(null).listPlayers().stream().filter(pr -> pp.equals(pr.getName())).collect(Collectors.toList());
    return !playerStream.isEmpty();
  }

  @Override
  public void join(Profile profile) {
    this.joinParty(profile, false);
  }

  @Override
  public void leave(Profile profile, Game<?> game) {
    Player player = profile.getPlayer();
    if (player == null || profile.getGame() != this) {
      return;
    }

    GladiatorTeam team = this.getTeam(player);

    boolean alive = this.players.contains(player.getUniqueId());
    this.players.remove(player.getUniqueId());
    this.spectators.remove(player.getUniqueId());

    if (game != null) {
      if (alive && this.state == GameState.EMJOGO) {
        List<Profile> hitters = profile.getLastHitters();
        Profile killer = hitters.size() > 0 ? hitters.get(0) : null;
        killLeave(profile, killer);
        for (Profile hitter : hitters) {
          if (!hitter.equals(killer) && hitter.playingGame() && hitter.getGame().equals(this) && !this.isSpectator(hitter.getPlayer())) {
            hitter.addStats("kCoreGladiator", this.getMode().getStats() + "gladassists");

            // Mensal.
            hitter.addStats("kCoreGladiator", "monthlyassists");
          }
        }
        hitters.clear();
      }

      if (team != null) {
        team.removeMember(player);
        if (this.state == GameState.AGUARDANDO && !team.isAlive()) {
          team.breakCage();
        }
      }
      if (Profile.isOnline(player.getName())) {
        profile.setGame(null);
        TagUtils.setTag(player);
      }
      if (this.state == GameState.AGUARDANDO) {
        this.broadcastMessage(Language.ingame$broadcast$leave.replace("{player}", Role.getPlayerRole(player).getName()).replace("{players}", String.valueOf(this.getOnline()))
                .replace("{max_players}", String.valueOf(this.getMaxPlayers())));
      }
      this.check();
      return;
    }

    if (alive && this.state == GameState.EMJOGO) {
      List<Profile> hitters = profile.getLastHitters();
      Profile killer = hitters.size() > 0 ? hitters.get(0) : null;
      killLeave(profile, killer);
      for (Profile hitter : hitters) {
        if (!hitter.equals(killer) && hitter.playingGame() && hitter.getGame().equals(this) && !this.isSpectator(hitter.getPlayer())) {
          hitter.addStats("kCoreGladiator", this.getMode().getStats() + "gladassists");
          // Mensal
          hitter.addStats("kCoreGladiator", "monthlyassists");
        }
      }
      hitters.clear();
    }

    if (team != null) {

      if (this.state == GameState.AGUARDANDO && !team.isAlive()) {
        team.breakCage();
      }
    }
    profile.setGame(null);
    TagUtils.setTag(player);
    reloadScoreboard(profile);
    profile.setHotbar(Hotbar.getHotbarById("lobby"));
    profile.refresh();
    if (this.state == GameState.AGUARDANDO) {
      this.broadcastMessage(Language.ingame$broadcast$leave.replace("{player}", Role.getColored(player.getName())).replace("{players}", String.valueOf(this.getOnline()))
              .replace("{max_players}", String.valueOf(this.getMaxPlayers())));
    }
    this.check();
  }


  @Override
  public void start() {
    this.state = GameState.EMJOGO;
    this.task.swap(null);

    this.listTeams().forEach(GladiatorTeam::startGame);

    for (Player player : this.listPlayers(false)) {
      Profile profile = Profile.getProfile(player.getName());
      reloadScoreboard(profile);
      profile.setHotbar(null);
      profile.addStats("kCoreGladiator", this.getMode().getStats() + "gladgames");



      profile.refresh();
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);

      if (this.getMode() == GladiatorMode.SOLO){

        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        player.getInventory().setItem(0, BukkitUtils.deserializeItemStack("DIAMOND_SWORD : 1 : encantar>DAMAGE_ALL:1"));
        player.getInventory().setItem(1, BukkitUtils.deserializeItemStack("139 : 64"));
        player.getInventory().setItem(2, BukkitUtils.deserializeItemStack("5 : 64"));
        player.getInventory().setItem(3, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(4, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(5, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(6, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(7, BukkitUtils.deserializeItemStack("326 : 1"));
        player.getInventory().setItem(8, BukkitUtils.deserializeItemStack("327 : 1"));

        //Inventário
        player.getInventory().setItem(9, BukkitUtils.deserializeItemStack("IRON_HELMET : 1"));
        player.getInventory().setItem(10, BukkitUtils.deserializeItemStack("IRON_CHESTPLATE : 1"));
        player.getInventory().setItem(11, BukkitUtils.deserializeItemStack("IRON_LEGGINGS : 1"));
        player.getInventory().setItem(12, BukkitUtils.deserializeItemStack("IRON_BOOTS : 1"));
        player.getInventory().setItem(13, BukkitUtils.deserializeItemStack("BOWL : 64"));
        player.getInventory().setItem(14, BukkitUtils.deserializeItemStack("351:3 : 64"));
        player.getInventory().setItem(15, BukkitUtils.deserializeItemStack("351:3 : 64"));
        player.getInventory().setItem(16, BukkitUtils.deserializeItemStack("5 : 64"));
        player.getInventory().setItem(17, BukkitUtils.deserializeItemStack("DIAMOND_SWORD : 1"));
        player.getInventory().setItem(18, BukkitUtils.deserializeItemStack("IRON_HELMET : 1"));
        player.getInventory().setItem(19, BukkitUtils.deserializeItemStack("IRON_CHESTPLATE : 1"));
        player.getInventory().setItem(20, BukkitUtils.deserializeItemStack("IRON_LEGGINGS : 1"));
        player.getInventory().setItem(21, BukkitUtils.deserializeItemStack("IRON_BOOTS : 1"));
        player.getInventory().setItem(22, BukkitUtils.deserializeItemStack("BOWL : 64"));
        player.getInventory().setItem(23, BukkitUtils.deserializeItemStack("351:3 : 64"));
        player.getInventory().setItem(24, BukkitUtils.deserializeItemStack("351:3 : 64"));
        player.getInventory().setItem(25, BukkitUtils.deserializeItemStack("101 : 64"));
        player.getInventory().setItem(26, BukkitUtils.deserializeItemStack("274 : 1"));
        player.getInventory().setItem(27, BukkitUtils.deserializeItemStack("326 : 1"));
        player.getInventory().setItem(28, BukkitUtils.deserializeItemStack("327 : 1"));
        player.getInventory().setItem(29, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(30, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(31, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(32, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(33, BukkitUtils.deserializeItemStack("282 : 1"));
        player.getInventory().setItem(34, BukkitUtils.deserializeItemStack("COMPASS : 1"));
        player.getInventory().setItem(35, BukkitUtils.deserializeItemStack("STONE_AXE : 1"));
      }

      player.updateInventory();
      player.setGameMode(GameMode.SURVIVAL);
      player.setNoDamageTicks(80);
    }

    GDGameStartEvent evt = new GDGameStartEvent(this);
    GDEvent.callEvent(evt);

    this.updateTags();
    this.check();
  }

  @Override
  public void kill(Profile profile, Profile killer) {
    Player player = profile.getPlayer();
    this.killLeave(profile, killer);
    GDPlayerDeathEvent evt = new GDPlayerDeathEvent(this, profile, killer);
    GDEvent.callEvent(evt);
    if (evt.isCancelled()) {
      // TODO: Reviver
      return;
    }

    GladiatorTeam team = this.getTeam(player);
    if (team != null) {
      team.removeMember(player);
    }
    this.players.remove(player.getUniqueId());
    this.spectators.add(player.getUniqueId());
    profile.setHotbar(Hotbar.getHotbarById("spectator"));
    for (Player players : this.listPlayers()) {
      if (isSpectator(players)) {
        player.showPlayer(players);
      } else {
        players.hidePlayer(player);
      }
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(JavaGlad.getInstance(), () -> {
      if (player.isOnline()) {
        profile.refresh();
        player.setGameMode(GameMode.ADVENTURE);
        player.spigot().setCollidesWithEntities(false);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        if (killer != null) {
          player.setVelocity(player.getLocation().getDirection().multiply(-1.6));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaGlad.getInstance(), () -> {
          if (player.isOnline()) {
            int coinsKill = (int) profile.calculateWM(this.getKills(player) * Language.options$coins$kills);
            int pointsKill = this.getKills(player) * Language.options$points$kills;

            if (coinsKill > 0) {
              player.sendMessage(Language.ingame$messages$coins$base.replace("{points}", StringUtils.formatNumber(pointsKill)).replace("{coins}", StringUtils.formatNumber(coinsKill)).replace("{points_win}", "").replace("{coins_win}", "").replace("{coins_kills}",
                      Language.ingame$messages$coins$kills.replace("{coins}", StringUtils.formatNumber(coinsKill)).replace("{kills}", StringUtils.formatNumber(this.getKills(player)))
                              .replace("{s}", this.getKills(player) > 1 ? "s" : "")).replace("{points_kills}", pointsKill < 1 ? "" : Language.ingame$messages$points$kills.replace("{s}", this.getKills(player) > 1 ? "s" : "").replace("{points}", StringUtils.formatNumber(pointsKill))));
            }

            if (this.getMode().equals(GladiatorMode.RANKED) && pointsKill > 0) {
              player.sendMessage(Language.ingame$messages$points$base.replace("{points}", StringUtils.formatNumber(pointsKill)).replace("{coins}", StringUtils.formatNumber(coinsKill)).replace("{points_win}", "").replace("{coins_win}", "").replace("{coins_kills}", this.getKills(player) < 1 ? "" :
                      Language.ingame$messages$points$kills.replace("{points}", StringUtils.formatNumber(coinsKill)).replace("{kills}", StringUtils.formatNumber(this.getKills(player)))
                              .replace("{s}", this.getKills(player) > 1 ? "s" : "")).replace("{points_kills}", Language.ingame$messages$points$kills.replace("{kills}", StringUtils.formatNumber(this.getKills(player))).replace("{s}", this.getKills(player) > 1 ? "s" : "").replace("{points}", StringUtils.formatNumber(pointsKill))));
            }

            NMS.sendTitle(player, Language.ingame$titles$death$header, Language.ingame$titles$death$footer, 0, 60, 0);
          }
        }, 27);
      }
    }, 3);
    this.updateTags();
    this.check();
  }

  @Override
  public void killLeave(Profile profile, Profile killer) {
    Player player = profile.getPlayer();

    Player pk = killer != null ? killer.getPlayer() : null;
    if (player.equals(pk)) {
      pk = null;
    }

    profile.addStats("kCoreGladiator", this.getMode().getStats() + "deaths");
    profile.addStats("kCoreGladiator", "monthlydeaths");
    if (pk == null) {
      this.broadcastMessage(Language.ingame$broadcast$suicide.replace("{name}", Role.getColored(player.getName()) + "" + player.getName()));


      String suffix = this.addKills(pk);
      EnumSound.ORB_PICKUP.play(pk, 1.0F, 1.0F);

      killer.addCoinsWM("kCoreGladiator", Language.options$coins$kills);
      killer.addStats("kCoreGladiator", this.getMode().getStats() + "kills");
      killer.addStats("kCoreGladiator", "monthlykills");
      //    if (Main.kClans) {
      //    if (ClanAPI.getClanByPlayerName(killer.getName()) != null) {
      ////     ClanAPI.addCoins(ClanAPI.getClanByPlayerName(killer.getName()), Language.options$coins$clan$kills);
      //  }
      //  }
      this.broadcastMessage(Language.ingame$broadcast$default_killed_message.replace("{name}", Role.getColored(player.getName())).replace("{killer}", Role.getColored(pk.getName())) + suffix);
    }

  }

  private void check() {
    if (this.state != GameState.EMJOGO) {
      return;
    }

    List<GladiatorTeam> teams = this.listTeams().stream().filter(GameTeam::isAlive).collect(Collectors.toList());
    if (teams.size() <= 1) {
      this.stop(teams.isEmpty() ? null : teams.get(0));
    }

    teams.clear();
  }

  @Override
  public void stop(GladiatorTeam winners) {
    this.state = GameState.ENCERRADO;

    StringBuilder name = new StringBuilder();
    List<Player> players = winners != null ? winners.listPlayers() : null;
    if (players != null) {
      for (Player player : players) {
        if (!name.toString().isEmpty()) {
          name.append(" §ae ").append(Role.getColored(player.getName()));
        } else {
          name = new StringBuilder(Role.getColored(player.getName()));
        }
      }

      players.clear();
    }
    if (name.toString().isEmpty()) {
      this.broadcastMessage(Language.ingame$broadcast$end);
    } else {
      this.broadcastMessage((this.getMode() == GladiatorMode.SOLO || this.getMode() == GladiatorMode.RANKED ? Language.ingame$broadcast$win$solo : Language.ingame$broadcast$win$dupla).replace("{name}", name.toString()));
    }
    for (Player player : this.listPlayers(false)) {
      Profile profile = Profile.getProfile(player.getName());
      profile.update();
      GladiatorTeam team = this.getTeam(player);
      if (team != null) {
        int coinsWin = (int) (team.equals(winners) ? profile.calculateWM(Language.options$coins$wins) : 0);
        int coinsKill = (int) profile.calculateWM(this.getKills(player) * Language.options$coins$kills);
        int pointsKill = this.getKills(player) * Language.options$points$kills;
        int pointsWin = (team.equals(winners) ? Language.options$points$wins : 0);
        int totalPoints = pointsKill + pointsWin;
        int totalCoins = coinsWin + coinsKill;

        if (totalCoins > 0 || totalPoints > 0) {
          Bukkit.getScheduler().scheduleSyncDelayedTask(JavaGlad.getInstance(), () -> {
            if (totalCoins > 0) {
              player.sendMessage(
                      Language.ingame$messages$coins$base.replace("{points}", StringUtils.formatNumber(totalPoints)).replace("{coins}", StringUtils.formatNumber(totalCoins))
                              .replace("{points_kills}", pointsKill < 1 ? "" : Language.ingame$messages$points$kills.replace("{s}", this.getKills(player) > 1 ? "s" : "").replace("{points}", StringUtils.formatNumber(pointsKill))).replace("{points_win}", pointsWin > 0 ? Language.ingame$messages$points$win.replace("{points}", StringUtils.formatNumber(pointsWin)) : "").replace("{coins_win}", coinsWin > 0 ? Language.ingame$messages$coins$win.replace("{coins}", StringUtils.formatNumber(coinsWin)) : "").replace("{coins_kills}",
                                      coinsKill > 0 ?
                                              Language.ingame$messages$coins$kills.replace("{coins}", StringUtils.formatNumber(coinsKill)).replace("{kills}", StringUtils.formatNumber(this.getKills(player)))
                                                      .replace("{s}", this.getKills(player) > 1 ? "s" : "") :
                                              ""));
            }
            if (totalPoints > 0 && this.getMode().equals(GladiatorMode.RANKED)) {
              player.sendMessage(
                      Language.ingame$messages$points$base
                              .replace("{points}", StringUtils.formatNumber(totalPoints))
                              .replace("{points_kills}", pointsKill < 1 ? "" : Language.ingame$messages$points$kills
                                      .replace("{kills}", StringUtils.formatNumber(this.getKills(player)))
                                      .replace("{s}", this.getKills(player) > 1 ? "s" : "")
                                      .replace("{points}", StringUtils.formatNumber(pointsKill)))
                              .replace("{points_win}", pointsWin > 0 ? Language.ingame$messages$points$win.
                                      replace("{points}", StringUtils.formatNumber(pointsWin)) : "")
                              .replace("{coins_win}", coinsWin > 0 ? Language.ingame$messages$coins$win
                                      .replace("{coins}", StringUtils.formatNumber(coinsWin)) : "")
                              .replace("{coins_kills}",
                                      coinsKill > 0 ?
                                              Language.ingame$messages$coins$kills.replace("{coins}",
                                                              StringUtils.formatNumber(coinsKill)).replace("{kills}", StringUtils.formatNumber(this.getKills(player)))
                                                      .replace("{s}", this.getKills(player) > 1 ? "s" : "") :
                                              ""));
            }
          }, 30);
        }
      }

      if (winners != null && winners.hasMember(player)) {
        profile.addCoinsWM("kCoreGladiator", Language.options$coins$wins);
        if (this.getMode().equals(GladiatorMode.RANKED)) {
          profile.addStats("kCoreGladiator", Language.options$points$wins, "rankedpoints");
        }
        profile.addStats("kCoreGladiator", this.getMode().getStats() + "wins");

        NMS.sendTitle(player, Language.ingame$titles$win$header, Language.ingame$titles$win$footer, 10, 80, 10);
      } else {
        NMS.sendTitle(player, Language.ingame$titles$lose$header, Language.ingame$titles$lose$footer, 10, 80, 10);
      }

      this.spectators.add(player.getUniqueId());
      profile.setHotbar(Hotbar.getHotbarById("spectator"));
      profile.refresh();
      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
    }

    this.updateTags();
    this.task.swap(winners);
  }

  @Override
  public void reset() {
    this.event = null;
    this.nextEvent = null;
    this.topKills.clear();
    this.kills.clear();
    this.streak.clear();
    this.players.clear();
    this.spectators.clear();
    this.placedBlocks.clear();
    this.task.cancel();
    this.placedBlocks.clear();

    this.listTeams().forEach(GladiatorTeam::reset);
    addToQueue(this);
  }

  private void updateTags() {
    for (Player player : this.listPlayers()) {
      Scoreboard scoreboard = player.getScoreboard();

      for (Player players : this.listPlayers()) {
        GladiatorTeam gt;

        if (this.isSpectator(players)) {
          Team team = scoreboard.getEntryTeam(players.getName());
          if (team != null && !team.getName().equals("spectators")) {
            if (team.getSize() == 1) {
              team.unregister();
            } else {
              team.removeEntry(players.getName());
            }
            team = null;
          }

          if (team == null) {
            team = scoreboard.getTeam("spectators");
            if (team == null) {
              team = scoreboard.registerNewTeam("spectators");
              team.setPrefix("§8");
              team.setCanSeeFriendlyInvisibles(true);
            }

            if (!team.hasEntry(players.getName())) {
              team.addEntry(players.getName());
            }
          }
        } else if ((gt = this.getTeam(players)) != null) {
          Team team = scoreboard.getTeam(gt.getName());
          if (team == null) {
            team = scoreboard.registerNewTeam(gt.getName());
            team.setPrefix(gt.hasMember(player) ? "§a" : "§c");
          }

          if (!team.hasEntry(players.getName())) {
            team.addEntry(players.getName());
          }
        }
      }
    }

  }

  @Override
  public String getGameName() {
    return this.name;
  }

  public int getTimer() {
    return this.timer;
  }

  public void setTimer(int timer) {
    this.timer = timer;
  }

  public GladiatorConfig getConfig() {
    return this.config;
  }

  public World getWorld() {
    return this.config.getWorld();
  }

  public GladiatorTask getTask() {
    return this.task;
  }

  public CubeID getCubeId() {
    return this.config.getCubeId();
  }

  public String getMapName() {
    return this.config.getMapName();
  }

  public GladiatorMode getMode() {
    return this.config.getMode();
  }

  @Override
  public GameState getState() {
    return this.state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  @Override
  public boolean isSpectator(Player player) {
    return this.spectators.contains(player.getUniqueId());
  }

  public String addKills(Player player) {
    this.kills.put(player.getName(), this.getKills(player) + 1);
    return "";
  }

  public int getKills(Player player) {
    return this.kills.get(player.getName()) != null ? kills.get(player.getName()) : 0;
  }



  @Override
  public int getOnline() {
    return this.players.size();
  }

  @Override
  public int getMaxPlayers() {
    return this.listTeams().size() * this.getMode().getSize();
  }

  public GladiatorTeam getAvailableTeam(int teamSize) {
    return this.listTeams().stream().filter(team -> team.canJoin(teamSize)).findAny().orElse(null);
  }

  @Override
  public GladiatorTeam getTeam(Player player) {
    return this.listTeams().stream().filter(team -> team.hasMember(player)).findAny().orElse(null);
  }

  public void resetBlock(Block block) {
    GladiatorBlock sb = this.blocks.get(BukkitUtils.serializeLocation(block.getLocation()));

    if (sb != null) {
      block.setType(sb.getMaterial());
      BlockState state = block.getState();
      state.getData().setData(sb.getData());
      state.update(true);
    } else {
      block.setType(Material.AIR);
    }
  }

  public Map<String, GladiatorBlock> getBlocks() {
    return this.blocks;
  }

  public void generateEvent() {
    this.event =
            this.listEvents().entrySet().stream().filter(e -> !(e.getValue() instanceof AnnounceEvent) && this.getTimer() < e.getKey()).min(Comparator.comparingInt(Map.Entry::getKey))
                    .orElse(null);
    this.nextEvent = this.listEvents().entrySet().stream().filter(e -> this.getTimer() < e.getKey()).min(Comparator.comparingInt(Map.Entry::getKey)).orElse(null);
  }

  public String getEvent() {
    if (this.event == null) {
      return Language.options$events$end;
    }

    return this.event.getValue().getName() + " " + SDF.format((this.event.getKey() - this.getTimer()) * 1000);
  }

  public Map.Entry<Integer, GladiatorEvent> getNextEvent() {
    return this.nextEvent;
  }

  public int getTimeUntilEvent() {
    return (this.event == null ? this.getTimer() : this.event.getKey()) - this.getTimer();
  }

  @Override
  public List<GladiatorTeam> listTeams() {
    return this.config.listTeams();
  }



  public Map<Integer, GladiatorEvent> listEvents() {
    return this.getMode() == GladiatorMode.SOLO ? GladiatorEvent.SOLO : this.getMode() == GladiatorMode.RANKED ? GladiatorEvent.RANKED : GladiatorEvent.DUPLA;
  }

  @Override
  public List<Player> listPlayers() {
    return this.listPlayers(true);
  }

  @Override
  public List<Player> listPlayers(boolean spectators) {
    List<Player> players = new ArrayList<>(spectators ? this.spectators.size() + this.players.size() : this.players.size());
    this.players.forEach(id -> players.add(Bukkit.getPlayer(id)));
    if (spectators) {
      this.spectators.stream().filter(id -> !this.players.contains(id)).forEach(id -> players.add(Bukkit.getPlayer(id)));
    }

    return players.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }
}

