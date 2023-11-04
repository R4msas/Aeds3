package Compressao;

import java.io.IOException;
import java.time.*;
import java.util.PriorityQueue;
import main.RAF;

public class Huffman {
    static final String caminhoDados="resources/db/csgo_players.db";
    static final String prefixo="resources/compressao/";
    static final String nomeArquivoCompactado="dbCompressaoHuffmanVersao";
    static final String nomeArquivoDescompactado="dbDescompactadoHuffmanVersao";

    static int versao;
    static final int tamanhoAlfabeto=256;
    int numeroNosFolha;
    private long inicio,fim;
    int numeroBytes;
    
    public void compressao() throws Exception{
        inicio=Instant.now().toEpochMilli();
        NoHuffman array[]=new NoHuffman[256];
        contarRepeticoes(array);
        NoHuffman raiz=montaArvoreHuffman(array);
        String [] tabelaCaminho=new String[tamanhoAlfabeto];
        String s="";
        montaTabela(raiz,tabelaCaminho,s);
        RAF arquivoSaida=new RAF(prefixo+nomeArquivoCompactado+versao+".db", "rw");
        arquivoSaida.writeLong(0);
        arquivoSaida.writeInt(0);
        arquivoSaida.writeInt(0);
        escreveArvore(raiz, arquivoSaida);
        long tam=arquivoSaida.length();
        arquivoSaida.movePointerToStart();
        arquivoSaida.writeLong(tam);
        arquivoSaida.movePointerToEnd();
        int numeroBits=escreveTexto(tabelaCaminho, arquivoSaida);
        arquivoSaida.seek(8);
        arquivoSaida.writeInt(numeroBytes);
        arquivoSaida.writeInt(numeroBits);
        arquivoSaida.close();
        fim=Instant.now().toEpochMilli();
        System.out.println("Tempo de compressão em milissegundos: "+(fim-inicio));

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
    /**
     * Este método concatena no final da representação da árvore trie o texto compactado, no último byte a ordem de escrita deve ser diferente, e por consequência a leitura.
     * @param caminho contem as codificações de huffman para cada caractere do texto
     * @param arquivoSaida arquivo compactado 
     * @return retorna quantos são os bits importantes do último byte.
     * @throws IOException
     */
    private int escreveTexto(String []caminho, RAF arquivoSaida) throws IOException{
        RAF arquivoEntrada=new RAF(caminhoDados, "r");
        arquivoEntrada.movePointerToStart();
        String compactado="";
        int posicao;
        int bitsEscritosNoUltimoByte=0;
        while(arquivoEntrada.canRead())
        {
            posicao=arquivoEntrada.readUnsignedByte();
            compactado=caminho[posicao];
            while(compactado.length()>8)
            {
                String subString=compactado.substring(0, 8);
                int escrever=Integer.parseInt(subString,2);
                compactado=compactado.substring(8);
                arquivoSaida.writeByte(escrever);
                numeroBytes++;

            }
        }
        //lógica de escrita do último byte, preencherá com zero o restante da string
        if(compactado.length()>0)
        {
            bitsEscritosNoUltimoByte=compactado.length();
            while(compactado.length()!=8)
            {
                compactado=compactado+'0';
                
            }
           int escrever=Integer.parseInt(compactado,2);
            arquivoSaida.writeByte(escrever);
            numeroBytes++;
        }
        
        arquivoEntrada.close();
        return bitsEscritosNoUltimoByte;
    }

    public void descompacta() throws Exception{
        RAF arquivoCompactado=new RAF(prefixo+nomeArquivoCompactado+versao+".db", "r");
        arquivoCompactado.movePointerToStart();
        Long comecoTexto=arquivoCompactado.readLong();
        int numeroBytesEscritos=arquivoCompactado.readInt();
        int numeroBitsNoUltimoByte=arquivoCompactado.readInt();
        NoHuffman raiz=remontaArvoreHuffman(arquivoCompactado);
        arquivoCompactado.seek(comecoTexto);
        leTexto(arquivoCompactado, raiz,numeroBitsNoUltimoByte, numeroBytesEscritos);

        
    }
    private NoHuffman remontaArvoreHuffman(RAF arquivoCompactado) throws Exception{
        boolean folha=arquivoCompactado.readBoolean();
        NoHuffman no;
        if(folha==true)
        {
            no=new NoHuffman((char)arquivoCompactado.readByte());
        }
        else{
            no=new NoHuffman(-1, remontaArvoreHuffman(arquivoCompactado),remontaArvoreHuffman(arquivoCompactado));
        }
        return no;
    }
    
    private void leTexto(RAF arquivoCompactado,NoHuffman raiz, int numeroBitsNoUltimoByte, int numeroBytesEscritos) throws Exception{
        String instrucao="";
        RAF arquivoDescompactado=new RAF (prefixo+nomeArquivoDescompactado+".db","rw");
        int bytesLidos=1;
        int posicaoChar=0;
        while(bytesLidos<numeroBytesEscritos)
        {
            NoHuffman no=raiz;
            while(no.eFolha()==false)
            {
                if(instrucao.length()<=posicaoChar)
                {
                   if(bytesLidos<numeroBytesEscritos)
                   {
                    instrucao=lerUmByte(arquivoCompactado);
                    posicaoChar=0;
                   }
                   else{
                    instrucao=lerUltimoByte(arquivoCompactado, numeroBitsNoUltimoByte);
                    posicaoChar=0;
                   }
                }
                if(instrucao.charAt(posicaoChar)=='1')
                {
                    no=no.dir;
                }
                else{
                    no=no.esq;
                }
                posicaoChar++;
            }
            arquivoDescompactado.writeByte((int)no.caractere);
        }
    arquivoDescompactado.close();
    }
    private String lerUmByte(RAF arquivoCompactado) throws IOException
    {
        int valorByte=arquivoCompactado.readUnsignedByte();
        String resp=Integer.toBinaryString(valorByte);
        return resp;
    }
    private String lerUltimoByte(RAF arquivoCompactado,int numeroBitsNoUltimoByte) throws IOException
    {
        int valorByte=arquivoCompactado.readUnsignedByte();
        String resp=Integer.toBinaryString(valorByte);
        resp=resp.substring(0, numeroBitsNoUltimoByte);
        return resp;
    }


    }
  
    

