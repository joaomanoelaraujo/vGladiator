package br.com.johnmanoel.d4rkk.glad.lobby;

import br.com.johnmanoel.d4rkk.glad.Language;
import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import br.com.johnmanoel.d4rkk.glad.lobby.trait.NPCSkinTrait;
import dev.slickcollections.kiwizin.libraries.holograms.HologramLibrary;
import dev.slickcollections.kiwizin.libraries.holograms.api.Hologram;
import dev.slickcollections.kiwizin.libraries.npclib.NPCLibrary;
import dev.slickcollections.kiwizin.libraries.npclib.api.npc.NPC;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.utils.BukkitUtils;
import dev.slickcollections.kiwizin.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayNPC {
  
  private static final KConfig CONFIG = JavaGlad.getInstance().getConfig("npcs");
  private static final List<PlayNPC> NPCS = new ArrayList<>();
  private String id;
  private GladiatorMode mode;
  private Location location;
  private NPC npc;
  private Hologram hologram;
  
  public PlayNPC(Location location, String id, GladiatorMode mode) {
    this.location = location;
    this.id = id;
    this.mode = mode;
    if (!this.location.getChunk().isLoaded()) {
      this.location.getChunk().load(true);
    }
    
    this.spawn();
  }
  
  public static void setupNPCs() {
    if (!CONFIG.contains("play")) {
      CONFIG.set("play", new ArrayList<>());
    }
    
    for (String serialized : CONFIG.getStringList("play")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];
        GladiatorMode mode = GladiatorMode.fromName(serialized.split("; ")[7]);
        if (mode == null) {
          continue;
        }
        
        NPCS.add(new PlayNPC(BukkitUtils.deserializeLocation(serialized), id, mode));
      }
    }
    
    Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaGlad.getInstance(), () -> listNPCs().forEach(PlayNPC::update), 20, 20);
  }
  
  public static void add(String id, Location location, GladiatorMode mode) {
    NPCS.add(new PlayNPC(location, id, mode));
    List<String> list = CONFIG.getStringList("play");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id + "; " + mode);
    CONFIG.set("play", list);
  }
  
  public static void remove(PlayNPC npc) {
    NPCS.remove(npc);
    List<String> list = CONFIG.getStringList("play");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId() + "; " + npc.getMode());
    CONFIG.set("play", list);
    
    npc.destroy();
  }
  
  public static PlayNPC getById(String id) {
    return NPCS.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }
  
  public static Collection<PlayNPC> listNPCs() {
    return NPCS;
  }

  public void spawn() {
    if (this.npc != null) {
      this.npc.destroy();
      this.npc = null;
    }
    
    if (this.hologram != null) {
      HologramLibrary.removeHologram(this.hologram);
      this.hologram = null;
    }
    
    this.hologram = HologramLibrary.createHologram(this.location.clone().add(0, 0.5, 0));
    for (int index = (this.mode == GladiatorMode.SOLO ? Language.lobby$npc$play$solo$hologram.size() : (this.mode == GladiatorMode.RANKED ? Language.lobby$npc$play$ranked$hologram.size() : Language.lobby$npc$play$dupla$hologram.size())); index > 0; index--) {
      this.hologram.withLine((this.mode == GladiatorMode.SOLO ? Language.lobby$npc$play$solo$hologram : this.mode == GladiatorMode.RANKED ? Language.lobby$npc$play$ranked$hologram : Language.lobby$npc$play$dupla$hologram).get(index - 1).replace("{players}",
          StringUtils.formatNumber(Gladiator.getWaiting(this.mode) + Gladiator.getPlaying(this.mode))));
    }
    
    this.npc = NPCLibrary.createNPC(EntityType.PLAYER, "§8[NPC] ");
    this.npc.data().set("play-npc", this.mode.name());
    this.npc.data().set(NPC.HIDE_BY_TEAMS_KEY, true);
    if (this.mode == GladiatorMode.SOLO) {
      this.npc.addTrait(new NPCSkinTrait(this.npc, Language.lobby$npc$play$solo$skin$value, Language.lobby$npc$play$solo$skin$signature));
    } else if (this.mode == GladiatorMode.RANKED) {
      this.npc.addTrait(new NPCSkinTrait(this.npc, Language.lobby$npc$play$ranked$skin$value, Language.lobby$npc$play$ranked$skin$signature));
    } else {
      this.npc.addTrait(new NPCSkinTrait(this.npc, Language.lobby$npc$play$dupla$skin$value, Language.lobby$npc$play$dupla$skin$signature));
    }
    
    this.npc.spawn(this.location);
  }
  
  public void update() {
    int size = this.mode == GladiatorMode.SOLO ? Language.lobby$npc$play$solo$hologram.size() : Language.lobby$npc$play$dupla$hologram.size();
    for (int index = size; index > 0; index--) {
      this.hologram.updateLine(size - (index - 1), (this.mode == GladiatorMode.SOLO ? Language.lobby$npc$play$solo$hologram : this.mode == GladiatorMode.RANKED ? Language.lobby$npc$play$ranked$hologram : Language.lobby$npc$play$dupla$hologram).get(index - 1)
          .replace("{players}", StringUtils.formatNumber(Gladiator.getWaiting(this.mode) + Gladiator.getPlaying(this.mode))));
    }
  }
  
  public void destroy() {
    this.id = null;
    this.mode = null;
    this.location = null;
    
    this.npc.destroy();
    this.npc = null;
    HologramLibrary.removeHologram(this.hologram);
    this.hologram = null;
  }
  
  public NPC getNpc() {
    return this.npc;
  }
  
  public String getId() {
    return id;
  }
  
  public GladiatorMode getMode() {
    return this.mode;
  }
  
  public Location getLocation() {
    return this.location;
  }
}
