package Compressao;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import Compressao.lzw.LZWPlayerFileCompresser;

public class Compressao {
    public static final String caminhoArquivo = "resources/db/csgo_players.db";
    public static final long tamanhoArquivoOriginal = new File(caminhoArquivo).length();

    public static void menu(Scanner scan) {
        while (true) {
            System.out.println(
                    "\nEscolha o tipo de compressão que deseja utilizar:\n0)Huffman\n1)LZW\n2)Comparar Eficiência\n"
                            + "Digite qualquer outro valor para sair\n");
            int operacao = scan.nextInt();

            switch (operacao) {
                case 0:
                    huffmanMenu(scan);
                    break;
                case 1:
                    lzwMenu(scan);
                    break;
                case 2:
                    try {
                        comparationMenu(scan);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                                + "\nTamanho do arquivo compactado: \n" + lzw.getCompressedFileSize() + "B\n");
                        break;
                    case 2:
                        long hexTime = lzw.compressPlayerFile(caminhoArquivo, true);
                        System.out.println("\nTempo de compactação: " + hexTime + "ms"
                                + "\nTamanho do arquivo compactado: " + lzw.getCompressedFileSize() + "B\n");
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

    private static void comparationMenu(Scanner scanner) throws Exception {
        LZWPlayerFileCompresser lzw = new LZWPlayerFileCompresser();
        Huffman huffman = new Huffman();

        while (true) {
            System.out.println(
                    "Escolha a operação que deseja realizar:\n" + "0)Compactar\n1)Descompactar\n"
                            + "Digite qualquer outro valor para voltar\n");
            int option = scanner.nextInt();

            switch (option) {
                case 0:
                    long lzwCompressionTime = lzw.compressPlayerFile(caminhoArquivo, false);
                    System.out.println("\nTempo gasto com LZW: " + lzwCompressionTime + "ms");
                    long huffmanCompressionTime = huffman.compressao();
                    System.out.println("Tempo gasto com Huffman: " + huffmanCompressionTime + "ms\n");

                    String fastestCompressionMethod = "Não houve";
                    long fastestCompressionTime = lzwCompressionTime;
                    if (lzwCompressionTime < huffmanCompressionTime) {
                        fastestCompressionMethod = "LZW";
                        fastestCompressionTime = lzwCompressionTime;
                    } else if (lzwCompressionTime > huffmanCompressionTime) {
                        fastestCompressionMethod = "Hufmann";
                        fastestCompressionTime = huffmanCompressionTime;
                    }

                    long lzwSize = lzw.getCompressedFileSize();
                    System.out.println("Espaço gasto com LZW: " + lzwSize + " bytes");

                    long huffmanSize = huffman.getFileSize();
                    System.out.println("Espaço gasto com Huffman: " + huffmanSize + " bytes");

                    String mostEfficientMethod = "Não houve";
                    long smallestFileSize = lzwSize;
                    if (lzwCompressionTime < huffmanCompressionTime) {
                        mostEfficientMethod = "LZW";
                        smallestFileSize = lzwSize;
                    } else if (lzwCompressionTime > huffmanCompressionTime) {
                        mostEfficientMethod = "Hufmann";
                        smallestFileSize = huffmanSize;
                    }

                    System.out.println("\nO algoritmo de compressão mais veloz foi " + fastestCompressionMethod
                            + ", que gastou " + ((double) fastestCompressionTime / 1000) + " segundos.");
                    System.out.println("\nO algoritmo de compressão mais eficiente foi " + mostEfficientMethod
                            + ", que gastou " + ((double) smallestFileSize / 1024) + " KB.");

                    float taxaCompressao = (float) (1 - ((float) smallestFileSize / tamanhoArquivoOriginal));
                    System.out.println("A taxa de compressão resultante foi de: " + (taxaCompressao * 100) + "%\n");
                    break;

                case 1:
                    long lzwDiscompressionTime = lzw.discompressPlayerFile(caminhoArquivo);
                    System.out.println("Tempo gasto com LZW: " + lzwDiscompressionTime + "ms");
                    long huffmanDiscompressionTime = huffman.descompacta();
                    System.out.println("Tempo gasto com Huffman: " + huffmanDiscompressionTime + "ms");

                    String fastestDiscompressionMethod = "Não houve";
                    long fastestDiscompressionTime = lzwDiscompressionTime;
                    if (lzwDiscompressionTime < huffmanDiscompressionTime) {
                        fastestDiscompressionMethod = "LZW";
                        fastestCompressionTime = huffmanDiscompressionTime;
                    } else if (lzwDiscompressionTime > huffmanDiscompressionTime) {
                        fastestDiscompressionMethod = "Hufmann";
                        fastestCompressionTime = huffmanDiscompressionTime;
                    }
                    System.out.println("O algoritmo de compressão mais veloz foi " + fastestDiscompressionMethod
                            + ", que gastou " + ((double) fastestDiscompressionTime / 1000) + " segundos.");
                    break;
                default:
                    return;
            }

        }
    }
}
