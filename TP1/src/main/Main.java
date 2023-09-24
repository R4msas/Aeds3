package main;

import dao.*;
import hash.*;
import model.*;
import sort.*;

public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "resources/data/csgo_players_treated.csv",
        "resources/db/csgo_players.db");
    fh.csvToDBFile();

    PlayerSort sort = new PlayerSort("csgo_players.db", "resources/db/");
    sort.heapSort(7, 55);

    fh.buildHash("resources/db/", 40);

    var a = fh.readFromDB();
    for (Player player : a) {
      System.out.println(player);
    }
  }
}
