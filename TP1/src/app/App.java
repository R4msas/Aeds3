package app;

import java.util.Scanner;
import Compressao.*;
import casamentoDePadroes.*;

public class App {
  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Olá! Seja bem-vindo(a) ao nosso trabalho de AEDS3");
    while (true) {
      System.out.println("\nEscolha a operação a realizar:");
      System.out.println("0 - Criar ou ler os arquivos binários");
      System.out.println("1 - Operações de CRUD");
      System.out.println("2 - Ordenação externa");
      System.out.println("3 - Operações com Lista Invertida e árvore B");
      System.out.println("4 - Casamento de padrões");
      System.out.println("5 - Compressao");
      System.out.println("Digite qualquer outro valor para sair do programa\n");

      int option = scanner.nextInt();
      switch (option) {
        case 0:
          FileHandlerApp.menu(scanner);
          break;
        case 1:
          try {
            DAOApp.menu(scanner);
          } catch (Exception e) {
            System.out.println("Ocorreu um erro na manipulação dos arquivos");
            e.printStackTrace();
          }
          break;
        case 2:
          SortApp.menu(scanner);
          break;
        case 3:
          try {
            ArvoreEListaInvertida.menu(scanner);
          } catch (Exception e) {
            System.out.println("Ocorreu um erro manipulando as listas invertidas");
            e.printStackTrace();
          }
          break;
        case 4:
          CasamentoDePadroes.menu(scanner);
          break;
        case 5:
          Compressao.menu(scanner);
          break;

        default:
          scanner.close();
          return;
      }
    }
  }
}
