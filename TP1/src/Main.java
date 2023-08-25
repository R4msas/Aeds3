public class Main {
  public static void main(String[] args) throws Exception {
    try {
      PlayerService ps = new PlayerService(-1, "TP1/resources/csgo_players_treated.csv",
          "TP1/resources/csgo_players.db");
      ps.csvToDBFile();

      PlayerDAO dao = new PlayerDAO(ps);
      var strings = new String[1];
      strings[0] = "";

      Player foo = new Player("teste", strings, 1, "2001-07-27", "BRA", (float) 1.0);
      dao.create(foo);

      var players = ps.readFromDB();
      for (Player player : players) {
        System.out.println(player.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
