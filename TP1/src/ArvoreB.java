import java.util.ArrayList;

/**
 * Indexação dos registros em uma árvore B de tamanho 8
 */
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
//a fragmentação será na descida, sendo assim antes de passar para a próxima página, verifica-se se é preciso fragmentar
public void inserir(Player player){
    int tamanhoMax=numeroFilhos-1;
    if (raiz==null)
    {
        raiz=new Pagina();
    }
    else if(raiz.getNumeroRegistros()==tamanhoMax)
    {
        split(raiz, tamanhoMax);
    }
    inserir(raiz, player,tamanhoMax);
}
private void inserir(Pagina raiz, Player player,int tamanhoMax){

}
public void split(Pagina pagina, int tamanhoMax)
{
    Pagina superior=new Pagina();
    Pagina lateral=new Pagina();
    superior.registros.add(pagina.registros.get(tamanhoMax/2));
    superior.ponteiros.add()



    if(pagina==raiz)
    {

    }
}


}
class Pagina{

private int numeroRegistros;


public ArrayList<Registro> registros;
public ArrayList<Long> ponteiros;
public boolean folha;
private long ponteiroDir;
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
    folha=true;
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