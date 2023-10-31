package casamentoDePadroes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import main.RAF;
import model.PlayerRegister;

public class CasamentoDePadroes {
    public static final String caminhoArquivo="resources/db/csgo_players.db";
    public static Scanner sc;
    public static void menu(Scanner scan)
    {
        sc=scan;
        System.out.println("Escolha o tipo de Busca:\n0)sair\n1)BoyerMoore\n2)KMP");
        int operacao=sc.nextInt();
        System.out.println("Informe o padrão que deseja procurar:");
        String padrao=sc.nextLine();
        switch(operacao){
        case 0:
        System.out.println("Até mais");
        break;
        case 1:procuraPlayerBoyerMoore(padrao);
        break;
        case 2:
        break;
        default:
        System.out.println("Opção Incorreta:");
        menu(sc);
        break;}
    }
    public static void procuraPlayerBoyerMoore(String padrao) throws IOException{
        RAF arquivo=new RAF(caminhoArquivo, "r");
        arquivo.movePointerToStart();
        PlayerRegister player=new PlayerRegister();
        while(arquivo.canRead())
        {
        player.fromFile(arquivo, true);
        procuraPlayerBoyerMoore(padrao, player.toString());
        }
    }
    private static boolean procuraPlayerBoyerMoore(String padrao, String texto){
        boolean resp=false;
        while()
    }
}
