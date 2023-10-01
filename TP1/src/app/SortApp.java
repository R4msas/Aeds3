package app;

import java.util.Scanner;

import sort.PlayerSort;

public class SortApp {
  public static void menu(Scanner scanner) {
    while (true) {
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - Ordenação por Intercalação Balanceada");
      System.out.println("1 - Ordenação por Intercalação de Tamanho Variável");
      System.out.println("2 - Ordenação por Seleção por Substituição");
      System.out.println("Digite qualquer outro valor para retornar\n");

      int option = scanner.nextInt();

      System.out.print("Informe quantos registros podem ser ordenados em memória principal: ");
      int distributionSize = scanner.nextInt();

      System.out.println("\nInforme quantos caminhos a operação terá: ");
      int numberFiles = scanner.nextInt();

      PlayerSort sort = new PlayerSort("csgo_players.db", "resources/db/");

      try {
        switch (option) {
          case 0:
            sort.balancedSort(numberFiles, distributionSize);
            break;
          case 1:
            sort.variableSizeSort(numberFiles, distributionSize);
            break;
          case 2:
            sort.heapSort(distributionSize, numberFiles);
          default:
            return;
        }
      } catch (Exception e) {
        System.out.println("Erro manipulação dos arquivos");
        e.printStackTrace();
      }
    }
  }
}
