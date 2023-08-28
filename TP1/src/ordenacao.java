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
// falta testar
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
//falta testar
//cria um array list de jogadores vindos de um arquivo    
public ArrayList<Player> leVinteRegistros(String nomeArq) throws Exception
{
    int repeticao=0;
    RandomAccessFile arqPrincipal=new RandomAccessFile(nomeArq,"r");
    long ponteiroPrincipal=arqPrincipal.getFilePointer();
    ArrayList<Player> lista=new ArrayList<Player>();
        while(repeticao<20&&ponteiroPrincipal<arqPrincipal.length())//verifica se o arquivo não terminou pois o grupo poderá ter menos de vinte registros no grupo do arquivo
        {
            //Boolean lapide=arqPrincipal.readBoolean(); //vamos ver se não precis da lápide na hora de formar o arquivo.
            int tam=arqPrincipal.readInt();
            byte[] b=new byte[tam];
            arqPrincipal.read(b);
            Player tmp=new Player();
            tmp.fromByteArray(b);
            lista.add(tmp);
            repeticao++;
        }
        arqPrincipal.close();
        return lista;
}
public void mergeSort(ArrayList<Player>lista)
{
    mergeSort(lista,0,lista.size()-1);
}
private void mergeSort(ArrayList<Player> arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }
}

private void merge(ArrayList<Player> arr, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;
    
    ArrayList<Player> leftArr = new ArrayList<>();
    ArrayList<Player> rightArr = new ArrayList<>();
    
    for (int i = 0; i < n1; i++) {
        leftArr.add(arr.get(left + i));
    }
    for (int j = 0; j < n2; j++) {
        rightArr.add(arr.get(mid + 1 + j));
    }
    
    int i = 0, j = 0, k = left;
    
    while (i < n1 && j < n2) {
        if (leftArr.get(i).getPlayerId() <= rightArr.get(j).getPlayerId()) {
            arr.set(k, leftArr.get(i));
            i++;
        } else {
            arr.set(k, rightArr.get(j));
            j++;
        }
        k++;
    }
    
    while (i < n1) {
        arr.set(k, leftArr.get(i));
        i++;
        k++;
    }
    
    while (j < n2) {
        arr.set(k, rightArr.get(j));
        j++;
        k++;
    }
}
//recebe o nome de dois arquivos de entrada e dois arquivos de saída
public ArrayList<Long> intercalacaoExterna(String entradaUm, String entradaDois, String saidaUm, String saidaDois)
{

}

}

