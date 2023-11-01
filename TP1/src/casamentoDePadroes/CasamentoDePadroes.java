package casamentoDePadroes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import main.RAF;
import model.PlayerRegister;

public class CasamentoDePadroes {
    public static final String caminhoArquivo="resources/db/csgo_players.db";
    public static Scanner sc;
    public static void menu(Scanner scan) throws IOException
    {
        sc=scan;
        System.out.println("Escolha o tipo de Busca:\n0)sair\n1)BoyerMoore\n2)KMP");
        int operacao=sc.nextInt();
        System.out.println("Informe o padrão que deseja procurar:");
        String str=sc.nextLine();//não faço ideia porque o primeiro não está lendo
        str=sc.nextLine();
        char[]padrao=str.toCharArray();

        switch(operacao){
        case 0:
        System.out.println("Até mais");
        break;
        case 1:
        procuraPlayerBoyerMoore(padrao);
        break;
        case 2:
        break;
        default:
        System.out.println("Opção Incorreta:");
        menu(sc);
        break;}
    }
    public static void procuraPlayerBoyerMoore(char[] padrao) throws IOException{
        RAF arquivo=new RAF(caminhoArquivo, "r");
        arquivo.movePointerToStart();
        boolean imprimiu=false;
        PlayerRegister player=new PlayerRegister();
        while(arquivo.canRead())
        {
        player.fromFile(arquivo, true);
        char[] texto=player.toString().toCharArray();
        if(procuraPlayerBoyerMoore(padrao, texto)==true)
        {
            imprimiu=true;
            System.out.println(player.toString());
        }
        }
        if(imprimiu==false)
        {
            System.out.println("Não foi encontrado este padrão.");
        }   
    }
    private static boolean procuraPlayerBoyerMoore(char[] padrao, char[] texto){
        return BoyerMoore.findBoyerMoore(texto, padrao);
    }
}
