package Compressao;

import java.io.IOException;
import java.util.Scanner;

public class Compressao {
    public static final String caminhoArquivo="resources/db/csgo_players.db";
    public static Scanner sc;
    public static void menu(Scanner scan) throws Exception
    {
        sc=scan;
        System.out.println("Escolha o tipo de compressão:\n0)sair\n1)LZ\n2)Huffman");
        int operacao=sc.nextInt();
        switch(operacao){
        case 0:
        System.out.println("Até mais");
        break;
        case 1:
        break;
        case 2:
        Huffman hf=new Huffman();
        hf.compressao();
        break;
        default:
        menu(sc);
        break;}
    }
}
