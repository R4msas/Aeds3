package Compressao;

import java.io.IOException;
import java.util.Scanner;

import Compressao.lzw.LZWPlayerFileCompresser;

public class Compressao {
    public static final String caminhoArquivo = "resources/db/csgo_players.db";

    public static void menu(Scanner scan) {
        while (true) {
            System.out.println(
                    "\nEscolha o tipo de compressão que deseja utilizar:\n0)Huffman\n1)LZW\nDigite qualquer outro valor para sair\n");
            int operacao = scan.nextInt();

            switch (operacao) {
                case 0:
                    huffmanMenu(scan);
                    break;
                case 1:
                    lzwMenu(scan);
                    break;
                default:
                    return;
            }
        }
    }

    private static void huffmanMenu(Scanner scanner) {
        while (true) {
            System.out.println(
                    "\nEscolha a operação que deseja realizar:\n" + "0)Compactar\n1)Descompactar\n"
                            + "Digite qualquer outro valor para sair\n");
            int operacao = scanner.nextInt();

            Huffman huffman = new Huffman();
            switch (operacao) {
                case 0:
                    try {
                        huffman.compressao();
                    } catch (Exception e) {
                        System.err.println("Ocorreu um erro acessando os arquivos durante a compressão.");
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        huffman.descompacta();
                    } catch (Exception e) {
                        System.err.println("Ocorreu um erro manipulando os arquivos durante a descompactação.");
                        e.printStackTrace();
                    }
                    break;
                default:
                    return;
            }
        }
    }

    private static void lzwMenu(Scanner scanner) {
        LZWPlayerFileCompresser lzw = new LZWPlayerFileCompresser();
        while (true) {

            System.out.println("\nEscolha a operação que deseja utilizar:\n" + "0)Criar novo compressor\n"
                    + "1)Compactar\n2)Compactar salvando números como Hexadecimal\n" + "3)Descompactar\n"
                    + "Digite qualquer outro valor para sair\n");
            int operation = scanner.nextInt();
            try {
                switch (operation) {
                    case 0:
                        lzw = new LZWPlayerFileCompresser();
                        break;
                    case 1:
                        long decimalTime = lzw.compressPlayerFile(caminhoArquivo, false);
                        System.out.println("Tempo de compactação: " + decimalTime + "ms"
                                + "\nTamanho do arquivo compactado: \n" + lzw.getCompressedFileSize()
                                + "\nTamanho arquivo compactado + tabela: " + lzw.getCombinedFilesSize() + "\n");
                        break;
                    case 2:
                        long hexTime = lzw.compressPlayerFile(caminhoArquivo, true);
                        System.out.println("\nTempo de compactação: " + hexTime + "ms"
                                + "\nTamanho do arquivo compactado: " + lzw.getCompressedFileSize()
                                + "\nTamanho arquivo compactado + tabela: " + lzw.getCombinedFilesSize() + "\n");
                        break;
                    case 3:
                        long time = lzw.discompressPlayerFile(caminhoArquivo);
                        System.out.println("Tempo de descompressão: " + time + "ms\n");
                        break;
                    default:
                        return;
                }
            } catch (IOException e) {
                System.err.println("Ocorreu um erro manipulando os arquivos.");
                e.printStackTrace();
            }
        }
    }
}
