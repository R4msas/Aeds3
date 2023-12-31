package arvoreB;

import java.util.ArrayList;
// import java.util.Scanner;
import model.*;
import main.*;

public class ArvoreB {
    private int numeroFilhos = 8;
    private Pagina raiz;
    private String prefixo = "resources/arvoreB/";
    private String indice = "indice.db";
    private String caminhoDoArquivo = "resources/db/csgo_players.db";

    public int getNumeroFilhos()
    {
        return numeroFilhos;
    }

    public void setNumeroFilhos(int numeroFilhos)
    {
        this.numeroFilhos = numeroFilhos;
    }

    public Pagina getRaiz()
    {
        return raiz;
    }

    public void setRaiz(Pagina raiz)
    {
        this.raiz = raiz;
    }

    public void delete(int id) throws Exception
    {
        raiz.delete(id);
    }

    public Player read(int id)
    {
        Player player = new Player();
        return player;
    }

    /**
     * Da chamada deste método, verifica-se se existe um arquivo índice já gravado, caso não tenha,
     * inicializa a árvore B, faz o lançamento manual do primeiro do primeiro arquivo na raiz, caso
     * seja ainda não haja nenhuma inserção.
     * 
     * @param player
     * @throws Exception
     */
    public void inserir(PlayerRegister player) throws Exception
    {
        try
        {
            RAF arquivo = new RAF(prefixo + indice, "rw");
            long posicao = arquivo.readLong();
            raiz = raiz.lerPaginaDoArquivo(posicao);
            arquivo.close();
            inserir(raiz, player);
        } catch (Exception e)
        {
            RAF arquivo = new RAF(prefixo + indice, "rw");
            raiz = new Pagina();
            Registro registro = new Registro();
            registro.setPonteiro(player.getPosition());
            registro.setId(player.getPlayer().getPlayerId());
            ArrayList<Registro> resp = new ArrayList<>();
            resp.add(registro);
            raiz.setRegistros(resp);
            ArrayList<Long> ponteiros = new ArrayList<Long>();
            ponteiros.add((long) -1);// inicialização manual dos dois primeiros ponteiros, nulos,
                                     // pois a raiz também é uma folha.
            ponteiros.add((long) -1);
            raiz.setPonteiros(ponteiros);
            arquivo.writeLong(8);// os oito primeiros bytes são reservados para a raiz
            raiz.setEnderecoDaPagina(8);// endereça a raiz no primeiro espaço
            arquivo.close();
            raiz.setNumeroRegistros(1);
            raiz.escreverPagina();// grava a raiz no primeiro endereço
        }



    }

    /**
     * Neste momento é testado se a raiz está cheia, caso estiver, é preciso fazer um split da raiz,
     * que retorna a página da nova raiz. Neste momento será atualizado o endereço da raiz, long no
     * começo do arquivo.
     * 
     * @param raiz
     * @param player
     * @throws Exception
     */
    private void inserir(Pagina raiz, PlayerRegister player) throws Exception
    {
        if (raiz.getNumeroRegistros() == raiz.getTamanhoMax())
        {
            raiz = raiz.splitRaiz();
            RAF arquivo = new RAF(prefixo + indice, "rw");
            arquivo.movePointerToStart();
            arquivo.writeLong(raiz.getEnderecoDaPagina());
            arquivo.close();
        }
        raiz.inserir(player);

    }

    /**
     * Método que faz o update do endereço no índice secundário da árvore B. Caso a página seja
     * nula, significa que não há um jogador com este id
     * 
     * @param id id do jogador
     * @param novoEndereco endereço que foi alterado
     */
    public boolean update(int id, long novoEndereco) throws Exception
    {
        boolean resp = true;
        raiz = new Pagina();
        RAF arquivo = new RAF(prefixo + "indice.db", "r");
        arquivo.movePointerToStart();
        long endereco = arquivo.readLong();
        arquivo.close();
        raiz = raiz.lerPaginaDoArquivo(endereco);
        Pagina pagina = raiz.procura(id);
        if (pagina == null)
        {
            resp = false;
        } 
        else
        {
            int pos = pagina.encontraPosicao(id);
            Registro registro = new Registro(novoEndereco, id);
            pagina.getRegistros().set(pos, registro);
            pagina.escreverPagina();
        }
        return resp;
    }


    /**
     * Em toda procura, a raiz é retirada do arquivo, neste momento é feita uma busca na árvore,
     * este método retorna o objeto do jogador.
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public Player procura(int id) throws Exception
    {
        raiz = new Pagina();
        RAF arquivo = new RAF(prefixo + "indice.db", "r");
        arquivo.movePointerToStart();
        long endereco = arquivo.readLong();
        arquivo.close();
        raiz = raiz.lerPaginaDoArquivo(endereco);
        Pagina pagina = raiz.procura(id);
        int pos = pagina.encontraPosicao(id);
        long posicaoDados = pagina.getRegistros().get(pos).getPonteiro();
        RAF arqDados = new RAF(caminhoDoArquivo, "r");
        arqDados.seek(posicaoDados);
        PlayerRegister player = new PlayerRegister();
        player.fromFile(arqDados, true);
        return player.getPlayer();
    }

    /**
     * Este método foi criado única e exclusivamente para debuggar as inserções na árvore.
     * 
     * @throws Exception
     */
    public void imprimeTodaAArvore() throws Exception
    {
        RAF arq = new RAF(prefixo + indice, "r");
        System.out.println("______________________________________________");
        System.out.println("Endereço da Raiz " + arq.readLong());
        while (arq.canRead())
        {
            System.out.println("Endereço desta página :" + arq.getFilePointer());
            System.out.println(
                    "Folha " + arq.readBoolean() + " Número de registros " + arq.readInt());
            for (int c = 0; c < numeroFilhos - 1; c++)
            {
                System.out.print(" endereço Página " + arq.readLong() + " id: " + arq.readInt()
                        + " endereço dados " + arq.readLong());
            }
            System.out.println(arq.readLong());
        }
        arq.close();
    }

}
