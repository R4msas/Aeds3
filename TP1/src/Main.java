import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws Exception {
    FileHandler fh = new FileHandler(0, "TP1/resources/csgo_players_sorted.csv",
        "TP1/resources/csgo_players.db");

    try {
      FileWriter fileWriter = new FileWriter("./sorted.txt");
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      var a = fh.readFromCSV();
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
