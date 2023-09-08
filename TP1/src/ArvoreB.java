import java.util.ArrayList;
public class ArvoreB {
public int numeroFilhos=8;
public Pagina raiz;


public void delete(int id){

}
public void update(Player player)
{

}
public Player read(int id)
{
    Player player=new Player();
    return player;
}
public void inserir(Player player){
    if (raiz==null)
    {
        raiz=new Pagina();
        raiz.setTamanhoMax(numeroFilhos-1);
        //escrever arquivo
    }
    else if(raiz.getNumeroRegistros()==raiz.getTamanhoMax())
    {
        raiz=raiz.splitRaiz();
    }
    inserir(raiz, player);
}
private void inserir(Pagina raiz, Player player){

}


}
class Pagina{

private int numeroRegistros;

public long getEnderecoDaPagina()
{
    return enderecoDaPagina;
}

public void setEnderecoDaPagina(long enderecoDaPagina)
{
    this.enderecoDaPagina = enderecoDaPagina;
}
private long enderecoDaPagina;
private ArrayList<Registro> registros;
private ArrayList<Long> ponteiros;
private boolean folha;
public boolean getFolha()
{
    return folha;
}

public void setFolha(boolean folha)
{
    this.folha = folha;
}
private int tamanhoMax;
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
//contrutores
public Pagina() {
    numeroRegistros = 0;
    registros = new ArrayList<Registro>();
    ponteiros = new ArrayList<Long>();
}
//a fragmentação será na descida, sendo assim antes de passar para a próxima página, verifica-se se é preciso fragmentar

public void split()
{
    int indiceQuebra=tamanhoMax/2;
    Pagina lateral=new Pagina();
    lateral.setTamanhoMax(tamanhoMax);
    lateral.setFolha(folha);
    int posicao=indiceQuebra+1;
    while(posicao<registros.size())
    {
        lateral.registros.add(registros.remove(posicao));
        lateral.ponteiros.add(ponteiros.remove(posicao));
    }
    lateral.ponteiros.add(ponteiros.remove(posicao+1));
    registros.remove(indiceQuebra);
    lateral.registros.add(registros.get(posicao));
    
    
}
public Pagina splitRaiz()
{
    //falta criar o método de pegar o endereço da página recém escrita
    int indiceQuebra=tamanhoMax/2;
    Pagina lateral=new Pagina();
    Pagina superior=new Pagina();
    lateral.setTamanhoMax(tamanhoMax);
    lateral.setFolha(folha);
    superior.setFolha(false);
    superior.registros.add(registros.get(indiceQuebra));
    
    int posicao=indiceQuebra+1;
    while(posicao<registros.size())
    {
        lateral.registros.add(registros.remove(posicao));
        lateral.ponteiros.add(ponteiros.remove(posicao));
    }
    lateral.ponteiros.add(ponteiros.remove(posicao+1));
    registros.remove(indiceQuebra);
    lateral.registros.add(registros.get(posicao));
    //escrever lateral e pegar o endereço
    superior.ponteiros.add(enderecoDaPagina);
    superior.ponteiros.add(lateral.getEnderecoDaPagina());
    return superior;
}




    
}
class Registro{
private long ponteiro;
private int id;


//getter's and setter's
public int getId()
{
    return id;
}
public void setId(int id)
{
    this.id = id;
}
public long getPonteiro()
{
    return ponteiro;
}
public void setPonteiro(long ponteiro)
{
    this.ponteiro = ponteiro;
}
//construtores
public Registro(long ponteiro, int id) {
        this.ponteiro = ponteiro;
        this.id = id;
    }
public Registro() {
    ponteiro=0;
    id=0;
}
}
