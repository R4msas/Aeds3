import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main
 */
public class Main {
  public static String header;
  
  public static void main(String[] args) throws Exception {
    toDBFile("TP1/resources/csgo_players_treated.csv", "TP1/resources/csgo_players.db");
    Player[] players = readFromDB("TP1/resources/csgo_players.db");
    for (Player player : players) {
      System.out.println(player.toString());
    }
    System.out.println(players.length);
  }

  public static Player[] readFromCSV(String csvFilePath) throws Exception {
    Scanner scanner = new Scanner(new File(csvFilePath));
    header = scanner.nextLine();

    ArrayList<Player> array = new ArrayList<>();
    while (scanner.hasNext()) {
      Player temp = new Player();
      temp.fromCSVLine(scanner.nextLine());
      array.add(temp);
    }
    scanner.close();

    return array.toArray(new Player[0]);
  }

  public static Player[] readFromDB(String dbFilePath) throws IOException {
    RandomAccessFile arq = new RandomAccessFile(dbFilePath, "r");

    ArrayList<Player> players = new ArrayList<>();
    try {
      while (true) {
        int registerLength = arq.readInt();
        byte[] byteArray = new byte[registerLength];
        arq.read(byteArray);

        Player temp = new Player();
        temp.fromByteArray(byteArray);
        players.add(temp);
      }
    } catch (EOFException e) {
      /* Nothing to see here. */
    }

    arq.close();
    return players.toArray(new Player[0]);

  }

  public static void toDBFile(Player[] players, String dbFilePath) throws IOException {
    RandomAccessFile arq = new RandomAccessFile(dbFilePath, "rw");

    for (Player player : players) {
      var ba = player.toByteArray();
      arq.writeInt(ba.length);
      arq.write(ba);
    }

    arq.close();
  }

  public static void toDBFile(String csvFilePath, String dbFilePath) throws Exception {
    toDBFile(readFromCSV(csvFilePath), dbFilePath);
  }
}