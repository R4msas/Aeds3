package Compressao;
/**
 *  Nó da árvore de huffman, contem o caractere, quantas vezes ele repete no texto
 */
public class NoHuffman {
    int repeticao;
    char caractere;
    NoHuffman esq;
    NoHuffman dir;
    
    public NoHuffman() 
    {}
    
    /**
     * Construtor utilizado na montagem da árvore
     * @param repeticao quantas vezes o char é usado se folha, valor de peso na árvore de prioridade
     * @param esq ponteiro para a esquerda
     * @param dir ponteiro para a direita
     */
    public NoHuffman(int repeticao, NoHuffman esq, NoHuffman dir) {
        this.repeticao = repeticao;
        this.esq = esq;
        this.dir = dir;
    }
    /**
     *construtor do nó, recebe o caractere e inicializa as repetições em 1; Será utilizado na primeira leitura do arquivo;
     * @param caractere char lido no arquivo base
     */
    public NoHuffman(char caractere) {
        this.caractere = caractere;
        repeticao=1;
        esq=dir=null;
    }
    public void incrementaRepeticao()
    {
        repeticao=repeticao+1;
    }
    /**
     * testa se é folha
     * @return booleano informando se é um nó folha ou não
     */
    public boolean eFolha()
    {
        boolean resp=false;
        if(esq==null&&dir==null)
        {
            resp=true;
        }
        return resp;
    }
}
