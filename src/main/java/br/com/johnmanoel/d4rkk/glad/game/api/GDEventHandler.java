package br.com.johnmanoel.d4rkk.glad.game.api;

import java.util.List;

public interface GDEventHandler {
  
  void handleEvent(GDEvent evt);
  
  List<Class<?>> getEventTypes();
}
