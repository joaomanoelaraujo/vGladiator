package br.com.johnmanoel.d4rkk.glad.game.gamemodes;

import br.com.johnmanoel.d4rkk.glad.game.types.NormalGladiator;
import dev.slickcollections.kiwizin.reflection.Accessors;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.interfaces.LoadCallback;

public enum GladiatorMode {
  RANKED("Ranked", "ranked", 1, NormalGladiator.class, 1),
  SOLO("Solo", "1v1", 1, NormalGladiator.class, 1),
  DUPLA("Duplas", "2v2", 2, NormalGladiator.class, 1);
  
  private static final GladiatorMode[] VALUES = values();
  private final int size;
  private final String stats;
  private final String name;
  private final Class<? extends Gladiator> gameClass;
  private final int cosmeticIndex;
  
  GladiatorMode(String name, String stats, int size, Class<? extends Gladiator> gameClass, int cosmeticIndex) {
    this.name = name;
    this.stats = stats;
    this.size = size;
    this.gameClass = gameClass;
    this.cosmeticIndex = cosmeticIndex;
  }
  
  public static GladiatorMode fromName(String name) {
    for (GladiatorMode mode : VALUES) {
      if (name.equalsIgnoreCase(mode.name())) {
        return mode;
      }
    }
    
    return null;
  }
  
  public Gladiator buildGame(String name, LoadCallback callback) {
    return Accessors.getConstructor(this.gameClass, String.class, LoadCallback.class).newInstance(name, callback);
  }
  
  public int getSize() {
    return this.size;
  }
  
  public String getStats() {
    return this.stats;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getCosmeticIndex() {
    return cosmeticIndex;
  }
}
