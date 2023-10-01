package arvoreB;

import java.util.ArrayList;
import model.*;
import main.*;

class Pagina {

    private int numeroRegistros;
    private long enderecoDaPagina;
    private ArrayList<Registro> registros;
    ArrayList<Long> ponteiros;
    private boolean folha;
    private int tamanhoMax = 7;
    private String prefixo = "resources/arvoreB/";

    public long getEnderecoDaPagina()
    {
        return enderecoDaPagina;
    }

    public void setEnderecoDaPagina(long enderecoDaPagina)
    {
        this.enderecoDaPagina = enderecoDaPagina;
    }

    public boolean getFolha()
    {
        return folha;
    }

    public void setFolha(boolean folha)
    {
        this.folha = folha;
    }

    public int getTamanhoMax()
    {
        return tamanhoMax;
    }

    public void setTamanhoMax(int tamanhoMax)
    {
        this.tamanhoMax = tamanhoMax;
    }

    public int getNumeroRegistros()
    {
        return numeroRegistros;
    }

    public void setNumeroRegistros(int numeroRegistros)
    {
        this.numeroRegistros = numeroRegistros;
    }

    public ArrayList<Registro> getRegistros()
    {
        return registros;
    }

    public void setRegistros(ArrayList<Registro> registros)
    {
        this.registros = registros;
    }

    public void setPonteiros(ArrayList<Long> ponteiros)
    {
        this.ponteiros = ponteiros;
    } // contrutores

    public Pagina() {
        numeroRegistros = 0;
        registros = new ArrayList<Registro>();
        ponteiros = new ArrayList<Long>();
        folha = true;
    }

    /**
     * a fragmentação será na descida, sendo assim antes de passar para a próxima página,
     * verifica-se se é preciso fragmentar; As alterações nas páginas são gravadas no arquivo de
     * índice;
     * 
     * @param superior
     * @throws Exception
     */
    public void split(Pagina superior) throws Exception
    {
        int indiceQuebra = tamanhoMax / 2;// este é o meio do arraylist
        Pagina lateral = new Pagina();// página a direita da fragmentada
        lateral.setFolha(folha);
        lateral.setEnderecoDaPagina(buscaEnderecoLivreNaPilha());
        int posicao = indiceQuebra + 1;
        Registro regNaPagina = registros.get(indiceQuebra);
        Registro regNaSuperior = superior.registros.get(superior.numeroRegistros - 1);// última
                                                                                      // posição
        if (regNaSuperior.getId() < regNaPagina.getId())
        {
            superior.registros.add(regNaPagina);
            superior.ponteiros.add(lateral.getEnderecoDaPagina());
        } else
        {
            for (int c = 0; c < superior.registros.size(); c++)
            {
                regNaSuperior = superior.registros.get(c);
                if (regNaSuperior.getId() > regNaPagina.getId())
                {
                    superior.registros.add(c, regNaPagina);
                    superior.ponteiros.add(c + 1, lateral.enderecoDaPagina);
                    break;
                }
            }
        }
        while (posicao < registros.size())
        {
            lateral.registros.add(registros.remove(posicao));
            lateral.ponteiros.add(ponteiros.remove(posicao));
        }
        lateral.ponteiros.add(ponteiros.remove(posicao));
        registros.remove(indiceQuebra);
        lateral.setNumeroRegistros(lateral.registros.size());
        numeroRegistros = registros.size();
        superior.setNumeroRegistros(superior.registros.size());
        lateral.escreverPagina();
        superior.escreverPagina();
        this.escreverPagina();
    }

    /**
     * Este método reaproveita espaços vazio no arquivo de índice, pois toda página tem o mesmo
     * tamanho. Assim retira o último endereço disponível na pilha e trunca o arquivo em -8 bytes.
     * 
     * @return
     * @throws Exception
     */
    public long buscaEnderecoLivreNaPilha() throws Exception
    {
        long resp;
        RAF pilha = new RAF(prefixo + "pilhaLapide.db", "rw");
        if (pilha.length() < 8)
        {
            RAF arquivo = new RAF(prefixo + "indice.db", "rw");
            arquivo.movePointerToEnd();
            resp = arquivo.getFilePointer();
            arquivo.close();
        } else
        {
            pilha.seek(pilha.length() - 8);

            resp = pilha.readLong();
            pilha.seek(pilha.length() - 8);
            pilha.setLength(pilha.length() - 8);
        }
        pilha.close();

        return resp;
    }


    /**
     * Sempre que houver uma deleção, a página marcada para ser apagada é enviada para esta pilha,
     * 
     * @throws Exception
     */
    public void insereEnderecoNaPilha() throws Exception
    {
        RAF pilha = new RAF(prefixo + "pilhaLapide.db", "rw");
        pilha.movePointerToEnd();
        pilha.writeLong(enderecoDaPagina);
        pilha.close();
    }

