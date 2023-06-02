package br.com.johnmanoel.d4rkk.glad.cmd.gl;

import br.com.johnmanoel.d4rkk.glad.JavaGlad;
import br.com.johnmanoel.d4rkk.glad.cmd.SubCommand;
import dev.slickcollections.kiwizin.plugin.config.KConfig;
import br.com.johnmanoel.d4rkk.glad.game.Gladiator;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class CloneCommand extends SubCommand {

  public CloneCommand() {
    super("clonar", "clonar [mundo] [novoMundo]", "Clonar uma sala.", false);
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length <= 1) {
      sender.sendMessage("§cUtilize /gd " + this.getUsage());
      return;
    }
    
    Gladiator game = Gladiator.getByWorldName(args[0]);
    if (game == null) {
      sender.sendMessage("§cNão existe uma sala neste mundo.");
      return;
    }
    
    String newWorld = args[1];
    if (Gladiator.getByWorldName(newWorld) != null) {
      sender.sendMessage("§cJá existe uma sala no mundo \"" + newWorld + "\".");
      return;
    }
    
    sender.sendMessage("§aClonando sala...");
    KConfig config = JavaGlad.getInstance().getConfig("arenas", newWorld);
    for (String key : game.getConfig().getConfig().getKeys(false)) {
      Object value = game.getConfig().getConfig().get(key);
      if (value instanceof String) {
        value = ((String) value).replace(game.getGameName(), newWorld);
      } else if (value instanceof List) {
        List<String> list = new ArrayList<>();
        for (String v : (List<String>) value) {
          list.add(v.replace(game.getGameName(), newWorld));
        }
        value = list;
      }
      
      config.set(key, value);
    }
    
    JavaGlad.getInstance().getFileUtils().copyFiles(new File("plugins/vGladiator/mundos", args[0]), new File("plugins/vGladiator/mundos", newWorld));
    Gladiator.load(config.getFile(), () -> sender.sendMessage("§aSala foi clonada."));
  }
}
