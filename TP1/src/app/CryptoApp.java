package app;

import java.io.File;
import java.util.Scanner;

import criptografia.*;

public class CryptoApp {
  public static void menu(Scanner scanner) {
    while (true) {
      System.out.println("Escolha qual tipo de criptografia deseja utilizar:");
      System.out.println("0) Vigenere");
      System.out.println("1) Cifra de Colunas");
      System.out.println("Digite qualquer outro valor para retornar.");

      int option = scanner.nextInt();

      switch (option) {
        case 0:
          System.out.println("Informe a chave que deseja utilizar:");
          String key = scanner.nextLine();
          cipher(scanner, new FileCipher(new Vigenere(key)));
          break;
        case 1:
          System.out.println("Informe a chave que deseja utilizar:");
          key = scanner.nextLine();
          cipher(scanner, new FileCipher(new ColumnCipher(key)));
          break;
        default:
          return;
      }
    }
  }

  private static void cipher(Scanner scanner, FileCipher selectedCipher) {
    while (true) {
      System.out.println("Escolha qual tipo de operação deseja utilizar:");
      System.out.println("0) Criptografar");
      System.out.println("1) Descriptografar");
      System.out.println("Digite qualquer outro valor para retornar.");

      int option = scanner.nextInt();
      switch (option) {
        case 0:
          selectedCipher.cipherFile(new File("resources/db/csgo_players.db"));
          System.out.println("Resultados disponíveis em resources/criptografia/cipher.db");
          break;
        case 1:
          selectedCipher.decipherFile(new File("resources/criptografia/cipher.db"));
          System.out.println("Resultados disponíveis em resources/criptografia/decipher.db");
          break;
        default:
          return;
      }
    }

  }
}
