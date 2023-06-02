package br.com.johnmanoel.d4rkk.glad.cmd.gl;


import br.com.johnmanoel.d4rkk.glad.cmd.SubCommand;
import br.com.johnmanoel.d4rkk.glad.lobby.Leaderboard;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends SubCommand {
  
  public LeaderboardCommand() {
    super("leaderboard", "leaderboard", "Adicione/remova Leaderboards.", true);
  }
  
  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§eAjuda\n \n§6/gd leaderboard adicionar [id] [tipo] §f- §7Adicionar Leaderboard.\n§6/gd leaderboard remover [id] §f- §7Remover Leaderboard.\n ");
      return;
    }
    
    String action = args[0];
    if (action.equalsIgnoreCase("adicionar")) {
      if (args.length <= 2) {
        player.sendMessage("§cUtilize /gd leaderboard adicionar [id] [vitorias/winstreak/abates]");
        return;
      }
      
      String id = args[1];
      if (Leaderboard.getById(id) != null) {
        player.sendMessage("§cJá existe uma Leaderboard utilizando \"" + id + "\" como ID.");
        return;
      }
      
      String type = args[2];
      if (!type.equalsIgnoreCase("vitorias") && !type.equalsIgnoreCase("winstreak") && !type.equalsIgnoreCase("abates")) {
        player.sendMessage("§cUtilize /gd leaderboard adicionar [id] [vitorias/winstreak/abates]");
        return;
      }
      
      Location location = player.getLocation().clone();
      location.setX(location.getBlock().getLocation().getX() + 0.5);
      location.setZ(location.getBlock().getLocation().getZ() + 0.5);
      Leaderboard.add(location, id, type);
      player.sendMessage("§aLeaderboard adicionada com sucesso.");
    } else if (action.equalsIgnoreCase("remover")) {
      if (args.length <= 1) {
        player.sendMessage("§cUtilize /gd leaderboard remover [id]");
        return;
      }
      
      String id = args[1];
      Leaderboard board = Leaderboard.getById(id);
      if (board == null) {
        player.sendMessage("§cNão existe uma Leaderboard utilizando \"" + id + "\" como ID.");
        return;
      }
      
      Leaderboard.remove(board);
      player.sendMessage("§cLeaderboard removida com sucesso.");
    } else {
      player.sendMessage(" \n§eAjuda\n \n§6/gd leaderboard adicionar [id] [tipo] §f- §7Adicionar Leaderboard.\n§6/gd leaderboard remover [id] §f- §7Remover Leaderboard.\n ");
    }
  }
}
