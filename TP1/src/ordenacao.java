import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
public class ordenacao {
    
    private long ponteiroArquivo;
    private ArrayList<Long> ponteiroTmp;
    private int numTmp;


    public long getPonteiroArquivo()
    {
        return ponteiroArquivo;
    }

    public void setPonteiroArquivo(long ponteiroArquivo)
    {
        this.ponteiroArquivo = ponteiroArquivo;
    }

    public long[] getPonteiroTmp()
    {
        return ponteiroTmp;
    }

    public void setPonteiroTmp(long[] ponteiroTmp)
    {
        this.ponteiroTmp = ponteiroTmp;
    }

    
    public int getNumTmp()
    {
        return numTmp;
    }

    public void setNumTmp(int numTmp)
    {
        this.numTmp = numTmp;
    }

public ordenacao() {
    ponteiroArquivo=-1;
    ponteiroTmp=null;
    numTmp=0;
}

public ordenacao(long ponteiroArquivo, ArrayList<Long> ponteiroTmp, int numTmp) {
        this.ponteiroArquivo = ponteiroArquivo;
        this.ponteiroTmp = ponteiroTmp;
        this.numTmp = numTmp;
    }
//lê o arquivo inicial e a cada 20 registros alterna entre a escrita nos arquivos tmp0 e tmp1
public ArrayList<Long> leituraInicial(String nomeArquivoPrincipal)throws IOException{
    String nomeArquivo;
    String tmp="tmp";
    int repeticao=0;
    RandomAccessFile tmp0=new RandomAccessFile("tmp0","w");
    RandomAccessFile tmp1=new RandomAccessFile("tmp1","w");
    RandomAccessFile arqPrincipal=new RandomAccessFile(nomeArquivoPrincipal,"r");
    int ultimoId=arqPrincipal.readInt();
    int id;
    ArrayList<Long> ponteiros=new ArrayList<Long>;


do
{
    id=arqPrincipal.readInt();
    nomeArquivo=tmp+repeticao;
    for (int c=0;c<20;c++)
    {
        
        

    }
    repeticao=(repeticao-1)*-1;//alterna entre 0 e 1 para salvar no arquivo tmp 0 e 1;
}while();

tmp0.close();
tmp1.close();
arqPrincipal.close();
}
    
}
