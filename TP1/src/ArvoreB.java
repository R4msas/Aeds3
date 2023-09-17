import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class ArvoreB {
    private int numeroFilhos = 8;
    private Pagina raiz;

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
        raiz.delete(int id);
    }

    public void update(Player player) {

    }

    public Player read(int id) {
        Player player = new Player();
        return player;
    }

    public void inserir(PlayerRegister player) throws Exception
    {

        if (raiz.getNumeroRegistros() == raiz.getTamanhoMax())
        {
            raiz = raiz.splitRaiz();
        } else {
            raiz.inserir(player);
        }

    }
    public void procura(int id) throws Exception {
    
        RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rs");
        long endereco = arquivo.readLong();
        raiz=raiz.lerPaginaDoArquivo(endereco);
        raiz.procura(id);
        arquivo.close();
    
}

}

class Pagina {

    private int numeroRegistros;
    private long enderecoDaPagina;
    private ArrayList<Registro> registros;
    private ArrayList<Long> ponteiros;
    private boolean folha;
    private int tamanhoMax;

    public long getEnderecoDaPagina() {
        return enderecoDaPagina;
    }

    public void setEnderecoDaPagina(long enderecoDaPagina) {
        this.enderecoDaPagina = enderecoDaPagina;
    }

    public boolean getFolha() {
        return folha;
    }

    public void setFolha(boolean folha) {
        this.folha = folha;
    }

    public int getTamanhoMax() {
        return tamanhoMax;
    }

    public void setTamanhoMax(int tamanhoMax) {
        this.tamanhoMax = tamanhoMax;
    }

    public int getNumeroRegistros() {
        return numeroRegistros;
    }

    public void setNumeroRegistros(int numeroRegistros) {
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
                .get(indiceQuebra).getId()) {
            superior.registros.add(registros.remove(indiceQuebra));
            superior.ponteiros.add(lateral.getEnderecoDaPagina());
        } else {
            for (int c = 0; c < superior.registros.size(); c++) {
                if (superior.registros.get(c).getId() > registros.get(indiceQuebra).getId()) {
                    superior.registros.add(c, registros.remove(indiceQuebra));
                    superior.ponteiros.add(c, ponteiros.remove(indiceQuebra));
                    superior.ponteiros.add(c, ponteiros.remove(indiceQuebra));
                }
            }
        }
        while (posicao < registros.size()) {
            lateral.registros.add(registros.remove(posicao));
            lateral.ponteiros.add(ponteiros.remove(posicao));
        }
        lateral.ponteiros.add(ponteiros.remove(posicao + 1));
        registros.remove(indiceQuebra);
        lateral.registros.add(registros.get(posicao));
        lateral.setNumeroRegistros(lateral.registros.size());
        numeroRegistros = registros.size();
        escreverPagina(lateral);
        escreverPagina(superior);
        escreverPagina(this);
    }

    public long buscaEnderecoLivre() throws Exception {
        long resp;
        RandomAccessFile pilha = new RandomAccessFile("pilhaLapide.db", "rs");
        if (pilha.length() < 8) {
            RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rs");
            resp = arquivo.getFilePointer();
            arquivo.close();
        } else {
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

    public void excluirPagina() {

    }

    

    public void procura(int id) {

    }

    public void excluirFolha(){

    }

    public Pagina splitRaiz() {
        // falta criar o método de pegar o endereço da página recém escrita
        int indiceQuebra = tamanhoMax / 2;
        Pagina lateral = new Pagina();
        Pagina superior = new Pagina();
        lateral.setTamanhoMax(tamanhoMax);
        lateral.setFolha(folha);
        superior.setFolha(false);
        superior.registros.add(registros.get(indiceQuebra));

        int posicao = indiceQuebra + 1;
        while (posicao < registros.size()) {
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

    public void inserir(PlayerRegister playerRegister) throws Exception {
        if (folha == true) {
            inserirFolha(playerRegister);
        } else {
            int contador = 0;
            while (contador < numeroRegistros) {
                if (playerRegister.getPlayer().getPlayerId() < registros.get(contador).getId()) {

                    Pagina proxInsercao = lerPaginaDoArquivo(ponteiros.get(contador));
                    this.checaTamanho(proxInsercao);
                    proxInsercao.inserir(playerRegister);
                    contador = numeroRegistros + 1;// para a repetição
                } else {
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

    public void inserirFolha(PlayerRegister playerRegister) {
        int contador = 0;
        while (contador < numeroRegistros) {
            if (playerRegister.getPlayer().getPlayerId() < registros.get(contador).getId()) {
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

    public void checaTamanho(Pagina inferior) {
        if (inferior.getNumeroRegistros() == tamanhoMax) {
            inferior.split(this);
        }
    }

    public Pagina lerPaginaDoArquivo(long endereco) throws Exception {
        RandomAccessFile arquivo = new RandomAccessFile("indice", "r");
        arquivo.seek(endereco);
        Pagina pagina = new Pagina();
        pagina.enderecoDaPagina = endereco;
        pagina.setFolha(arquivo.readBoolean());
        pagina.setNumeroRegistros(arquivo.readInt());
        int contador = 0;
        while (contador < pagina.getNumeroRegistros()) {
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

    public void escreverPagina(Pagina pagina) throws Exception {
        RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rw");
        arquivo.seek(pagina.enderecoDaPagina);
        arquivo.writeBoolean(pagina.folha);
        int contador = 0;
        arquivo.writeInt(pagina.numeroRegistros);
        while (contador < pagina.registros.size()) {
            arquivo.writeLong(pagina.ponteiros.get(contador));// ponteiro para outra página
            arquivo.writeInt(pagina.registros.get(contador).getId());
            arquivo.writeLong(pagina.registros.get(contador).getPonteiro());// ponteiro para o
                                                                            // arquivo de
            // dados
            contador++;
        }
        arquivo.writeLong(ponteiros.get(contador));
        while (contador < tamanhoMax) {
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
    public void apagaRegistro(int id)
    {
        int posicaoApagar=this.encontraPosicao(id);
        this.registros.get(posicaoApagar);
    }
}

class Registro {
    private long ponteiro;
    private int id;

    // getter's and setter's
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPonteiro() {
        return ponteiro;
    }

    public void setPonteiro(long ponteiro) {
        this.ponteiro = ponteiro;
    }

    // construtores
    public Registro(long ponteiro, int id) {
        this.ponteiro = ponteiro;
        this.id = id;
    }

    public Registro() {
        ponteiro = 0;
        id = 0;
    }
}
