package br.com.johnmanoel.d4rkk.glad.game.object;

import org.bukkit.Material;

public class GladiatorBlock {
  
  private final Material material;
  private final byte data;
  
  public GladiatorBlock(Material material, byte data) {
    this.material = material;
    this.data = data;
  }
  
  public Material getMaterial() {
    return this.material;
  }
  
  public byte getData() {
    return this.data;
  }
  
  @Override
  public String toString() {
    return "GladiatorBlock{material=" + material + ", data=" + data + "}";
  }
}
