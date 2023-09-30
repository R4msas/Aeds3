import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import listaInvertida.*;
import model.*;
import arvoreB.*;

public class app {
    public static void main(String[] args)throws Exception
    {
        int menu = 0;
        while (menu != 42)
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("Escolha a opcao:");
            System.out.println("1)Criar a árvore B");
            System.out.println("2)Criar índice secundário país");
            System.out.println("3)Criar índice secundário time");
            System.out.println("4)Procurar um registro");
            System.out.println("5)Procurar a partir de um índice secundário");
            System.out.println("42)Para sair do menu");

            menu = sc.nextInt();
            String caminhoDoArquivo="resources/db/csgo_players.db";

            switch (menu)
            {
                case 1:
                    ArvoreB arv = new ArvoreB();
                    RandomAccessFile arqDados = new RandomAccessFile(caminhoDoArquivo, "r");
                    arqDados.skipBytes(4);
                    while (arqDados.getFilePointer() < arqDados.length())
                    {
                        PlayerRegister playerRegister = new PlayerRegister();
                        playerRegister.fromFile(arqDados,true);
                        if(playerRegister!=null)
                        {
                        arv.inserir(playerRegister);
                        }
                    }
                    arqDados.close();
                    break;
                case 2:
                 ListaPais listaPais= new ListaPais();
                 listaPais.criaIndiceSecundario(caminhoDoArquivo);
                    break;
                
                case 3:
                ListaTime listaTime=new ListaTime();
                listaTime.criaIndiceSecundario(caminhoDoArquivo);
                    break;
                
                case 4:
                    break;
                case 5:
                Scanner scan=new Scanner(System.in);
                System.out.println("1)Procura por pais:");
                System.out.println("2)Procura por time:");
                System.out.println("3)Procura por time e pais:");
                ArrayList<Player>resp=new ArrayList<>();
                int opcao=scan.nextInt();

                switch(opcao)
                    {
                        case (1):
                        listaPais=new ListaPais();
                        resp=listaPais.procura();
                        listaPais.imprime(resp);
                        break;
                        case(2):
                        listaTime=new ListaTime();
                        resp=listaTime.procura();
                        listaTime.imprime(resp);
                        break;
                        case(3):
                        listaPais=new ListaPais();
                        listaTime=new ListaTime();
                        ArrayList <Player> paises=listaPais.procura();
                        ArrayList <Player> times=listaTime.procura();
                        resp=listaPais.join(paises, times);
                        if(resp.size()==0)
                        {
                            System.out.println("Não há jogador desta nacionalidade e time");
                        }
                        else{
                        listaTime.imprime(resp);
                        }
                    }
                    break;
                default:
                    break; 
            }
        }
    }
}
