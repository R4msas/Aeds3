import java.io.RandomAccessFile;
import java.util.ArrayList;
public class ArvoreB {
    public int numeroFilhos = 8;
    public Pagina raiz;
    public RandomAcessFile arquivo;

    public void delete(int id) {

    }

    public void update(Player player) {

    }

    public Player read(int id) {
        Player player = new Player();
        return player;
    }

    public void inserir(Player player) {
        if (raiz == null) {
            raiz = new Pagina();
            raiz.setTamanhoMax(numeroFilhos - 1);
            // escrever arquivo
        } else if (raiz.getNumeroRegistros() == raiz.getTamanhoMax()) {
            raiz = raiz.splitRaiz();
        } else {
            raiz.inserir(player);
        }

    }

}

class Pagina {

    private boolean lapide;
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

    // contrutores
    public Pagina() {
        numeroRegistros = 0;
        registros = new ArrayList<Registro>();
        ponteiros = new ArrayList<Long>();
    }
    // a fragmentação será na descida, sendo assim antes de passar para a próxima
    // página, verifica-se se é preciso fragmentar
//precisa concertar o retorno
    public void split(Pagina superior) {
        int indiceQuebra = tamanhoMax / 2;
        Pagina lateral = new Pagina();
        lateral.setTamanhoMax(tamanhoMax);
        lateral.setFolha(folha);
        int posicao = indiceQuebra + 1;
        if(superior.registros.get(superior.getNumeroRegistros()-1).getId()<registros.get(indiceQuebra).getId())
        {
            superior.registros.add(registros.remove(indiceQuebra));
            superior.ponteiros.add(lateral.getEnderecoDaPagina());
        }
        else{
            for(int c=0;c<superior.registros.size();c++)
            {
                if(superior.registros.get(c).getId()>registros.get(indiceQuebra).getId())
                {
                    superior.registros.add(c,registros.remove(indiceQuebra));
                    superior.registros.add(c,ponteiros.remove(indiceQuebra));
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

    public inserir(Player player)
    {
        if(folha==true)
        {
            inserirFolha(player);
        }
        else{
            int contador=0;
            while(contador<numeroRegistros)
            {
                if(player.getId()<registros.get(contador).getId())
                {
                Pagina proxInsercao=lerPaginaDoArquivo("indice.db",ponteiros.get(contador));
                
                 inserir(ponteiros.get(contador));
                 contador=numeroRegistros+1;//para a repetição   
                }
                else{
                    contador++;
                }
            }
            if (contador==numeroRegistros)//se chegar a ser igual é porque o valor não é menor, portanto, deverá ir ao ponteiro mais a direita.
            {
           (     inserir(ponteiros.get(contador));
            }
        }
    public boolean checaTamanho(Pagina inferior)
    {
        if(inferior.)
    }
    }
    public Pagina lerPaginaDoArquivo(String indice, long endereco){
        RandomAccessFile arquivo=new RandomAccessFile(indice, "rw");
        Pagina pagina= new Pagina();
        //falta
    }
    public void escreverPagina(Pagina pagina)
    {
        RandomAccessFile arquivo=new RandomAccessFile("indice.db", "rw");
        arquivo.getFilePointer(pagina.enderecoDaPagina);


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
