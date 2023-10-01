package app;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import listaInvertida.ListaPais;
import listaInvertida.ListaTime;
import model.*;
import arvoreB.*;
import java.io.File;

public class ArvoreEListaInvertida {
    public static void menu(Scanner sc) throws Exception
    {
        int menu = 0;
        while (menu != 42)
        {
            System.out.println("Escolha a opcao:");
            System.out.println("1)Criar a árvore B");
            System.out.println("2)Criar índice secundário país");
            System.out.println("3)Criar índice secundário time");
            System.out.println("4)Deletar a partir de um índice secundário:");
            System.out.println("5)Procurar a partir de um índice secundário");
            System.out.println("6)Procurar em uma árvore B:");
            System.out.println("7)Update árvore B:");
            System.out.println("8)Imprimir toda a árvore B:");
            System.out.println("42)Para sair do menu");
            menu = sc.nextInt();
            String caminhoDoArquivo = "resources/db/csgo_players.db";
            String caminhoIndice = "resources/arvoreB/indice.db";
            switch (menu)
            {
                // este método insere todos os jogadores do arquivo de dados na árvore B.
                case 1:
                    ArvoreB arv = new ArvoreB();
                    RandomAccessFile arqDados = new RandomAccessFile(caminhoDoArquivo, "r");
                    File index = new File(caminhoIndice);
                    index.delete();// apaga o índice anterior
                    arqDados.skipBytes(4);
                    while (arqDados.getFilePointer() < arqDados.length())
                    {

                        PlayerRegister playerRegister = new PlayerRegister();
                        playerRegister.fromFile(arqDados, true);
                        if (playerRegister != null)
                        {
                            arv.inserir(playerRegister);
                        }
                        // arv.imprimeTodaAArvore(); //este método é só para demonstrar a árvore
                        // sendo efetuada, se quiser testar só
                    }
                    arqDados.close();
                    break;
                // cria o índice de país, se já existir, apaga o índice anterior;
                case 2:
                    ListaPais listaPais = new ListaPais();
                    listaPais.criaIndiceSecundario(caminhoDoArquivo);
                    break;
                // cria o índice de time, se já existir, apaga o íncide anterior
                case 3:
                    ListaTime listaTime = new ListaTime();
                    listaTime.criaIndiceSecundario(caminhoDoArquivo);
                    break;

                // deleta de um time ou país
                case 4:
                    System.out.println("Deseja deletar por time(0) ou Pais(1)");
                    int op = sc.nextInt();
                    boolean deletado = false;
                    ArrayList<Player> lista = new ArrayList<>();
                    int idRemover = 0;
                    listaTime = new ListaTime();
                    listaPais = new ListaPais();
                    switch (op)
                    {
                        case (0):
                            lista = listaTime.procura();
                            listaTime.imprime(lista);
                            System.out.println("Escolha o id a ser removido");
                            idRemover = sc.nextInt();
                            for (Player l : lista)
                            {
                                if (l.getPlayerId() == idRemover)
                                {
                                    deletado = listaTime.delete(l);
                                    listaPais.delete(l);// a remoção deve ocorrer em ambos os
                                                        // índices
                                    break;
                                }
                            }
                            break;
                        case (1):
                            lista = listaPais.procura();
                            listaPais.imprime(lista);
                            System.out.println("Escolha o id a ser removido");
                            idRemover = sc.nextInt();
                            for (Player l : lista)
                            {
                                if (l.getPlayerId() == idRemover)
                                {
                                    deletado = listaPais.delete(l);
                                    listaTime.delete(l);
                                    break;
                                }
                            }
                            break;
                        default:
                            System.out.println("Opção incorreta!!!!");
                            break;
                    }


                    if (deletado)
                    {
                        System.out.println("Jogador de id " + idRemover + " deletado.");
                    } else
                    {
                        System.out.println("Id não encontrado.");
                    }
                    break;
                // faz a busca composta
                case 5:
                    Scanner scan = new Scanner(System.in);
                    System.out.println("1)Procura por pais:");
                    System.out.println("2)Procura por time:");
                    System.out.println("3)Procura por time e pais:");
                    ArrayList<Player> resp = new ArrayList<>();
                    int opcao = scan.nextInt();

                    switch (opcao)
                    {
                        case (1):
                            listaPais = new ListaPais();
                            resp = listaPais.procura();
                            listaPais.imprime(resp);
                            if (resp.size() == 0)
                            {
                                System.out.println("Não há jogador desta nacionalidade");
                            } else
                            {
                                listaPais.imprime(resp);
                            }

                            break;
                        case (2):
                            listaTime = new ListaTime();
                            resp = listaTime.procura();
                            if (resp.size() == 0)
                            {
                                System.out.println("Não há jogador destfe time");
                            } else
                            {
                                listaTime.imprime(resp);
                            }
                            break;
                        case (3):
                            listaPais = new ListaPais();
                            listaTime = new ListaTime();
                            ArrayList<Player> paises = listaPais.procura();
                            ArrayList<Player> times = listaTime.procura();
                            resp = listaPais.join(paises, times);
                            if (resp.size() == 0)
                            {
                                System.out.println("Não há jogador desta nacionalidade e time");
                            } else
                            {
                                listaTime.imprime(resp);
                            }
                    }
                    break;
                // faz a busca por Id na árvore B
                case (6):
                    System.out.println("Informe o id que deseja buscar na árvore B:");
                    op = sc.nextInt();
                    arv = new ArvoreB();
                    Player p = arv.procura(op);
                    System.out.println(p);
                    break;
                // efetua a atualização de um índice na árvore Bs
                case (7):
                    arv = new ArvoreB();
                    System.out.println("informe o novo endereço e o id a ser atualizado");
                    long pos = sc.nextLong();
                    int id = sc.nextInt();
                    if (arv.update(id, pos))
                    {
                        System.out.println("O Jogador de id " + id
                                + " teve o seu endereço no arquivo de dados alterado");
                    } else
                    {
                        System.out.println("Não foi possível efetuar a exclusão");
                    }
                    break;
                    case(8):
                    ArvoreB arvore=new ArvoreB();
                    arvore.imprimeTodaAArvore();
                    break;
                default:
                    break;
            }
        }
    }
}
