package main;

import dao.*;
import hash.*;
import model.*;
import sort.*;

public class Main {
  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    FileHandler fh = new FileHandler(0, "resources/data/csgo_players_treated.csv",
        "resources/db/csgo_players.db");
    fh.csvToDBFile();

    fh.buildHash("resources/db/", 20);
    long endTime = System.currentTimeMillis();

    long executionTimeMillis = endTime - startTime;
    double executionTimeSeconds = executionTimeMillis / 1000.0; // Divide by 1000 to get seconds
    System.out.println("Execution time: " + executionTimeSeconds + " seconds");
  }
}
