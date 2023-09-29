package main;

import fileHandler.*;
import dao.*;
import hash.*;
import model.*;
import sort.*;

public class Main {
  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    CSVDBHandler csvHandler = new CSVDBHandler("resources/db/csgo_players.db",
        "resources/data/csgo_players_treated.csv");
    csvHandler.csvToDBFile();

    HashFileHandler indexFileHandler = new HashFileHandler(csvHandler, new Hash(0, "resources/db/", 20, true));
    indexFileHandler.buildIndexFromDB();

    for (Directory directory : indexFileHandler.readDirectories()) {
      System.out.println(directory);
    }
    System.out.println(indexFileHandler.readDirectories().length);

    long endTime = System.currentTimeMillis();

    long executionTimeMillis = endTime - startTime;
    double executionTimeSeconds = executionTimeMillis / 1000.0; // Divide by 1000 to get seconds
    System.out.println("Execution time: " + executionTimeSeconds + " seconds");
  }
}
