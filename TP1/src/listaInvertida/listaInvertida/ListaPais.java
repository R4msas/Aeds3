package listaInvertida;

import model.*;
import java.util.Scanner;
import main.RAF;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import hash.*;
import dao.*;

/*
 * Esta classe cria um índice de clustering de paises, cada índice secundário tem como nome do país
 * dos jogadores, dentro deste arquivo estarão os id's de cada jogador.
 */
public class ListaPais {

    private int id;
    private String pais;
    private String listaPaisesExistentes = "listaPaisesExistentes.txt";
    private String prefixo = "resources/indiceSecundario/pais/";
    private String arqHash = "resources/db/";
    private String arqPrincipal = "resources/db/csgo_players.db";

    public ListaPais(int id, String pais) {
        this.id = id;
        this.pais = pais;
    }

    public ListaPais() {
        id = -1;
        pais = null;
    }

    // Getters e Setters

    public int getId()
    {
        return id;
    }

    public String getPais()
    {
        return pais;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    //cria índice de dados, caso já exista um índice, apaga todos os arquivos e grava novamente.
    public void insere(PlayerRegister player) throws Exception
    {
        String nomePais = player.getPlayer().getCountry();
        if (PrecisaEscreverOPais(nomePais))
        {
            FileWriter arqPais = new FileWriter(prefixo + listaPaisesExistentes, true);
            arqPais.write(nomePais + ",");
            arqPais.close();
        }
        String nomeArq = prefixo + nomePais + ".db";
        RAF indice = new RAF(nomeArq, "rw");
        indice.movePointerToEnd();
        int id = player.getPlayer().getPlayerId();
        indice.writeInt(id);
        indice.close();
    }

    // método para manter a lista de paises sem nomes duplicados, o try/catch é para o primeiro
    // teste, caso o arquivo não exista
    public boolean PrecisaEscreverOPais(String nomePais) throws Exception
    {
        boolean resp = true;
        try
        {
            Scanner arqPaises = new Scanner(new File(prefixo + listaPaisesExistentes));
            String strCsv = arqPaises.nextLine();
            String paises[] = strCsv.split(",");

            for (int c = 0; c < paises.length; c++)
            {
                if (paises[c].equals(nomePais))
                {
                    resp = false;
                    break;
                }
            }
        } catch (Exception e)
        {
            resp = true;
        }
        return resp;
    }

    // este método lê todo o arquivo de dados e grava no arquivo id por id, gravando em um arquivo
    // que tem o nome do pais do jogador. Atualmente não faz a distinção se este índice já foi
    // gravado, portanto duas chamadas deste método criará um arquivo com jogadores duplicados.
    public void criaIndiceSecundario(String caminhoDoArquivo) throws Exception
    {
        apagaIndice();
        RAF arquivoIndice = new RAF(caminhoDoArquivo, "r");
        arquivoIndice.skipBytes(4);// primeira parte do arquivo é
        while (arquivoIndice.canRead())
        {
            PlayerRegister player = new PlayerRegister();
            player.fromFile(arquivoIndice, true);
            if (!player.isTombstone())
            {
                insere(player);
            }
        }
        arquivoIndice.close();
    }
    // lista-se os times no arquivo auxiliar, após escolhido, chama o método privado que efetuará a
    // busca pelo nome do país

    public ArrayList<Player> procura() throws Exception
    {
        Scanner arqPaises = new Scanner(new File(prefixo + listaPaisesExistentes));
        String strCsv = arqPaises.nextLine();
        String paises[] = strCsv.split(",");
        System.out.println("Escolha o número de pais que deseja pesquisar:");
        for (int c = 0; c < paises.length; c++)
        {
            System.out.println(c + ")" + paises[c]);
        }
        Scanner sc = new Scanner(System.in);
        int numPais = sc.nextInt();
        ArrayList<Player> resp = procura(paises[numPais]);
        arqPaises.close();
        return resp;

    }

    // as procuras no índice são feitas utilizando o hash para ter mais eficiência
    private ArrayList<Player> procura(String pais) throws Exception
    {
        ArrayList<Player> resp = new ArrayList<>();
        String nomeArquivo = prefixo + pais + ".db";
        RAF arquivo = new RAF(nomeArquivo, "r");
        arquivo.movePointerToStart();
        while (arquivo.canRead())
        {
            int id = arquivo.readInt();
            Hash indiceHash = new Hash(0, arqHash, 1, false);
            IndexDAO index = new IndexDAO(arqPrincipal, indiceHash);
            Player player = index.read(id);
            resp.add(player);
        }
        arquivo.close();
        return resp;
    }

    public void imprime(ArrayList<Player> lista)
    {
        for (Player l : lista)
        {
            System.out.print(l);
        }
    }

    // junção por força bruta por serem índices não ordenados
    public ArrayList<Player> join(ArrayList<Player> paises, ArrayList<Player> times)
    {
        ArrayList<Player> resp = new ArrayList<>();
        for (Player p : paises)
        {
            for (Player pl : times)
            {
                if (p.getPlayerId() == pl.getPlayerId())
                {
                    resp.add(p);
                }
            }
        }
        return resp;
    }

    // no método de deleção, as alteração somente persistem no índice secundário, deste modo,
    // alterações no arquivo de dados deverão chamar a função de deleção, do contrário, poderá haver
    // informações incongruentes
    // neste método são feitas as deleções, procura-se o id desejado, salva esta posição, grava o
    // último id nela, depois trunca-se o arquivo com um registro a menos.
    public boolean delete(Player player) throws Exception
    {
        boolean resp = false;
        String nomePais = player.getCountry();
        String nomeArquivo = prefixo + nomePais + ".db";
        RAF arquivo = new RAF(nomeArquivo, "rw");
        arquivo.movePointerToStart();
        while (arquivo.canRead())
        {
            long posicaoDeletada = arquivo.getFilePointer();
            int id = arquivo.readInt();
            if (id == player.getPlayerId())
            {
                long posicaoUltimo = arquivo.length() - 4;
                arquivo.seek(posicaoUltimo);
                int temp = arquivo.readInt();
                arquivo.seek(posicaoDeletada);
                arquivo.writeInt(temp);
                arquivo.setLength(posicaoUltimo);
                resp = true;
                break;
            }
        }
        arquivo.close();
        return resp;

    }
    //neste método, quando chamado pelo cria índice, apaga os índices secundários, para evitar duplicidade de lançamentos
    private void apagaIndice()throws Exception
    {
        try{
        Scanner arqPaises = new Scanner(new File(prefixo + listaPaisesExistentes));
        String strCsv = arqPaises.nextLine();
        String paises[] = strCsv.split(",");
        for(String s:paises)
        {
            File arquivo= new File(prefixo+s+".db");
            arquivo.delete();
        }}
        catch(Exception NoSuchElementException)
        {
            File arq=new File(prefixo+listaPaisesExistentes);
        }
    }
}   