    public void excluirPagina()
    {

    }

    public void excluirFolha()
    {

    }

    /**
     * Quando cheia, a raiz é dividida em duas novas páginas, este método retorna uma página, para
     * que o método ArvoreB, grave-a no início do arquivo de índice.
     * 
     * @return A página que será a nova raiz.
     * @throws Exception
     */
    public Pagina splitRaiz() throws Exception
    {
        int indiceQuebra = tamanhoMax / 2;
        Pagina lateral = new Pagina();
        Pagina superior = new Pagina();
        lateral.setEnderecoDaPagina(buscaEnderecoLivreNaPilha());
        lateral.setFolha(folha);
        superior.setFolha(false);
        superior.registros.add(registros.get(indiceQuebra));

        int posicao = indiceQuebra + 1;

        while (posicao < registros.size())
        {
            lateral.registros.add(registros.remove(posicao));
            lateral.ponteiros.add(ponteiros.remove(posicao));
        }
        lateral.ponteiros.add(ponteiros.remove(posicao));
        registros.remove(indiceQuebra);
        // escrever lateral e pegar o endereço
        superior.ponteiros.add(enderecoDaPagina);
        superior.ponteiros.add(lateral.getEnderecoDaPagina());
        lateral.setNumeroRegistros(lateral.registros.size());
        superior.numeroRegistros = superior.registros.size();
        numeroRegistros = registros.size();
        this.escreverPagina();
        lateral.escreverPagina();
        superior.setEnderecoDaPagina(buscaEnderecoLivreNaPilha());
        superior.escreverPagina();


        return superior;
    }

    /**
     * 
     * @param playerRegister
     * @throws Exception
     */
    public void inserir(PlayerRegister playerRegister) throws Exception
    {
        if (folha == true)
        {
            inserirFolha(playerRegister);
        } else
        {
            int contador = 0;
            while (contador < numeroRegistros)
            {
                if (playerRegister.getPlayer().getPlayerId() < registros.get(contador).getId())
                {

                    Pagina proxInsercao = lerPaginaDoArquivo(ponteiros.get(contador));// se o split
                                                                                      // foi feito,
                                                                                      // chama o
                                                                                      // inserir
                                                                                      // novamente
                                                                                      // na página
                                                                                      // pai, caso
                                                                                      // este teste
                                                                                      // não fosse
                                                                                      // feito,
                                                                                      // quebraria a
                                                                                      // propriedade
                                                                                      // da árvore
                    if (this.foiFeitoSplit(proxInsercao))
                    {
                        this.inserir(playerRegister);
                    } else
                    {
                        proxInsercao.inserir(playerRegister);// do contrário, segue normalmente.
                    }
                    contador = numeroRegistros + 1;// para a repetição
                } else
                {
                    contador++;
                }
            }
            if (contador == numeroRegistros)// se chegar a ser igual é porque o valor não é menor,
                                            // portanto, deverá ir ao ponteiro mais a direita.
            {
                Pagina proxInsercao = lerPaginaDoArquivo(ponteiros.get(contador));
                if (this.foiFeitoSplit(proxInsercao))
                {
                    this.inserir(playerRegister);
                } else
                {
                    proxInsercao.inserir(playerRegister);
                }

            }
        }

    }

    /**
     * Método de inserção inserção na folha, não é preciso o teste do número de registros, pois este
     * teste já foi feito anteriormente. Neste momento a única preocupação é inserir o id e o
     * ponteiro para dados em ordem.
     * 
     * @param playerRegister
     * @throws Exception
     */
    public void inserirFolha(PlayerRegister playerRegister) throws Exception
    {
        int contador = 0;
        while (contador < numeroRegistros)
        {
            if (playerRegister.getPlayer().getPlayerId() < registros.get(contador).getId())
            {
                registros.add(contador, new Registro());
                registros.get(contador).setId(playerRegister.getPlayer().getPlayerId());
                registros.get(contador).setPonteiro(playerRegister.getPosition());
                ponteiros.add((long) -1);
                contador = numeroRegistros + 1;// para a repetição
            }
            {
                contador++;
            }
        }
        ponteiros.add((long) -1);

        if (contador == numeroRegistros)// se chegar a ser igual é porque o valor não é menor,
                                        // portanto, deverá ir ao ponteiro mais a direita.
        {
            registros.add(new Registro());
            registros.get(contador).setId(playerRegister.getPlayer().getPlayerId());
            registros.get(contador).setPonteiro(playerRegister.getPosition());
            ponteiros.add((long) -1);
        }
        numeroRegistros++;
        this.escreverPagina();

    }

