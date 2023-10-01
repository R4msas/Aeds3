package app;

import java.io.IOException;
import java.util.Scanner;

import fileHandler.CSVDBHandler;
import fileHandler.DBHandler;
import fileHandler.HashFileHandler;
import indexacao.hash.Bucket;
import indexacao.hash.Directory;
import indexacao.hash.Hash;
import model.Player;
import model.PlayerRegister;

public class FileHandlerApp {
  public static void menu(Scanner scanner) {
    DBHandler dbHandler = new DBHandler("resources/db/csgo_players.db");
    while (true) {
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - Ler arquivo CSV");
      System.out.println("1 - Criar arquivo DB a partir de CSV");
      System.out.println("2 - Criar arquivo DB indexado por hash");
      System.out.println("3 - Ler jogadores arquivo DB");
      System.out.println("4 - Ler registros arquivo DB");
      System.out.println("5 - Ler arquivos de índice Hash");
      System.out.println("Digite qualquer outro valor para retornar\n");

      int entrada = scanner.nextInt();
      switch (entrada) {
        case 0:
          readFromCSV(new CSVDBHandler(dbHandler, "resources/data/csgo_players_treated.csv"));
          break;
        case 1:
          csvToDBFile(new CSVDBHandler(dbHandler, "resources/data/csgo_players_treated.csv"));
          break;
        case 2:
          csvToIndexedDB(scanner, dbHandler);
          break;
        case 3:
          readPlayers(dbHandler);
          break;
        case 4:
          readRegisters(dbHandler);
          break;
        case 5:
          try {
            readIndexes(new HashFileHandler(dbHandler, new Hash(0, "resources/db/", 1, false)));
          } catch (IOException e) {
            System.out.println("Erro lendo o hash.");
            e.printStackTrace();
          }
          break;
        default:
          return;
      }
    }
  }

  private static void readFromCSV(CSVDBHandler csvdbHandler) {
    try {
      Player[] players = csvdbHandler.readFromCSV();
      for (Player player : players) {
        System.out.println(player + "\n");
      }
    } catch (Exception e) {
      System.out.println("Erro na leitura do arquivo CSV.");
      e.printStackTrace();
    }
  }

  private static void csvToDBFile(CSVDBHandler csvdbHandler) {
    try {
      csvdbHandler.csvToDBFile();
      System.out.println("Arquivo DB criado com sucesso.");
    } catch (IOException e) {
      System.out.println("Erro na escrita do arquivo DB.");
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Erro na leitura do arquivo CSV.");
      e.printStackTrace();
    }
  }

  private static void csvToIndexedDB(Scanner scanner, DBHandler dbHandler) {
    int size = -1;
    do {
      System.out.print("Defina o tamanho do bucket: ");
      size = scanner.nextInt();

      if (size <= 0 || size > 40) {
        System.out.println("O tamanho do bucket deve ser um número positivo menor que 41");
      }
    } while (size <= 0 || size > 40);

    try {
      dbHandler = new CSVDBHandler(dbHandler, "resources/data/csgo_players_treated.csv");
      ((CSVDBHandler) dbHandler).csvToDBFile();

      dbHandler = new HashFileHandler(dbHandler, new Hash(0, "resources/db/", size, true));
      ((HashFileHandler) dbHandler).buildIndexFromDB();

      System.out.println("Arquivos DB criado com sucesso.");
    } catch (IOException e) {
      System.out.println("Erro na manipuação dos arquivos DB.");
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Erro na leitura do arquivo CSV.");
      e.printStackTrace();
    }
  }

  private static void readPlayers(DBHandler dbHandler) {
    try {
      Player[] players = dbHandler.readFromDB();
      for (Player player : players) {
        System.out.println(player + "\n");
      }
    } catch (IOException e) {
      System.out.println("Erro na leitura do arquivo DB.");
      e.printStackTrace();
    }
  }

  private static void readRegisters(DBHandler dbHandler) {
    try {
      PlayerRegister[] players = dbHandler.readRegistersFromDB();
      for (PlayerRegister player : players) {
        System.out.println(player + "\n");
      }
    } catch (IOException e) {
      System.out.println("Erro na leitura do arquivo DB.");
      e.printStackTrace();
    }
  }

  private static void readIndexes(HashFileHandler hashFileHandler) {
    try {
      Directory[] directories = hashFileHandler.readDirectories();
      for (Directory directory : directories) {
        System.out.println(directory);
      }

      Bucket[] buckets = hashFileHandler.readBuckets();
      for (Bucket bucket : buckets) {
        System.out.println(bucket + "\n");
      }

    } catch (IOException e) {
      System.out.println("Erro na manipuação dos arquivos DB.");
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Erro na leitura do arquivo CSV.");
      e.printStackTrace();
    }
  }
}
