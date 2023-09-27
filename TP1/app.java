import java.util.Scanner;

public class app {
    public static void main(String[] args)
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
                    RandomAccessFile arqDados=new RandomAccessFile("csgo_players.db","r");
                    while()
                    arv.inserir();
                    break;
                case 2:
                    arv.inserir();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    break;
            }
        }
    }
}
