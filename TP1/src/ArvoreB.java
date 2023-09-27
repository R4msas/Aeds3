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
        raiz.delete(id);
    }

    public void update(Player player)
    {

    }

    public Player read(int id)
    {
        Player player = new Player();
        return player;
    }

    public void inserir(PlayerRegister player) throws Exception
    {
        if (raiz == null)
        {
            raiz = new Pagina();
            Registro registro = new Registro();
            registro.setPonteiro(player.getPosition());
            registro.setId(player.getPlayer().getPlayerId());
            ArrayList<Registro> resp = new ArrayList<>();
            resp.add(registro);
            raiz.setRegistros(resp);

        } else if (raiz.getNumeroRegistros() == raiz.getTamanhoMax())
        {
            raiz = raiz.splitRaiz();
        } else
        {
            raiz.inserir(player);
        }

    }

    public void procura(int id) throws Exception
    {

        RandomAccessFile arquivo = new RandomAccessFile("indice.db", "rw");
        long endereco = arquivo.readLong();
        raiz = raiz.lerPaginaDoArquivo(endereco);
        raiz.procura(id);
        arquivo.close();

    }

}
