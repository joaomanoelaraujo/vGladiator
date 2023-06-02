package br.com.johnmanoel.d4rkk.glad;

import dev.slickcollections.kiwizin.plugin.config.KConfig;
import dev.slickcollections.kiwizin.plugin.config.KWriter;
import dev.slickcollections.kiwizin.plugin.logger.KLogger;
import dev.slickcollections.kiwizin.utils.StringUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("rawtypes")
public class Language {
  
  public static final KLogger LOGGER = ((KLogger) JavaGlad.getInstance().getLogger())
      .getModule("LANGUAGE");
  private static final KConfig CONFIG = JavaGlad.getInstance().getConfig("language");
  public static long scoreboards$scroller$every_tick = 9;
  public static List<String> scoreboards$scroller$titles = Arrays
      .asList("§6§lGLADIATOR", "§f§lGLADIATOR", "§6§lGLADIATOR", "§f§lGLADIATOR", "§6§lGLADIATOR", "§f§lGLADIATOR", "§6§lG§f§lLADIATOR", "§6§lGL§f§lADIATOR", "§6§lGLA§f§lDIATOR"
              , "§6§lGLAD§f§lIATOR", "§6§lGLADI§f§lATOR", "§6§lGLADIA§f§lTOR", "§6§lGLADIAT§f§lOR", "§6§lGLADIATO§f§lR", "§6§lGLADIATOR");
  public static String scoreboards$time$waiting = "Aguardando...";
  public static String scoreboards$time$starting = "Iniciando em §a{time}s";
  public static List<String> scoreboards$lobby = Arrays
      .asList("",
              " Kills: §a%kCore_Gladiator_gladkills%",
              " Wins: §a%kCore_Gladiator_gladwins%",
              " Plays: §a%kCore_Gladiator_gladgames%",
              "",
              " Winstreak: §a%kCore_Gladiator_winstreak%",
              "",
              " Jogadores: §7%kCore_online%/100",
              "",
              " §cwww.vootytools.com.br");
  public static List<String> scoreboards$waiting =
      Arrays
          .asList("",
                  "Modo: §aPadrão",
                  "Jogadores: §a{players}/{max_players}",
                  "",
                  "Winstreak: §a%kCore_Gladiator_winstreak%",
                  "{time}",
                  "",
                  "§cwww.vootytools.com.br");
  public static List<String> scoreboards$ingame$solo = Arrays
      .asList("",
              "Modo: §aPadrão",
              "Tempo: §7{next_event}",
              "",
              "Winstreak: §a%kCore_Gladiator_winstreak%",
              "",
              "§cwww.vootytools.com.br");

  public static String chat$delay = "§cAguarde mais {time}s para falar novamente.";
  public static String chat$color$default = "§7";
  public static String chat$color$custom = "§f";
  public static String chat$format$lobby = "{player}{color}: {message}";
  public static String chat$format$spectator = "§8[Espectador] {player}{color}: {message}";
  public static int options$coins$wins = 50;
  public static int options$coins$kills = 5;
  public static int options$coins$clan$wins = 50;
  public static int options$coins$clan$kills = 15;
  public static int options$coins$clan$play = 5;
  public static int options$points$wins = 60;
  public static int options$points$kills = 20;
  public static int options$start$waiting = 45;
  public static int options$start$full = 10;
  @KWriter.YamlEntryInfo(annotation = "Existem dois tipos de Regeneração de Arena: WorldReload e BlockRegen.\nÉ recomendável que teste os dois e veja qual se sai melhor.")
  public static boolean options$regen$world_reload = true;
  @KWriter.YamlEntryInfo(annotation = "Quantos blocos serão regenerados por tick no BlockRegen")
  public static int options$regen$block_regen$per_tick = 20000;
  public static String options$events$end = "";
  @KWriter.YamlEntryInfo(annotation = "Se você não definir o evento FIM a partida não terá fim por tempo.\nEventos disponíveis:\nFIM:tempo_em_segundos\nANUNCIO(anuncio de minutos restante):tempo_em_segundos\nREFILL:tempo_em_segundos")
  public static List<String> options$events$solo$timings = Arrays
      .asList("FIM:840");
  public static List<String> options$events$ranked$timings = Arrays
      .asList("FIM:840");
  public static List<String> options$events$dupla$timings = Arrays
      .asList("FIM:840");

