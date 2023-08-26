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
public void leituraInicial(String nomeArquivoPrincipal)throws IOException{
    int numArquivo=0, repeticao=0;
    RandomAccessFile tmp[]=new RandomAccessFile[2];
    tmp[0]=new RandomAccessFile("tmp0","w");
    tmp[1]=new RandomAccessFile("tmp1","w");
    RandomAccessFile arqPrincipal=new RandomAccessFile(nomeArquivoPrincipal,"r");
    arqPrincipal.readInt();
    long ponteiroPrincipal=arqPrincipal.getFilePointer();
    do
        {
            repeticao=0;
           while(repeticao<20&&ponteiroPrincipal<arqPrincipal.length())
            {
                ponteiroPrincipal=arqPrincipal.getFilePointer();
                Boolean lapide=arqPrincipal.readBoolean();
                int tam=arqPrincipal.readInt();
                if(lapide==false)
                {
                    byte[] b=new byte[tam];
                    arqPrincipal.read(b);
                    tmp[numArquivo].write(b);
                    repeticao++;
                }
                else{
                    arqPrincipal.skipBytes(tam);
                }
            }
            numArquivo=(numArquivo-1)*-1;//alterna entre 0 e 1 para salvar no arquivo tmp 0 e 1;
        }while(ponteiroPrincipal<arqPrincipal.length());

tmp[0].close();
tmp[1].close();
arqPrincipal.close();
}
    
public void leVinteRegistrosEOrdena (String nomeArq)
{
    
}
}
