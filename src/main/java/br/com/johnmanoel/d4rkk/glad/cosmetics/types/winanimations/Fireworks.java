package br.com.johnmanoel.d4rkk.glad.cosmetics.types.winanimations;



import br.com.johnmanoel.d4rkk.glad.cosmetics.object.AbstractExecutor;
import br.com.johnmanoel.d4rkk.glad.cosmetics.object.winanimations.FireworksExecutor;
import br.com.johnmanoel.d4rkk.glad.cosmetics.types.WinAnimation;
import dev.slickcollections.kiwizin.player.Profile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public class Fireworks extends WinAnimation {

  public Fireworks(ConfigurationSection section) {
    super(0, "fireworks", 0.0, "", section.getString("name"), section.getString("icon"));
  }

  @Override
  public boolean has(Profile profile) {
    return true;
  }



  public AbstractExecutor execute(Player player) {
    return new FireworksExecutor(player);
  }
}
