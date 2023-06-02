package br.com.johnmanoel.d4rkk.glad.game.events;

import br.com.johnmanoel.d4rkk.glad.Language;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import br.com.johnmanoel.d4rkk.glad.game.GladiatorEvent;

public class EndEvent extends GladiatorEvent {
  
  @Override
  public void execute(Gladiator game) {
    game.stop(null);
  }
  
  @Override
  public String getName() {
    return Language.options$events$end;
  }
}
