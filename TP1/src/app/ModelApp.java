package app;

import java.util.Scanner;

import model.Player;

public class ModelApp {
  public static Player createPlayer(Scanner scanner) {
    System.out.println("Criando um jogador...");

    return new Player(getNickname(scanner), getTeams(scanner), 0, getBirthDate(scanner), getCountry(scanner),
        getRating(scanner));
  }

  public static Player updatePlayer(Scanner scanner, Player oldPlayer) {
    while (true) {
      System.out.println("\nJogador em atualização:\n" + oldPlayer);
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - Mudar nickname");
      System.out.println("1 - Mudar times");
      System.out.println("2 - Mudar data de nascimento");
      System.out.println("3 - Mudar país de origem");
      System.out.println("4 - Mudar rating");
      System.out.println("Digite qualquer outro valor para retornar\n");

      int entrada = scanner.nextInt();
      switch (entrada) {
        case 0:
          oldPlayer.setName(getNickname(scanner));
          break;
        case 1:
          oldPlayer.setTeams(getTeams(scanner));
          break;
        case 2:
          oldPlayer.setBirthDate(getBirthDate(scanner));
          break;
        case 3:
          oldPlayer.setCountry(getCountry(scanner));
          break;
        case 4:
          oldPlayer.setRating(getRating(scanner));
          break;
        default:
          return oldPlayer;
      }
    }
  }

  private static String getNickname(Scanner scanner) {
    System.out.print("\nInforme o nickname do jogador: ");
    return scanner.next();
  }

  private static String[] getTeams(Scanner scanner) {
    System.out.print("\nInforme a quantidade de times que esse jogador terá: ");
    int numberTeams = scanner.nextInt();

    System.out.println("\nInforme os times que esse jogador terá: ");
    String teams[] = new String[numberTeams];
    for (int i = 0; i < numberTeams; i++) {
      teams[i] = scanner.next();
    }

    return teams;
  }

  private static String getBirthDate(Scanner scanner) {
    System.out.print("\nInforme a data de nascimento do jogador (AAAA-MM-DD): ");
    return scanner.next();
  }

  private static String getCountry(Scanner scanner) {
    System.out.print("\nInforme o país de origem do jogador: ");
    return scanner.next();
  }

  private static Float getRating(Scanner scanner) {
    System.out.print("\nInforme o rating do jogador: ");
    return scanner.nextFloat();
  }
}
