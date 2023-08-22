import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main
 */
public class Main {
  public static String header;

  public static Player[] readFromCSV(String csvFilePath) throws FileNotFoundException {
    Scanner scanner = new Scanner(new File(csvFilePath));
    scanner.useDelimiter(",");
    header = scanner.nextLine();

    ArrayList<Player> array = new ArrayList<>();
    while (scanner.hasNext()) {
      /* String index = */ scanner.next();
      String nickname = scanner.next();
      String team = getNextTeam(scanner);
      int playerId = scanner.nextInt();
      String birthDate = scanner.next();
      String country = scanner.next();
      float rating = Float.parseFloat(scanner.nextLine().substring(1)); // Substring removes the first character(comma)
      Player temp = new Player(nickname, team, playerId, birthDate, country, rating);
      array.add(temp);
    }
    scanner.close();

    return array.toArray(new Player[0]);
  }
  
  private static String getNextTeam(Scanner scanner) {
    String team = scanner.next();

    if (team.charAt(0) == '\"') {
      while (team.charAt(team.length() - 1) != '\"') {
        team += ',' + scanner.next().substring(1); // Substring removes the first character(space)
      }
      team = team.substring(1, team.length() - 1); // Remove quote and unquote
    }

    return team;
  }
  
  public static Player[] readFromDB(String dbFilePath) throws IOException {
    FileInputStream arq = new FileInputStream(dbFilePath);
    DataInputStream dis = new DataInputStream(arq);

    ArrayList<Player> players = new ArrayList<>();
    try {
      while (true) {
        int registerLength = dis.readInt();
        byte[] byteArray = new byte[registerLength];
        dis.read(byteArray);

        Player temp = new Player();
        temp.fromByteArray(byteArray);
        players.add(temp);
      }
    } catch (EOFException e) {
      /* Nothing to see here. */
    }

    dis.close();
    return players.toArray(new Player[0]);

  }

  public static void toDBFile(Player[] players, String dbFilePath) throws IOException {
    FileOutputStream arq = new FileOutputStream(dbFilePath);
    DataOutputStream dos = new DataOutputStream(arq);

    for (Player player : players) {
      var ba = player.toByteArray();
      dos.writeInt(ba.length);
      dos.write(ba);
    }

    dos.close();
    arq.close();
  }

  public static void toDBFile(String csvFilePath, String dbFilePath) throws FileNotFoundException, IOException {
    toDBFile(readFromCSV(csvFilePath), dbFilePath);
  }
}