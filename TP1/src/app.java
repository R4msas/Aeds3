import java.io.RandomAccessFile;
import java.util.Scanner;
import model.PlayerRegister;
import java.util.ArrayList;

public class app {
    public static void main(String[] args)throws Exception
    {
        int menu = 0;
        Scanner sc = new Scanner(System.in);
        while (menu != -1)
        {
            System.out.println("Escolha a opcao:");
            System.out.println("1)Criar a árvore B");
            System.out.println("2)Criar indice secundário país");
            System.out.println("3)Deletar um Registro");
            System.out.println("4)Atualizar um registro");
            System.out.println("5)Procurar um registro");
            menu = sc.nextInt();
            switch (menu)
            {
                case 1:
                    ArvoreB arv = new ArvoreB();
                    RandomAccessFile arqDados = new RandomAccessFile("cs.db", "r");
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
                 String caminhoDoArquivo="resources/db/csgo_players.db";
                 ListaPais lista=new ListaPais();
                 ArrayList<ListaPais> array=new ArrayList<ListaPais>();
                 array=lista.criaLista(caminhoDoArquivo);
                lista.ordenaLista(array);
                lista.criaIndiceSecundario(array);
                    break;
                /*
                    case 3:
                    break;
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
