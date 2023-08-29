import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class ordenacao {
    public int tamanho=20;
    // falta testar
    // lê o arquivo inicial e a cada 20 registros alterna entre a escrita nos arquivos tmp0 e tmp1
    public void leituraInicial(String nomeArquivoPrincipal) throws IOException
    {
        int numArquivo = 0, repeticao = 0;
        RandomAccessFile tmp[] = new RandomAccessFile[2];
        tmp[0] = new RandomAccessFile("tmp0", "rw");
        tmp[1] = new RandomAccessFile("tmp1", "rw");
        RandomAccessFile arqPrincipal = new RandomAccessFile(nomeArquivoPrincipal, "r");
        arqPrincipal.readInt();
        long ponteiroPrincipal = arqPrincipal.getFilePointer();
        ArrayList<PlayerRegister> lista=new ArrayList<>();
 
        do
        {
            repeticao = 0;
            while (repeticao < tamanho && ponteiroPrincipal < arqPrincipal.length())
            {
                PlayerRegister pr=new PlayerRegister();
                pr.fromFileIfNotTomb(arqPrincipal);
                if(pr.getPlayer()!=null)
                {
                    repeticao++;
                }
                lista.add(pr);
            }
            mergeSort(lista);
            for (PlayerRegister pr : lista) {
                tmp[numArquivo].write(pr.toByteArray());
            }
            tmp[numArquivo]
            numArquivo = (numArquivo - 1) * -1;// alterna entre 0 e 1 para salvar no arquivo tmp 0 e
                                               // 1;
        } while (ponteiroPrincipal < arqPrincipal.length());

        tmp[0].close();
        tmp[1].close();
        arqPrincipal.close();
    }

    // falta testar
    // cria um array list de jogadores vindos de um arquivo

    public void mergeSort(ArrayList<PlayerRegister> lista)
    {
        mergeSort(lista, 0, lista.size() - 1);
    }

    private void mergeSort(ArrayList<PlayerRegister> arr, int left, int right)
    {
        if (left < right)
        {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(ArrayList<PlayerRegister> arr, int left, int mid, int right)
    {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        ArrayList<Player> leftArr = new ArrayList<>();
        ArrayList<Player> rightArr = new ArrayList<>();

        for (int i = 0; i < n1; i++)
        {
            leftArr.add(arr.get(left + i));
        }
        for (int j = 0; j < n2; j++)
        {
            rightArr.add(arr.get(mid + 1 + j));
        }

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2)
        {
            if (leftArr.get(i).getPlayerId() <= rightArr.get(j).getPlayerId())
            {
                arr.set(k, leftArr.get(i));
                i++;
            } else
            {
                arr.set(k, rightArr.get(j));
                j++;
            }
            k++;
        }

        while (i < n1)
        {
            arr.set(k, leftArr.get(i));
            i++;
            k++;
        }

        while (j < n2)
        {
            arr.set(k, rightArr.get(j));
            j++;
            k++;
        }
    }

    // recebe o nome de dois arquivos de entrada e dois arquivos de saída

    public void intercalacaoExterna() throws Exception
    {
        int tam=tamanho;
        RandomAccessFile entrada[] = new RandomAccessFile[2];
        entrada[0] = new RandomAccessFile("entrada0", "rw");
        entrada[1] = new RandomAccessFile("entrada1", "rw");
        RandomAccessFile saida[] = new RandomAccessFile[2];
        saida[0] = new RandomAccessFile("saida0", "rw");
        saida[1] = new RandomAccessFile("saida1", "rw");
        intercalacaoExterna(tam, entrada, saida);

    }

private void intercalacaoExterna(int tam, RandomAccessFile entrada[], RandomAccessFile saida[]) throws Exception
{
    int iteradorZero=0, iteradorUm=0;
    PlayerRegister temp0=new PlayerRegister();
    PlayerRegister temp1=new PlayerRegister();
    temp0.fromFileIfNotTomb(entrada[0]);
    temp1.fromFileIfNotTomb(entrada[1]);
    while (entrada[0].length()>entrada[0].getFilePointer()||entrada[1].length()>entrada[1].getFilePointer())
    {
    while((iteradorZero<tam)||(iteradorUm<tam))
    {
       if(entrada[0].length()>entrada[0].getFilePointer())
       {
        entrada[1].write(temp1)
        }
       else if(temp0.getPlayer().getPlayerId()<temp1.getPlayer().getPlayerId())
        {
            
        }
    }
    }

}

}

