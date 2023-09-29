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
        Scanner sc = new Scanner(System.in);
        while (menu != -1)
        {
            System.out.println("Escolha a opcao:");
            System.out.println("1)Criar a árvore B");
            System.out.println("2)Criar índice secundário país");
            System.out.println("3)Criar índice secundário time");
            System.out.println("4)Procurar a partir de um índice secundário");
            System.out.println("5)Procurar um registro");
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
                ListaRating listaRating=new ListaRating();
                ArrayList<ListaRating> arrayRating=new ArrayList<ListaRating>();
                arrayRating=listaRating.criaLista(caminhoDoArquivo);
                listaRating.ordenaLista(arrayRating);
                listaRating.criaIndiceSecundario(arrayRating);
                    break;
                
                    case 4:
                    break;
                case 5:
                int opcao=-1;
                System.out.println("1)Procura por pais:");
                System.out.println("2)Procura por time:");
                System.out.println("3)Procura por time e pais:");
                ArrayList<Player>resp=new ArrayList<>();
                switch(opcao)
                    {
                        case (1):
                        listaPais=new ListaPais();
                        resp=listaPais.procura();
                        break;
                        case(2):
                        resp=lista
                    }
                    
                    break;
                default:
                    break; 
            }
        }
        sc.close();
    }
}
