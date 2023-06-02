package br.com.johnmanoel.d4rkk.glad.game.api;

import java.util.ArrayList;
import java.util.List;

public class GDEvent {
  
  private static final List<GDEventHandler> HANDLERS = new ArrayList<>();
  
  public static void registerHandler(GDEventHandler handler) {
    HANDLERS.add(handler);
  }
  
  public static void callEvent(GDEvent evt) {
    HANDLERS.stream().filter(handler -> handler.getEventTypes().contains(evt.getClass())).forEach(handler -> handler.handleEvent(evt));
  }
}
