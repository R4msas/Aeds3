public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "TP1/resources/csgo_players_treated.csv", "TP1/resources/csgo_players.db");
    fh.csvToDBFile();
  }
}
