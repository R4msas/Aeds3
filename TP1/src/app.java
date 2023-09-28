import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import listaInvertida.*;
import model.*;

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
            System.out.println("3)Criar índice secundário rating");
            System.out.println("4)Atualizar um registro");
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
                 ListaPais listaPais=new ListaPais();
                 ArrayList<ListaPais> array=new ArrayList<ListaPais>();
                 array=listaPais.criaLista(caminhoDoArquivo);
                listaPais.ordenaLista(array);
                listaPais.criaIndiceSecundario(array);
                    break;
                
                    case 3:
                ListaRating listaRating=new ListaRating();
                ArrayList<ListaRating> arrayRating=new ArrayList<ListaRating>();
                arrayRating=listaRating.criaLista(caminhoDoArquivo);
                listaRating.ordenaLista(arrayRating);
                listaRating.criaIndiceSecundario(arrayRating);
                    break;
                /*
                    case 4:
                    break;
                case 5:
                    break;
                default:
                    break; */
            }
        }
        sc.close();
    }
}
