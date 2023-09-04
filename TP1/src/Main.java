public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "../resources/csgo_players_treated.csv",
        "../resources/csgo_players.db");
    fh.csvToDBFile();

    Ordenacao ordenacao = new Ordenacao(2);
    ordenacao.sort();

    var array = fh.readFromDB();
    for (Player player : array) {
      System.out.println(player);
    }
  }

  /*
   * public static void main(String[] args) throws IOException {
   * FileHandler fh = new FileHandler(0, "", "../resources/inputFiles1.db");
   * var a = fh.readFromDB();
   * for (Player player : a) {
   * System.out.println(player);
   * }
   * System.out.println(a.length);
   * }
   */
}
