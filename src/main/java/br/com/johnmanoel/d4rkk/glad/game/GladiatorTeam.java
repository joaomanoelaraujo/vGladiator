package br.com.johnmanoel.d4rkk.glad.game;

import dev.slickcollections.kiwizin.game.GameTeam;

public class GladiatorTeam extends GameTeam {
  
  private final int index;

  
  public GladiatorTeam(Gladiator game, String location, int size) {
    super(game, location, size);
    this.index = game.listTeams().size();
  }
  
  @Override
  public void reset() {
    super.reset();
  }
  
  public void startGame() {
    this.breakCage();
  }

  
  public void breakCage() {
  }
}
