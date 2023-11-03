package Compressao;

import java.io.IOException;
import java.util.PriorityQueue;
import main.RAF;

public class Huffman {
    static final String caminhoDados="resources/db/csgo_players.db";
    static final String prefixo="resources/compressao/";
    static final String nomeArquivoSaida="dbCompressaoHuffmanVersao";
    static int versao;
    static final int tamanhoAlfabeto=256;
    int numeroNosFolha;
    
    public void compressao() throws Exception{
        NoHuffman array[]=new NoHuffman[256];
        contarRepeticoes(array);
        NoHuffman raiz=montaArvoreHuffman(array);
        String [] tabelaCaminho=new String[tamanhoAlfabeto];
        String s="";
        montaTabela(raiz,tabelaCaminho,s);
        RAF arquivoSaida=new RAF(prefixo+nomeArquivoSaida+versao, "rw");
        arquivoSaida.writeLong(0);
        escreveArvore(raiz, arquivoSaida);
        long tam=arquivoSaida.length();
        arquivoSaida.movePointerToStart();
        arquivoSaida.writeLong(tam);
        arquivoSaida.movePointerToEnd();
        escreveTexto(tabelaCaminho, arquivoSaida);
        
    }
    /**
     *Conta a repetição de caracteres, criando um novo nó de huffman se este não existir ou, se existir, incrementando a repetição. 
     * @param array de nós de huffman
     * @throws Exception
     */
    private void contarRepeticoes(NoHuffman[]array) throws Exception{
        RAF raf=new RAF(caminhoDados, "r");
        raf.movePointerToStart();
        char caractere;
        while(raf.canRead())
        {
            caractere=(char)raf.readUnsignedByte();
            int posicao=(int)caractere;
            if(array[posicao]==null)
            {
                array[posicao]=new NoHuffman(caractere);
            }
            else{
                array[posicao].incrementaRepeticao();
            }
        }
        raf.close();
    }
    /**
     * Cria a árvore a partir de uma fila de prioridade
     * @param array array de nós
     * @return retorna a raiz da árvore trie
     */
    private NoHuffman montaArvoreHuffman(NoHuffman[] array){
        PriorityQueue<NoHuffman> fila = new PriorityQueue<NoHuffman>(tamanhoAlfabeto, new ComparadorNoHuffman());
        for(int c=0;c<tamanhoAlfabeto;c++)//adiciona os elementos não nulos em uma fila de prioridade
        {
            if(array[c]!=null)
            {
                fila.add(array[c]);
                numeroNosFolha++;
            }
        }
        while(fila.size()>1)//enquanto a fila tiver mais de um elemento junta os dois nós com menor uso
        {
            NoHuffman novoNo=new NoHuffman();
            NoHuffman esq=fila.poll();
            NoHuffman dir=fila.poll();
            novoNo.repeticao=esq.repeticao+dir.repeticao;
            novoNo.esq=esq;
            novoNo.dir=dir;
            fila.add(novoNo);
        }
        NoHuffman raiz=fila.poll();
        return raiz;
    }
    /**
     * caminha pela árvore para se verificar o caminho a ser feito para chegar no caractere folha.
     * @param no nó huffman que será verificado
     * @param str array de strings que tem o tamanho do alfabeto, grava as posições uma a uma 
     * @param s concatena-se '0's e '1's até o caminho ser codificado
     */
    private void montaTabela(NoHuffman no, String[]str, String s)
    {
        if(no.eFolha()!=true)
        {
            montaTabela(no.esq, str, s+0);
            montaTabela(no.dir, str, s+1);
        }
        else{
            str[no.caractere]=s;
        }
    }
    private void escreveArvore(NoHuffman no, RAF arquivoSaida) throws Exception{
        arquivoSaida.movePointerToStart();
        if(no.eFolha()==true)
        {
            arquivoSaida.writeBoolean(true);
            arquivoSaida.writeChar(no.caractere);
        }
        else{
            arquivoSaida.writeBoolean(false);
            escreveArvore(no.esq, arquivoSaida);
            escreveArvore(no.dir, arquivoSaida);
        }
    }
    private void escreveTexto(String []caminho, RAF arquivoSaida) throws IOException{
        arquivoSaida.writeByte(0);
        RAF arquivoEntrada=new RAF(caminhoDados, "r");
        arquivoEntrada.movePointerToStart();
        String compactado;
        int posicao;
        while(arquivoEntrada.canRead())
        {
            posicao=arquivoEntrada.readUnsignedByte();
            compactado=caminho[posicao];
            if(compactado.length()>8)
            {
                String subString=compactado.substring(0, 7);
                Byte escrever=Byte.parseByte(subString, 2);
                compactado=compactado.substring(8);
            }
        }
        arquivoEntrada.close();
    }

    }
    private NoHuffman leArvore(RAF arquivoCompactado) throws Exception{
        boolean folha=arquivoCompactado.readBoolean();
        NoHuffman no;
        if(folha==true)
        {
            no=new NoHuffman((char)arquivoCompactado.readByte());
        }
        else{
            no=new NoHuffman(-1, leArvore(arquivoCompactado),leArvore(arquivoCompactado));
        }
        return no;
    }
    private void descompressao(){

    }
    private void remontaArvoreHuffman(){

    }
    private void lerArquivoCompactado(){

    }
    
}
