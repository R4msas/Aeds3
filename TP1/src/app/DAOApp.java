package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import dao.IndexDAO;
import dao.PlayerDAO;
import hash.Hash;
import model.Player;

public class DAOApp {
  public static void menu(Scanner scanner) throws FileNotFoundException {
    PlayerDAO dao = new PlayerDAO("resources/db/csgo_players.db");

    while (true) {
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - CRUD Sequencial");
      System.out.println("1 - CRUD Indexado");
      System.out.println("Digite qualquer outro valor para retornar\n");

      int entrada = scanner.nextInt();
      switch (entrada) {
        case 0:
          crud(scanner, dao);
          break;
        case 1:
          try {
            IndexDAO indexDAO = daoDefinition(scanner, dao);
            if (indexDAO != null) {
              crud(scanner, indexDAO);
            }
          } catch (IOException e) {
            System.out.println("Erro manipulando os arquivos de indexação.");
            e.printStackTrace();
          }
          break;
        default:
          return;
      }
    }
  }

  private static IndexDAO daoDefinition(Scanner scanner, PlayerDAO dao) throws IOException {
    System.out.println("\nQual tipo de índice será utilizado?");
    System.out.println("0 - Hash");
    System.out.println("1 - Árvore B");
    System.out.println("Digite qualquer outro valor para retornar\n");

    int entrada = scanner.nextInt();
    switch (entrada) {
      case 0:
        System.out.println("A indexação será feita utilizando hash.");
        return new IndexDAO(dao, new Hash(entrada, "resources/db/", entrada, false));
      case 1:
        // return new = new ArvoreB();
        return null;
      default:
        return null;
    }
  }

  private static void crud(Scanner scanner, PlayerDAO playerDAO) {
    while (true) {
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - Inserir um jogador");
      System.out.println("1 - Ler um jogador");
      System.out.println("2 - Atualizar um jogador");
      System.out.println("3 - Apagar jogador");
      System.out.println("Digite qualquer outro valor para retornar\n");

      int entrada = scanner.nextInt();
      switch (entrada) {
        case 0:
          Player informedPlayer = ModelApp.createPlayer(scanner);
          try {
            System.out.println("Jogador inserido com id = " + playerDAO.create(informedPlayer));

          } catch (IOException e) {
            System.out.println("Erro manipulando o arquivo de inserção.");
            e.printStackTrace();
          }
          break;
        case 1:
          System.out.print("Informe o id que deseja ler: ");
          try {
            System.out.println(playerDAO.read(scanner.nextInt()));

          } catch (IOException e) {
            System.out.println("Erro manipulando o arquivo de leitura.");
            e.printStackTrace();
          }
          break;
        case 2:
          try {
            Player oldPlayer = null;
            do {
              System.out.print("Informe o id que deseja atualizar: ");
              oldPlayer = playerDAO.read(scanner.nextInt());

              if (oldPlayer == null) {
                System.out.println("Jogador não encontrado.");
              }
            } while (oldPlayer == null);

            System.out.println(
                "Jogador inserido na posição = 0x" + playerDAO.update(ModelApp.updatePlayer(scanner, oldPlayer)));

          } catch (Exception e) {
            System.out.println("Erro manipulando o arquivo de atualização.");
            e.printStackTrace();
          }

          break;
        case 3:
          try {
            System.out.print("Informe o id do jogador que deseja apagar: ");
            System.out.println("A deleção do jogador foi bem sucedida? " + playerDAO.delete(scanner.nextInt()));
          } catch (IOException e) {
            System.out.println("Erro manipulando o arquivo de atualização.");
            e.printStackTrace();
          }
          break;
        default:
          return;
      }
    }
  }
}
