package app;

import java.util.Scanner;

import fileHandler.IndexFileHandler;
import indexacao.hash.Hash;
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
      PlayerSort sort = new PlayerSort("csgo_players.db", "resources/db/");
      try {
        switch (option) {
          case 0:
            System.out.print("Informe quantos registros podem ser ordenados em memória principal: ");
            int distributionSize = scanner.nextInt();

            System.out.print("\nInforme quantos caminhos a operação terá: ");
            int numberFiles = scanner.nextInt();
            sort.balancedSort(numberFiles, distributionSize);
            break;
          case 1:
            System.out.print("Informe quantos registros podem ser ordenados em memória principal: ");
            distributionSize = scanner.nextInt();

            System.out.print("\nInforme quantos caminhos a operação terá: ");
            numberFiles = scanner.nextInt();
            sort.variableSizeSort(numberFiles, distributionSize);
            break;
          case 2:
            System.out.print("Informe quantos registros podem ser ordenados em memória principal: ");
            distributionSize = scanner.nextInt();

            System.out.print("\nInforme quantos caminhos a operação terá: ");
            numberFiles = scanner.nextInt();
            sort.heapSort(distributionSize, numberFiles);
          default:
            IndexFileHandler indexFileHandler = new IndexFileHandler("resources/db/csgo_players.db",
                new Hash(7, "resources/db/", 20, true));
            indexFileHandler.buildIndexFromDB();
            return;
        }
      } catch (Exception e) {
        System.out.println("Erro manipulação dos arquivos");
        e.printStackTrace();
      }
    }
  }
}
