public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "TP1/resources/csgo_players_treated.csv",
        "TP1/resources/csgo_players.db");
    fh.csvToDBFile();

    PlayerSort sort = new PlayerSort("csgo_players.db", "TP1/resources/");
    sort.sort(400);

    System.out.println(fh.readFromDB().length);
  }
}
