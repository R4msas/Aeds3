public class Main {
  public static void main(String[] args) throws Exception {
    try {
      PlayerService ps = new PlayerService(-1, "TP1/resources/csgo_players_treated.csv",
          "TP1/resources/csgo_players.db");
      ps.csvToDBFile();

      PlayerDAO dao = new PlayerDAO(ps);
      System.out.println(dao.read(20114).toString());
      dao.delete(20114);

      var players = ps.readFromDB();
      for (Player player : players) {
        System.out.println(player.toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
