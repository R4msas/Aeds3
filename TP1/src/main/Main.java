package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import dao.FileHandler;
import model.Player;
import sort.PlayerSort;

public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "TP1/resources/data/csgo_players_treated.csv",
        "TP1/resources/db/csgo_players.db");
    fh.csvToDBFile();

    PlayerSort sort = new PlayerSort("csgo_players.db", "TP1/resources/db/");
    sort.heapSort(7, 55);

    try {
      FileWriter fileWriter = new FileWriter("./a.txt");
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      var a = fh.readFromDB();
      for (Player player : a) {
        bufferedWriter.write(player.toString());
        bufferedWriter.newLine(); // Add a newline after each line
      }

      // Close the BufferedWriter to ensure data is flushed and the file is properly
      // closed.
      bufferedWriter.close();

      System.out.println("Data written to the file successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