  public static String lobby$broadcast = "{player} §6entrou no lobby!";
  public static boolean lobby$tab$enabled = true;
  public static String lobby$tab$header = " \n§b§lREDE SLICK\n  §fredeslick.com\n ";
  public static String lobby$tab$footer =
      " \n \n§aForúm: §fredeslick.com/forum\n§aTwitter: §f@RedeSlick\n§aDiscord: §fredeslick.com/discord\n \n                                          §bAdquira VIP acessando: §floja.redeslick.com                                          \n ";
  //  public static long lobby$leaderboard$minutes = 30;
  public static String lobby$leaderboard$empty = "§7Ninguém";


  public static List<String> lobby$leaderboard$daily_winstreak$holograms = Arrays
          .asList("§a10. {name_10} §7- §a{stats_10}", "§a9. {name_9} §7- §a{stats_9}",
                  "§a8. {name_8} §7- §a{stats_8}", "§a7. {name_7} §7- §a{stats_7}",
                  "§a6. {name_6} §7- §a{stats_6}",
                  "§a5. {name_5} §7- §a{stats_5}", "§a4. {name_4} §7- §a{stats_4}",
                  "§a3. {name_3} §7- §a{stats_3}", "§a2. {name_2} §7- §a{stats_2}",
                  "§a1. {name_1} §7- §a{stats_1}", "",
                  "§7Winstreak Diário", "§f§lTodos os Modos");
  public static List<String> lobby$leaderboard$wins$hologram = Arrays
      .asList("{monthly_color}Mensal {total_color}Total", "§6§lClique para alternar!", "§a10. {name_10} §7- §a{stats_10}", "§a9. {name_9} §7- §a{stats_9}",
          "§a8. {name_8} §7- §a{stats_8}", "§a7. {name_7} §7- §a{stats_7}",
          "§a6. {name_6} §7- §a{stats_6}",
          "§a5. {name_5} §7- §a{stats_5}", "§a4. {name_4} §7- §a{stats_4}",
          "§a3. {name_3} §7- §a{stats_3}", "§a2. {name_2} §7- §a{stats_2}",
          "§a1. {name_1} §7- §a{stats_1}", "",
          "§7Ranking de Vitórias", "§f§lTodos os Modos");
  public static List<String> lobby$leaderboard$kills$hologram = Arrays
      .asList("{monthly_color}Mensal {total_color}Total", "§6§lClique para alternar!", "§a10. {name_10} §7- §a{stats_10}", "§a9. {name_9} §7- §a{stats_9}",
          "§a8. {name_8} §7- §a{stats_8}", "§a7. {name_7} §7- §a{stats_7}",
          "§a6. {name_6} §7- §a{stats_6}",
          "§a5. {name_5} §7- §a{stats_5}", "§a4. {name_4} §7- §a{stats_4}",
          "§a3. {name_3} §7- §a{stats_3}", "§a2. {name_2} §7- §a{stats_2}",
          "§a1. {name_1} §7- §a{stats_1}", "",
          "§7Ranking de Abates", "§f§lTodos os Modos");
 /// public static List<String> lobby$leaderboard$points$hologram = Arrays
    //  .asList("§a10. {name_10} §7- §a{stats_10}", "§a9. {name_9} §7- §a{stats_9}",
   //       "§a8. {name_8} §7- §a{stats_8}", "§a7. {name_7} §7- §a{stats_7}",
   ///       "§a6. {name_6} §7- §a{stats_6}",
   //       "§a5. {name_5} §7- §a{stats_5}", "§a4. {name_4} §7- §a{stats_4}",
    //      "§a3. {name_3} §7- §a{stats_3}", "§a2. {name_2} §7- §a{stats_2}",
    //      "§a1. {name_1} §7- §a{stats_1}", "",
    //      "§7Ranking de Pontos", "§f§lRanqueado");
  public static String lobby$npc$play$connect = "§aConectando...";
  public static String lobby$npc$deliveries$deliveries = "§c{deliveries} Entrega{s}";
  public static List<String> lobby$npc$deliveries$hologram = Arrays
      .asList("{deliveries}", "§bEntregador", "§e§lCLIQUE DIREITO");
  public static List<String> lobby$npc$stats$hologram = Arrays
      .asList("§6Estatísticas", "Total de Eliminações: §7%kCore_Gladiator_kills%", "Total de Vitórias: §7%kCore_Gladiator_wins%", "§e§lCLIQUE DIREITO");
  public static List<String> lobby$npc$play$solo$hologram = Arrays
      .asList("§bSolo", "§a{players} Jogadores");
  public static List<String> lobby$npc$play$dupla$hologram = Arrays
      .asList("§bDuplas", "§a{players} Jogadores");
  public static List<String> lobby$npc$play$ranked$hologram = Arrays
      .asList("§bRanked", "§a{players} Jogadores");
  public static String lobby$npc$deliveries$skin$value =
      "eyJ0aW1lc3RhbXAiOjE1ODM0NTc4OTkzMTksInByb2ZpbGVJZCI6IjIxMWNhN2E4ZWFkYzQ5ZTVhYjBhZjMzMTBlODY0M2NjIiwicHJvZmlsZU5hbWUiOiJNYXh0ZWVyIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85MWU0NTc3OTgzZjEzZGI2YTRiMWMwNzQ1MGUyNzQ2MTVkMDMyOGUyNmI0MGQ3ZDMyMjA3MjYwOWJmZGQ0YTA4IiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=";
  public static String lobby$npc$deliveries$skin$signature =
      "SXnMF3f9x90fa+FdP2rLk/V6/zvMNuZ0sC4RQpPHF9JxdVWYRZm/+DhxkfjCHWKXV/4FSTN8LPPsxXd0XlYSElpi5OaT9/LGhITSK6BbeBfaYhLZnoD0cf9jG9nl9av38KipnkNXI+cO3wttB27J7KHznAmfrJd5bxdO/M0aGQYtwpckchYUBG6pDzaxN7tr4bFxDdxGit8Tx+aow/YtYSQn4VilBIy2y/c2a4PzWEpWyZQ94ypF5ZojvhaSPVl88Fbh+StdgfJUWNN3hNWt31P68KT4Jhx+SkT2LTuDj0jcYsiuxHP6AzZXtOtPPARqM0/xd53CUHCK+TEF5mkbJsG/PZYz/JRR1B1STk4D2cgbhunF87V4NLmCBtF5WDQYid11eO0OnROSUbFduCLj0uJ6QhNRRdhSh54oES7vTi0ja3DftTjdFhPovDAXQxCn+ROhTeSxjW5ZvP6MpmJERCSSihv/11VGIrVRfj2lo9MaxRogQE3tnyMNKWm71IRZQf806hwSgHp+5m2mhfnjYeGRZr44j21zqnSKudDHErPyEavLF83ojuMhNqTTO43ri3MVbMGix4TbIOgB2WDwqlcYLezENBIIkRsYO/Y1r5BWCA7DJ5IlpxIr9TCu39ppVmOGReDWA/Znyox5GP6JIM53kQoTOFBM3QWIQcmXll4=";
  public static String lobby$npc$play$solo$skin$value =
      "eyJ0aW1lc3RhbXAiOjE1MjM1Njk3MjI0OTgsInByb2ZpbGVJZCI6IjdiM2QxNGQ2YzExZDRjODA5NTc1ZjI5ODczNGE0ZDFiIiwicHJvZmlsZU5hbWUiOiJUYWxvbkRldiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQwMTdhYmQ5ZjExZTlkZTM4ODBkNGM0OTAxODUzNTdiOGY4ZmY1NGM3MzA2Mzg2ZTgyYWQ1NjdhNTMwMzMifX19";
  public static String lobby$npc$play$solo$skin$signature =
      "i7k5tYkZ0CJ1hnGrGELLVXjIi0hfVVtg+c4a/iXP4wOwvAPj6tQtExFWgGaZYnYhN6ldcjJKUw13a/TRwHi4er4OceOlxBgqSvc0zzT7U4iZsEUuCwv7r9t6a+3MELqSQe3/bbX6WP6pDA9TRSVWaCTGpBtZfAYyrszk+VTowMjKrDB7r/kzrhE+h2rSozVcv4fUMGOd4m8xbTPlcvBatZ9OcHfZEpuoTpECUq3tWH3GIJi+Uxz3rTVl5rKJdKLOeUVXLpiLSgQ0jybMy705WlB0NWFbWFkY0mEQU7yca6keopEsGaQ+36yEtcE4hKYhibqW2sFhne/wIZh5arwyXVv/04twL/dpdiBwg4nqGEO60i+tQoF9RVWeCmIwJizEn3+WO6H2QogfCy+W1vNO65/HoHlhVbC6Y6nkUUQ8r0jtqz/sBQVAEBhFDjOQcdFucyjnO4LXrZPajdzJtBhkottBZDQZQlbFoZxC47WpQ+sktc51SWT2f3BzMowRKg08R8xpZxMTf+bB5OldilMuDPggXF/wVQU4+N9OFo1qYNxRPtM/7DCP8dtS7pwfhJkRhnQOfBVu7/mkNX1EM3mlMRzhEiUmqXfhL3SSyzTzqdTB76JgrRF92zuW+ouUlnXHe4hWiaWvRQ1XHB4fc+HOQ6/1RMYb4NItJFte1tjcQQs=";
  public static String lobby$npc$play$dupla$skin$value =
      "eyJ0aW1lc3RhbXAiOjE1MjM1Njk3MjI0OTgsInByb2ZpbGVJZCI6IjdiM2QxNGQ2YzExZDRjODA5NTc1ZjI5ODczNGE0ZDFiIiwicHJvZmlsZU5hbWUiOiJUYWxvbkRldiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQwMTdhYmQ5ZjExZTlkZTM4ODBkNGM0OTAxODUzNTdiOGY4ZmY1NGM3MzA2Mzg2ZTgyYWQ1NjdhNTMwMzMifX19";
  public static String lobby$npc$play$dupla$skin$signature =
      "i7k5tYkZ0CJ1hnGrGELLVXjIi0hfVVtg+c4a/iXP4wOwvAPj6tQtExFWgGaZYnYhN6ldcjJKUw13a/TRwHi4er4OceOlxBgqSvc0zzT7U4iZsEUuCwv7r9t6a+3MELqSQe3/bbX6WP6pDA9TRSVWaCTGpBtZfAYyrszk+VTowMjKrDB7r/kzrhE+h2rSozVcv4fUMGOd4m8xbTPlcvBatZ9OcHfZEpuoTpECUq3tWH3GIJi+Uxz3rTVl5rKJdKLOeUVXLpiLSgQ0jybMy705WlB0NWFbWFkY0mEQU7yca6keopEsGaQ+36yEtcE4hKYhibqW2sFhne/wIZh5arwyXVv/04twL/dpdiBwg4nqGEO60i+tQoF9RVWeCmIwJizEn3+WO6H2QogfCy+W1vNO65/HoHlhVbC6Y6nkUUQ8r0jtqz/sBQVAEBhFDjOQcdFucyjnO4LXrZPajdzJtBhkottBZDQZQlbFoZxC47WpQ+sktc51SWT2f3BzMowRKg08R8xpZxMTf+bB5OldilMuDPggXF/wVQU4+N9OFo1qYNxRPtM/7DCP8dtS7pwfhJkRhnQOfBVu7/mkNX1EM3mlMRzhEiUmqXfhL3SSyzTzqdTB76JgrRF92zuW+ouUlnXHe4hWiaWvRQ1XHB4fc+HOQ6/1RMYb4NItJFte1tjcQQs=";
  public static String lobby$npc$play$ranked$skin$value =
      "eyJ0aW1lc3RhbXAiOjE1MjM1Njk3MjI0OTgsInByb2ZpbGVJZCI6IjdiM2QxNGQ2YzExZDRjODA5NTc1ZjI5ODczNGE0ZDFiIiwicHJvZmlsZU5hbWUiOiJUYWxvbkRldiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQwMTdhYmQ5ZjExZTlkZTM4ODBkNGM0OTAxODUzNTdiOGY4ZmY1NGM3MzA2Mzg2ZTgyYWQ1NjdhNTMwMzMifX19";
  public static String lobby$npc$play$ranked$skin$signature =
      "i7k5tYkZ0CJ1hnGrGELLVXjIi0hfVVtg+c4a/iXP4wOwvAPj6tQtExFWgGaZYnYhN6ldcjJKUw13a/TRwHi4er4OceOlxBgqSvc0zzT7U4iZsEUuCwv7r9t6a+3MELqSQe3/bbX6WP6pDA9TRSVWaCTGpBtZfAYyrszk+VTowMjKrDB7r/kzrhE+h2rSozVcv4fUMGOd4m8xbTPlcvBatZ9OcHfZEpuoTpECUq3tWH3GIJi+Uxz3rTVl5rKJdKLOeUVXLpiLSgQ0jybMy705WlB0NWFbWFkY0mEQU7yca6keopEsGaQ+36yEtcE4hKYhibqW2sFhne/wIZh5arwyXVv/04twL/dpdiBwg4nqGEO60i+tQoF9RVWeCmIwJizEn3+WO6H2QogfCy+W1vNO65/HoHlhVbC6Y6nkUUQ8r0jtqz/sBQVAEBhFDjOQcdFucyjnO4LXrZPajdzJtBhkottBZDQZQlbFoZxC47WpQ+sktc51SWT2f3BzMowRKg08R8xpZxMTf+bB5OldilMuDPggXF/wVQU4+N9OFo1qYNxRPtM/7DCP8dtS7pwfhJkRhnQOfBVu7/mkNX1EM3mlMRzhEiUmqXfhL3SSyzTzqdTB76JgrRF92zuW+ouUlnXHe4hWiaWvRQ1XHB4fc+HOQ6/1RMYb4NItJFte1tjcQQs=";
  public static String ingame$broadcast$join = "{player} §eentrou na partida! §a[{players}/{max_players}]";
  public static String ingame$broadcast$leave = "{player} §csaiu da partida! §a[{players}/{max_players}]";
  public static String ingame$broadcast$starting = "§aO jogo começa em §f{time} §asegundo{s}.";
  public static String ingame$broadcast$suicide = "{name} §emorreu sozinho.";
  public static String ingame$broadcast$default_killed_message = "{name} §efoi abatido por {killer}";
  public static String ingame$broadcast$double_kill = "§e. §e§lDOUBLE KILL";
    public static String ingame$broadcast$triple_kill = "§e. §b§lTRIPLE KILL";
    public static String ingame$broadcast$quadra_kill = "§e. §6§lQUADRA KILL";
    public static String ingame$broadcast$monster_kill = "§e. §c§lMOOONSTER KILL";
  public static String ingame$broadcast$end = " \n§aO tempo acabou, não houve ganhadores.\n ";
  public static String ingame$broadcast$win$solo = " \n{name} §avenceu a partida!\n ";
  public static String ingame$broadcast$win$dupla = " \n{name} §avenceram a partida!\n ";
  public static String ingame$actionbar$killed = "§aRestam §c{alive} §ajogadores!";
  public static String ingame$titles$end$header = "";
  public static String ingame$titles$end$footer = "§aRestam {time} minuto{s}";
  public static String ingame$titles$death$header = "§c§lVOCE MORREU";
  public static String ingame$titles$death$footer = "§7Você agora é um espectador";
  public static String ingame$titles$win$header = "§a§lVITÓRIA";
  public static String ingame$titles$win$footer = "§7Você é o último de pé";
  public static String ingame$titles$lose$header = "§c§lFIM DE JOGO";
  public static String ingame$titles$lose$footer = "§7Você não foi vitorioso dessa vez";
  public static String ingame$messages$bow$hit = "{name} §aestá com §c{hp} §ade HP.";
  public static String ingame$messages$coins$base = " \n  §a{coins} coins ganhos nesta partida:\n {coins_win}{coins_kills}\n ";
  public static String ingame$messages$coins$win = "\n       §a+{coins} §fpor vencer o jogo";
  public static String ingame$messages$coins$kills = "\n       §a+{coins} §fpor realizar §c{kills} §fabate{s}";
  public static String ingame$messages$points$base = "\n  \n §a{points} pontos ganhos nesta partida:\n {points_win}{points_kills}\n ";
  public static String ingame$messages$points$win = "\n       §a+{points} §fpor vencer o jogo";
  public static String ingame$messages$points$kills = "\n       §a+{points} §fpor realizar §c{kills} §fabate{s}";
  
