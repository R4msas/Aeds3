import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles multiple objects and entire files.
 */
public class PlayerService {
  private int biggestID;
  private String csvFilePath;
  private String dbFilePath;

  public PlayerService() {
    biggestID = -1;
    csvFilePath = "";
    dbFilePath = "";
  }

  public PlayerService(int biggestID, String csvFilePath, String dbFilePath) {
    this();
    setBiggestID(biggestID);
    setCSVFilePath(csvFilePath);
    setDBFilePath(dbFilePath);
  }

  @Override
  public String toString() {
    return "PlayerService" + "\nBiggest ID: " + this.biggestID + "\nCSV File Path : " + this.csvFilePath
        + "\nDB File Path: " + this.dbFilePath;
  }

  public Player[] readFromCSV() throws Exception {
    Scanner scanner = new Scanner(new File(csvFilePath));
    scanner.nextLine(); // To ignore header

    ArrayList<Player> array = new ArrayList<>();
    biggestID = -1;
    while (scanner.hasNext()) {
      Player temp = new Player();
      temp.fromCSVLine(scanner.nextLine());
      array.add(temp);

      biggestID = biggestID >= temp.getPlayerId() ? biggestID : temp.getPlayerId();
    }
    scanner.close();

    return array.toArray(new Player[0]);
  }

  public Player[] readFromDB() throws IOException {
    RandomAccessFile raf = new RandomAccessFile(dbFilePath, "r");
    biggestID = raf.readInt();

    ArrayList<Player> players = new ArrayList<>();
    try {
      while (true) {
        // TODO Refactoring
        raf.readBoolean();
        byte[] byteArray = new byte[raf.readInt()];
        raf.read(byteArray);

        Player temp = new Player();
        temp.fromByteArray(byteArray);
        players.add(temp);
      }
    } catch (EOFException e) {
      /* Nothing to see here. */
    }

    raf.close();
    return players.toArray(new Player[0]);

  }

  public void csvToDBFile() throws Exception {
    toDBFile(readFromCSV());
  }

  public void toDBFile(Player[] players) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw");
    raf.writeInt(biggestID);

    for (Player player : players) {
      raf.write(player.toByteArray());
    }

    raf.close();
  }

  public int getBiggestID() {
    return this.biggestID;
  }

  public void setBiggestID(int biggestID) {
    this.biggestID = biggestID;
  }

  public String getCSVFilePath() {
    return this.csvFilePath;
  }

  public void setCSVFilePath(String csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  public String getDBFilePath() {
    return this.dbFilePath;
  }

  public void setDBFilePath(String dbFilePath) {
    this.dbFilePath = dbFilePath;
  }
}