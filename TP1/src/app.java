import java.io.RandomAccessFile;
import java.util.Scanner;

public class app {
    public static void main(String[] args)throws Exception
    {
        int menu = 0;
        Scanner sc = new Scanner(System.in);
        while (menu != -1)
        {
            System.out.println("Escolha a opcao:");
            System.out.println("1)Criar a árvore B");
            System.out.println("2)Inserir um registro");
            System.out.println("3)Deletar um Registro");
            System.out.println("4)Atualizar um registro");
            System.out.println("5)Procurar um registro");
            menu = sc.nextInt();
            switch (menu)
            {
                case 1:
                    ArvoreB arv = new ArvoreB();
                    RandomAccessFile arqDados = new RandomAccessFile("cs.db", "r");
                    arqDados.skipBytes(8);
                    while (arqDados.getFilePointer() < arqDados.length())
                    {
                        PlayerRegister playerRegister = new PlayerRegister();
                        playerRegister.fromFileIfNotTomb(arqDados);
                        if(playerRegister!=null)
                        {
                        arv.inserir(playerRegister);
                        }
                    }
                    arqDados.close();
                    break;
                /* case 2:
                    arv.inserir();
                    break;
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
