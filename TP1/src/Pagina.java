import java.io.RandomAccessFile;
import java.util.ArrayList;
class Pagina {

    private int numeroRegistros;
    private long enderecoDaPagina;
    private ArrayList<Registro> registros;
    private ArrayList<Long> ponteiros;
    private boolean folha;
    private int tamanhoMax;

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

    // contrutores
    public Pagina() {
        numeroRegistros = 0;
        registros = new ArrayList<Registro>();
        ponteiros = new ArrayList<Long>();
    }

    // a fragmentação será na descida, sendo assim antes de passar para a próxima
    // página, verifica-se se é preciso fragmentar
    // precisa concertar o retorno
    public void split(Pagina superior) throws Exception
    {
        int indiceQuebra = tamanhoMax / 2;
        Pagina lateral = new Pagina();
        lateral.setTamanhoMax(tamanhoMax);
        lateral.setFolha(folha);
        lateral.setEnderecoDaPagina(buscaEnderecoLivre());
        int posicao = indiceQuebra + 1;
        if (superior.registros.get(superior.getNumeroRegistros() - 1).getId() < registros
                .get(indiceQuebra).getId())
        {
            superior.registros.add(registros.remove(indiceQuebra));
            superior.ponteiros.add(lateral.getEnderecoDaPagina());
        } else
        {
            for (int c = 0; c < superior.registros.size(); c++)
            {
                if (superior.registros.get(c).getId() > registros.get(indiceQuebra).getId())
                {
                    superior.registros.add(c, registros.remove(indiceQuebra));
                    superior.ponteiros.add(c, ponteiros.remove(indiceQuebra));
                    superior.ponteiros.add(c, ponteiros.remove(indiceQuebra));
                }
            }
        }
        while (posicao < registros.size())
        {
            lateral.registros.add(registros.remove(posicao));
            lateral.ponteiros.add(ponteiros.remove(posicao));
        }
        lateral.ponteiros.add(ponteiros.remove(posicao + 1));
        registros.remove(indiceQuebra);
        lateral.registros.add(registros.get(posicao));
        lateral.setNumeroRegistros(lateral.registros.size());
        numeroRegistros = registros.size();
        lateral.escreverPagina();
        superior.escreverPagina();
        this.escreverPagina();
    }

    public long buscaEnderecoLivre() throws Exception
    {
        long resp;
        RandomAccessFile pilha = new RandomAccessFile("pilhaLapide.db", "rs");
        if (pilha.length() < 8)
        {
            RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rs");
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

    // cria uma pilha de páginas deletadas, como os arquivos são de mesmo tamanho,
    // pode se
    // economizar espaço
    public void excluiPagina() throws Exception
    {
        RandomAccessFile pilha = new RandomAccessFile("pilhaLapide.db", "rw");
        pilha.writeLong(enderecoDaPagina);
        pilha.close();
    }

    public void excluirPagina()
    {

    }

    public void excluirFolha()
    {

    }

    public Pagina splitRaiz()
    {
        // falta criar o método de pegar o endereço da página recém escrita
        int indiceQuebra = tamanhoMax / 2;
        Pagina lateral = new Pagina();
        Pagina superior = new Pagina();
        lateral.setTamanhoMax(tamanhoMax);
        lateral.setFolha(folha);
        superior.setFolha(false);
        superior.registros.add(registros.get(indiceQuebra));

        int posicao = indiceQuebra + 1;
        while (posicao < registros.size())
        {
            lateral.registros.add(registros.remove(posicao));
            lateral.ponteiros.add(ponteiros.remove(posicao));
        }
        lateral.ponteiros.add(ponteiros.remove(posicao + 1));
        registros.remove(indiceQuebra);
        lateral.registros.add(registros.get(posicao));
        // escrever lateral e pegar o endereço
        superior.ponteiros.add(enderecoDaPagina);
        superior.ponteiros.add(lateral.getEnderecoDaPagina());
        lateral.setNumeroRegistros(lateral.registros.size());
        superior.numeroRegistros = superior.registros.size();
        return superior;
    }

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

                    Pagina proxInsercao = lerPaginaDoArquivo(ponteiros.get(contador));
                    this.checaTamanho(proxInsercao);
                    proxInsercao.inserir(playerRegister);
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
                this.checaTamanho(proxInsercao);
                proxInsercao.inserir(playerRegister);

            }
        }

    }

    public void inserirFolha(PlayerRegister playerRegister)
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

    }

    public void checaTamanho(Pagina inferior) throws Exception
    {
        if (inferior.getNumeroRegistros() == tamanhoMax)
        {
            inferior.split(this);
        }
    }

    public Pagina lerPaginaDoArquivo(long endereco) throws Exception
    {
        RandomAccessFile arquivo = new RandomAccessFile("indice", "r");
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
        pagina.tamanhoMax = tamanhoMax;// pode der erro aqui.
        arquivo.close();
        return pagina;

    }

    public void escreverPagina() throws Exception
    {
        RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rw");
        arquivo.seek(enderecoDaPagina);
        arquivo.writeBoolean(folha);
        int contador = 0;
        arquivo.writeInt(numeroRegistros);
        while (contador < registros.size())
        {
            arquivo.writeLong(ponteiros.get(contador));// ponteiro para outra página
            arquivo.writeInt(registros.get(contador).getId());
            arquivo.writeLong(registros.get(contador).getPonteiro());// ponteiro para o
                                                                     // arquivo de
            // dados
            contador++;
        }
        arquivo.writeLong(ponteiros.get(contador));
        while (contador < tamanhoMax)
        {
            arquivo.writeLong(-1);// ponteiro para outra página
            arquivo.writeInt(-1);
            arquivo.writeLong(-1);// ponteiro para o arquivo de dados
            contador++;

        }

        arquivo.close();
    }

    public Pagina procura(int id) throws Exception
    {
        int contador = 0;
        Pagina resp = null;

        while (contador < numeroRegistros)
        {
            if (id == registros.get(contador).getId())
            {
                resp = this;
                contador = numeroRegistros + 1;

            } else if (id < registros.get(contador).getId())
            {
                if (this.folha == false)
                {
                    Pagina proxBusca = lerPaginaDoArquivo(ponteiros.get(contador));
                    proxBusca.procura(id);
                } else
                {
                    contador = numeroRegistros + 1;
                }
            } else
            {
                contador++;

            }
        }
        if (contador == numeroRegistros)// se chegar a ser igual é porque o valor não é menor,
                                        // portanto, deverá ir ao ponteiro mais a direita.
        {

            resp = null;
            if (this.folha == false)
            {
                Pagina proxBusca = lerPaginaDoArquivo(ponteiros.get(contador));
                proxBusca.procura(id);
            }

        }
        return resp;
    }

    public Pagina delete(int id) throws Exception
    {
        delete(id, null);
        Pagina resp=new Pagina();
        if(this.numeroRegistros==0)
        {
            resp=lerPaginaDoArquivo(ponteiros.get(0));
        }
        else{
            resp=this;
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
        Pagina nova=lerPaginaDoArquivo(this.ponteiros.get(0));
        registros.set(posicaoAlterada,nova.maiorEsquerda());
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
            resp=registros.remove(numeroRegistros - 1);
            numeroRegistros -= 1;
            escreverPagina();
        }
        if(inferior!=null)
        {
            inferior.balancear(this, numeroRegistros-1);
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
            resp=registros.remove(0);
            numeroRegistros -= 1;
            escreverPagina();
        }
        if(inferior!=null)
        {
            inferior.balancear(this, 0);
        }
        return resp;
    }


    public void balancear(Pagina superior, int posicaoAlterada)
    {

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


    }
}