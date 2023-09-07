/**
 * Indexação dos registros em uma árvore B de tamanho 8
 */
public class ArvoreB {
public int numeroFilhos=8;
public Pagina raiz;

public void inserirVazio(int id, long ponteiroOrigem){

}
private void inserirCheio(){

}
private void split(){

}
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


}
class Pagina{

private int numeroRegistros;
private No no;
public char identificaPagina;//f para folha, r para raiz

//getter's and setter's
public int getNumeroRegistros()
{
    return numeroRegistros;
}
public void setNumeroRegistros(int numeroRegistros)
{
    this.numeroRegistros = numeroRegistros;
}

public No getNo()
{
    return no;
}
public void setNo(No no)
{
    this.no = no;
}
//contrutores
public Pagina(int numeroRegistros, No no) {
    this.numeroRegistros = numeroRegistros;
    this.no = no;
}
public Pagina() {
    numeroRegistros = 0;
    no = null;
}



    
}
class No{
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
public No(long ponteiro, int id) {
        this.ponteiro = ponteiro;
        this.id = id;
    }
public No() {
    ponteiro=0;
    id=0;
}
}