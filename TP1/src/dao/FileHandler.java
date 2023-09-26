package dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import main.RAF;
import model.*;
import hash.*;

/**
 * Handles multiple objects and entire files.
 */
public class FileHandler {
  private int biggestID;
  private String csvFilePath;
  private String dbFilePath;

  public FileHandler() {
    biggestID = -1;
    csvFilePath = "";
    dbFilePath = "";
  }

  public FileHandler(int biggestID, String csvFilePath, String dbFilePath) {
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
    RAF raf = new RAF(dbFilePath, "r");
    biggestID = raf.readInt();

    ArrayList<Player> players = new ArrayList<>();
    while (raf.canRead()) {
      PlayerRegister pr = new PlayerRegister();
      Player player = pr.fromFile(raf, true);
      players.add(player);
    }

    raf.close();
    return players.toArray(new Player[0]);
  }

  public PlayerRegister[] readRegisters() throws IOException {
    RAF raf = new RAF(dbFilePath, "r");
    biggestID = raf.readInt();

    ArrayList<PlayerRegister> players = new ArrayList<>();
    while (raf.canRead()) {
      PlayerRegister pr = new PlayerRegister();
      pr.fromFile(raf, true);
      players.add(pr);
    }

    raf.close();
    return players.toArray(new PlayerRegister[0]);
  }

  public PlayerDAO csvToDBFile() throws Exception {
    toDBFile(readFromCSV());

    return new PlayerDAO(dbFilePath);
  }

  public void toDBFile(Player[] players) throws IOException {
    RAF raf = new RAF(dbFilePath, "rw");
    raf.writeInt(biggestID);

    for (Player player : players) {
      PlayerRegister pr = new PlayerRegister(false, player);
      raf.write(pr.toByteArray());
    }

    raf.close();
  }

  public HashDAO buildHash(String filePath, int bucketSize) throws IOException {
    Hash hash = new Hash(filePath, bucketSize);
    PlayerRegister[] playerRegisters = readRegisters();

    for (PlayerRegister playerRegister : playerRegisters) {
      hash.insert(playerRegister);
    }

    return new HashDAO(dbFilePath, hash);
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