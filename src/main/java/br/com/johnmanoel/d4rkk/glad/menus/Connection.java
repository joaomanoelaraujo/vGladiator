package br.com.johnmanoel.d4rkk.glad.menus;

import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.gamemodes.GladiatorMode;
import dev.slickcollections.kiwizin.player.Profile;

public class Connection {

  private GladiatorMode mode;

  public Connection(Profile profile) {

    this.mode = mode;
    Gladiator game = Gladiator.findRandom(this.mode);
    game.join(profile);


  }

}