    /**
     * O objeto que chama este método envia como parâmetro o próximo caminho na inserção. Nesta
     * página inferior, onde é devida a inserção, verifica-se se já tem o tamanho máximo; se
     * necessário chama o método split()
     * 
     * @return
     * @param inferior página inferior onde haverá a inserção
     * @throws Exception
     */
    public boolean foiFeitoSplit(Pagina inferior) throws Exception
    {
        boolean resp = false;
        if (inferior.getNumeroRegistros() == tamanhoMax)
        {
            inferior.split(this);
            resp = true;
        }
        return resp;
    }

    /**
     * Lê a página do arquivo de índice recebendo como parâmetro o endereço no arquivo de índice.
     * Retorna a página preenchida.
     * 
     * @param endereco
     * @return
     * @throws Exception
     */
    public Pagina lerPaginaDoArquivo(long endereco) throws Exception
    {
        RAF arquivo = new RAF(prefixo + "indice.db", "r");
        arquivo.seek(endereco);
        Pagina pagina = new Pagina();
        pagina.enderecoDaPagina = endereco;
        pagina.setFolha(arquivo.readBoolean());
        pagina.setNumeroRegistros(arquivo.readInt());
        int contador = 0;
        while (contador < pagina.getNumeroRegistros())
        {
            pagina.ponteiros.add(arquivo.readLong());
            pagina.registros.add(new Registro());
            pagina.registros.get(contador).setId(arquivo.readInt());
            pagina.registros.get(contador).setPonteiro(arquivo.readLong());
            contador++;
        }
        pagina.ponteiros.add(arquivo.readLong());
        arquivo.close();
        return pagina;

    }

    /**
     * Escreve a página no arquivo de índice, o endereço é um dos atributos do objeto página,
     * preenche com -1 os demais itens não preenchidos, para que todas as páginas tenham tamanho
     * fixo de 153 bytes.
     * 
     * @throws Exception
     */
    public void escreverPagina() throws Exception
    {
        RAF arquivo = new RAF(prefixo + "indice.db", "rw");
        arquivo.seek(enderecoDaPagina);
        arquivo.writeBoolean(folha);
        int contador = 0;
        arquivo.writeInt(numeroRegistros);
        while (contador < numeroRegistros)
        {
            arquivo.writeLong(ponteiros.get(contador));
            arquivo.writeInt(registros.get(contador).getId());
            arquivo.writeLong(registros.get(contador).getPonteiro());
            contador++;
        }
        arquivo.writeLong(ponteiros.get(contador));

        while (contador < tamanhoMax)
        {
            arquivo.writeInt(-1);
            arquivo.writeLong(-1);
            arquivo.writeLong(-1);
            contador++;
        }
        // long tamanhoEscrita=arquivo.getFilePointer()-enderecoDaPagina;
        // System.out.println("Escreveu "+tamanhoEscrita+" bytes");
        arquivo.close();
    }
    /**
     * Efetua busca no arquivo de índice, retorna uma página nula, caso não tenha este endereço.
     * @param id
     * @return
     * @throws Exception
     */
    public Pagina procura(int id) throws Exception
    {
        Pagina resp = null;
        int maiorIdDaPagina = registros.get(numeroRegistros - 1).getId();

        if (id > maiorIdDaPagina)
        {
            Pagina proxBusca = lerPaginaDoArquivo(ponteiros.get(numeroRegistros));// última posição
            resp = proxBusca.procura(id);
        } else
        {
            for (int c = 0; c < numeroRegistros; c++)
            {
                int idAtual = registros.get(c).getId();
                if (id == idAtual)
                {
                    resp = this;
                    break;
                } else if (id < idAtual)
                {
                    if (this.folha == false)
                    {
                        Pagina proxBusca = lerPaginaDoArquivo(ponteiros.get(c));
                        resp = proxBusca.procura(id);
                    }
                    break;

                }
            }
        }
        return resp;
    }
    /**
     * este método não está funcional.
     * @param id
     * @return
     * @throws Exception
     */
    //Os métodos abaixo ainda não estão funcionais, foi escrito somente o esqueleto do que seria necessário, mas faltou tempo de testar e debugar;
    public Pagina delete(int id) throws Exception
    {
        delete(id, null);
        Pagina resp = new Pagina();
        if (this.numeroRegistros == 0)
        {
            resp = lerPaginaDoArquivo(ponteiros.get(0));
        } else
        {
            resp = this;
        }
        return resp;
    }

