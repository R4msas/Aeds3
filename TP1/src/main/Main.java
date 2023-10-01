package main;

import fileHandler.*;

import java.util.Scanner;

import app.DAOApp;
import app.FileHandlerApp;
import dao.*;
import hash.*;
import model.*;
import sort.*;

public class Main {
  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    Scanner scanner = new Scanner(System.in);
    FileHandlerApp.menu(scanner);
    DAOApp.menu(scanner);

    long endTime = System.currentTimeMillis();

    long executionTimeMillis = endTime - startTime;
    double executionTimeSeconds = executionTimeMillis / 1000.0; // Divide by 1000 to get seconds
    System.out.println("Execution time: " + executionTimeSeconds + " seconds");
  }
}
