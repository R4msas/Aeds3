public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "TP1/resources/csgo_players_treated.csv",
        "TP1/resources/csgo_players.db");
    fh.csvToDBFile();

    PlayerSort playerSort = new PlayerSort("csgo_players.db", "TP1/resources/");
    playerSort.sort(4, 40);

    var array = fh.readFromDB();
    for (Player player : array) {
      System.out.println(player);
    }
  }
}
