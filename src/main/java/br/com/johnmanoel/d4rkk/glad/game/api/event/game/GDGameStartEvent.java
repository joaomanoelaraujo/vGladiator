package br.com.johnmanoel.d4rkk.glad.game.api.event.game;

import dev.slickcollections.kiwizin.game.Game;
import dev.slickcollections.kiwizin.game.GameTeam;
import br.com.johnmanoel.d4rkk.glad.game.api.GDEvent;

public class GDGameStartEvent extends GDEvent {
  
  private final Game<? extends GameTeam> game;
  
  public GDGameStartEvent(Game<? extends GameTeam> game) {
    this.game = game;
  }
  
  public Game<? extends GameTeam> getGame() {
    return this.game;
  }
}