    private void delete(int id, Pagina superior) throws Exception
    {
        int contador = 0;
        int posicaoAlterada = 0;
        Pagina novaPagina = new Pagina();
        while (contador < numeroRegistros)
        {
            if (id == registros.get(contador).getId())
            {
                posicaoAlterada = contador;
                contador = numeroRegistros + 1;
                if (folha == true)
                {
                    removerNaFolha(id);
                } else
                {
                    removerPaginaIntermediaria(contador);
                }
            } else if (id < registros.get(contador).getId())
            {
                novaPagina = lerPaginaDoArquivo(registros.get(contador).getPonteiro());
                posicaoAlterada = contador;
                novaPagina.delete(id, this);
                contador = numeroRegistros + 1;
            } else
            {
                contador++;
            }

        }
        if (contador == numeroRegistros)
        {
            novaPagina = lerPaginaDoArquivo(registros.get(contador).getPonteiro());
            posicaoAlterada = contador;
            novaPagina.delete(id, this);
        }
        if (novaPagina != null)
        {
            novaPagina.balancear(this, posicaoAlterada);
        }

    }

    public Registro removerPaginaIntermediaria(int posicaoAlterada) throws Exception
    {
        // Pagina nova=lerPaginaDoArquivo(this.ponteiros.get(0));
        // registros.set(posicaoAlterada,nova.maiorEsquerda());
        Registro resp;
        Pagina pag = new Pagina();
        pag = lerPaginaDoArquivo(ponteiros.get(posicaoAlterada));
        if (pag.checarTamEsq())
        {
            resp = maiorEsquerda();
        } else
        {
            pag = lerPaginaDoArquivo(ponteiros.get(posicaoAlterada + 1));
            resp = pag.menorDireita();
        }
        return resp;
    }

    public boolean checarTamDir() throws Exception
    {
        boolean resp = false;
        if (folha == false)
        {

            resp = lerPaginaDoArquivo(ponteiros.get(0)).checarTamDir();

        } else
        {
            if (numeroRegistros > tamanhoMax / 2)
            {
                resp = true;
            }
        }
        return resp;
    }

    public boolean checarTamEsq() throws Exception
    {
        boolean resp = false;
        if (folha == false)
        {

            resp = lerPaginaDoArquivo(ponteiros.get(numeroRegistros)).checarTamDir();

        } else
        {
            if (numeroRegistros > tamanhoMax / 2)
            {
                resp = true;
            }
        }
        return resp;
    }

    public Registro maiorEsquerda() throws Exception
    {
        Registro resp;
        Pagina inferior = new Pagina();
        if (folha != true)
        {
            inferior = lerPaginaDoArquivo(ponteiros.get(numeroRegistros));
            resp = inferior.maiorEsquerda();
        } else
        {
            resp = registros.remove(numeroRegistros - 1);
            numeroRegistros -= 1;
            escreverPagina();
        }
        if (inferior != null)
        {
            inferior.balancear(this, numeroRegistros - 1);
        }
        return resp;
    }

    public Registro menorDireita() throws Exception
    {
        Registro resp;
        Pagina inferior = new Pagina();
        if (folha != true)
        {
            inferior = lerPaginaDoArquivo(ponteiros.get(0));
            resp = inferior.maiorEsquerda();
        } else
        {
            resp = registros.remove(0);
            numeroRegistros -= 1;
            escreverPagina();
        }
        if (inferior != null)
        {
            inferior.balancear(this, 0);
        }
        return resp;
    }


    public void balancear(Pagina superior, int posicaoAlterada) throws Exception
    {
        Pagina irma = new Pagina();
        if (numeroRegistros < tamanhoMax / 2)
        {
            if (posicaoAlterada == 0)
            {
                irma = lerPaginaDoArquivo(superior.ponteiros.get(1));
                superior.juncao(this, irma, posicaoAlterada);
            } else
            {
                irma = lerPaginaDoArquivo(superior.ponteiros.get(posicaoAlterada - 1));
                superior.juncao(irma, this, posicaoAlterada);
            }
        }

    }

    // falta a logica para a junção
    public void juncao(Pagina irmaEsq, Pagina irmaDir, int posicaoAlterada)
    {
        irmaEsq.registros.add(this.registros.remove(posicaoAlterada));
        irmaEsq.numeroRegistros += irmaDir.numeroRegistros + 1;
    }

    public int encontraPosicao(int id)
    {
        int contador = 0;
        int resp = 0;

        while (contador < numeroRegistros)
        {
            if (id == registros.get(contador).getId())
            {
                resp = contador;
                contador = numeroRegistros + 1;

            } else
            {
                contador++;

            }
        }

        return resp;

    }

    public Registro apagaRegistro(int id) throws Exception
    {
        int posicaoApagar = this.encontraPosicao(id);
        Registro reg = new Registro();
        reg = this.registros.remove(posicaoApagar);
        escreverPagina();
        return reg;

    }

    public Registro removerNaFolha(int id) throws Exception
    {
        Registro resp = new Registro();
        resp = apagaRegistro(id);
        numeroRegistros--;
        return resp;
    }
}
