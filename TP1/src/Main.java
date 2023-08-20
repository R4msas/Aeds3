import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
      String team = scanner.next();
      if (team.charAt(0) == '\"') {
        while (team.charAt(team.length() - 1) != '\"') {
          String newTeam = scanner.next();
          newTeam = newTeam.charAt(0) == ' ' ? newTeam.substring(1) : newTeam;
          team += ',' + newTeam;
        }
        team = team.substring(1, team.length() - 1);
      }

      int playerId = scanner.nextInt();
      String birthDate = scanner.next();
      String country = scanner.next();
      float rating = Float.parseFloat(scanner.nextLine().substring(1));
      Player temp = new Player(nickname, team, playerId, birthDate, country, rating);
      array.add(temp);
    }
    scanner.close();

    return array.toArray(new Player[0]);
  }

  public static void main(String[] args) {
    try {
      Player[] array = readFromCSV("../resources/csgo_players_treated.csv");

      FileOutputStream arq = new FileOutputStream("../resources/jogadores_ds.db");
      DataOutputStream dos = new DataOutputStream(arq);
      //dos.writeUTF(header);

      for (Player player : array) {
        dos.writeUTF(player.getName());
        dos.writeUTF(player.getTeams());
        dos.writeInt(player.getPlayerId());
        dos.writeUTF(player.getBirthDate());
        dos.writeUTF(player.getCountry());
        dos.writeFloat(player.getRating());
      }

      dos.close();
      arq.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      FileInputStream arq = new FileInputStream("../resources/jogadores_ds.db");
      DataInputStream dis = new DataInputStream(arq);

      String nickname = dis.readUTF();
      String teams = dis.readUTF();
      int id = dis.readInt();
      String birthdate = dis.readUTF();
      String country = dis.readUTF();
      float rating = dis.readFloat();

      Player player = new Player(nickname, teams, id, birthdate, country, rating);
      System.out.println(player.getName() + ", " + player.getTeams() + ", " + player.getPlayerId() + ", " + player.getBirthDate() + ", " + player.getCountry() + ", " + player.getRating());

      arq.close();
      dis.close();

    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}