  public static void setupLanguage() {
    boolean save = false;
    KWriter writer = JavaGlad.getInstance().getWriter(CONFIG.getFile(),
        "kGladiator - Criado por D4RKK\nVersão da configuração: " + JavaGlad.getInstance()
            .getDescription().getVersion());
    for (Field field : Language.class.getDeclaredFields()) {
      if (field.getName().contains("$") && !Modifier.isFinal(field.getModifiers())) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");
        
        try {
          Object value;
          KWriter.YamlEntryInfo entryInfo = field.getAnnotation(KWriter.YamlEntryInfo.class);
          
          if (CONFIG.contains(nativeName)) {
            value = CONFIG.get(nativeName);
            if (value instanceof String) {
              value = StringUtils.formatColors((String) value).replace("\\n", "\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.formatColors((String) v).replace("\\n", "\n"));
                } else {
                  list.add(v);
                }
              }
              
              value = list;
            }
            
            field.set(null, value);
            writer.set(nativeName, new KWriter.YamlEntry(
                new Object[]{entryInfo == null ? "" : entryInfo.annotation(),
                    CONFIG.get(nativeName)}));
          } else {
            value = field.get(null);
            if (value instanceof String) {
              value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
                } else {
                  list.add(v);
                }
              }
              
              value = list;
            }
            
            save = true;
            writer.set(nativeName, new KWriter.YamlEntry(
                new Object[]{entryInfo == null ? "" : entryInfo.annotation(), value}));
          }
        } catch (ReflectiveOperationException e) {
          LOGGER.log(Level.WARNING, "Unexpected error on settings file: ", e);
        }
      }
    }
    if (save) {
      writer.write();
      CONFIG.reload();
      Bukkit.getScheduler().scheduleSyncDelayedTask(JavaGlad.getInstance(),
          () -> LOGGER.info("A config §6language.yml §afoi modificada ou criada."));
    }
  }
}