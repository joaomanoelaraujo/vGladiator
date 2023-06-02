package br.com.johnmanoel.d4rkk.glad.hook.hotbar;

import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import dev.slickcollections.kiwizin.player.Profile;
import dev.slickcollections.kiwizin.player.hotbar.HotbarActionType;

import br.com.johnmanoel.d4rkk.glad.menus.MenuLobbies;
import br.com.johnmanoel.d4rkk.glad.menus.MenuPlay;
import br.com.johnmanoel.d4rkk.glad.menus.MenuSpectator;


public class SWHotbarActionType extends HotbarActionType {
  
  @Override
  public void execute(Profile profile, String action) {
     if (action.equalsIgnoreCase("lobbies")) {
      new MenuLobbies(profile);
    } else if (action.equalsIgnoreCase("espectar")) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game != null) {
        new MenuSpectator(profile.getPlayer(), game);
      }
    } else if (action.equalsIgnoreCase("jogar")) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game != null) {
        new MenuPlay(profile, game.getMode());
      }
    } else if (action.equalsIgnoreCase("sair")) {
      Gladiator game = profile.getGame(Gladiator.class);
      if (game != null) {
        game.leave(profile, null);
      }
    }
  }
}
