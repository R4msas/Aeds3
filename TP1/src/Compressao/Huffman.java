package Compressao;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

import main.RAF;

public class Huffman {
    static final String caminhoDados = "resources/db/csgo_players.db";
    static final String prefixo = "resources/compressao/";
    static final String nomeArquivoCompactado = "dbCompressaoHuffmanVersao";
    static final String nomeArquivoDescompactado = "dbDescompactadoHuffmanVersao";
    private NoHuffman[] arrayHuffman;

    static int versao;
    static final int tamanhoAlfabeto = 256;
    int numeroNosFolha;

    public long compressao() throws Exception {
        Cabecalho cabecalho = new Cabecalho();
        long inicio = System.currentTimeMillis();
        arrayHuffman = new NoHuffman[256];
        contarRepeticoes(arrayHuffman);
        NoHuffman raiz = montaArvoreHuffman(arrayHuffman);
        String[] tabelaCaminho = new String[tamanhoAlfabeto];
        String s = "";
        montaTabela(raiz, tabelaCaminho, s);
        RAF arquivoSaida = new RAF(prefixo + nomeArquivoCompactado + versao + ".db", "rw");
        arquivoSaida.writeLong(0);
        arquivoSaida.writeInt(0);
        arquivoSaida.writeInt(0);
        escreveArvore(raiz, arquivoSaida, cabecalho);
        cabecalho.setInicioTexto(arquivoSaida.getFilePointer());
        escreveTexto(tabelaCaminho, arquivoSaida, cabecalho);
        arquivoSaida.movePointerToStart();
        cabecalho.atualizaCabecalho(arquivoSaida);
        arquivoSaida.close();
        long fim = System.currentTimeMillis();
        System.out.println("Tempo de compressão em milissegundos: " + (fim - inicio));
        return fim - inicio;
    }

    /*
     * private void calculoEntropia(String [] caractereCodificado)throws Exception
     * {
     * FileWriter arq=new FileWriter(prefixo+"entropiaVersao"+versao+".txt",true);
     * int numBytes=0;
     * for (NoHuffman no : arrayHuffman) {//itera pelo array contabilizando as
     * repetições para cálculo da probabilidade
     * numBytes+=no.repeticao;
     * }
     * double entropia[]=new double[tamanhoAlfabeto];
     * double entropiaTotal=0;
     * int posMaior=0;
     * for(int c=0;c<tamanhoAlfabeto;c++)
     * {
     * 
     * double probabilidade=BigDecimal.divide(arrayHuffman[c].repeticao/numBytes);
     * double aux=Math.log10(probabilidade)/Math.log10(2);//cálcula o log na base
     * dois da probabilidade, mas alguns números são muito pequenos para aparecer em
     * um double, e bigDecimal não tem função de log
     * entropia[c]=-1*aux*probabilidade;//fórmula da entropia
     * entropiaTotal=entropiaTotal+entropia[c];
     * arq.write("Entropia do char "+(char)c+" é "
     * +entropia+", codificacao utilizada "+caractereCodificado);
     * if(caractereCodificado[c].length()>posMaior)
     * {
     * posMaior=c;
     * }
     * }
     * arq.write("Entropia total "
     * +entropiaTotal+". O caractere com o maior tamanho foi: "+caractereCodificado[
     * posMaior]);
     * arq.close();
     * 
     * }
     */
    /**
     * Conta a repetição de caracteres, criando um novo nó de huffman se este não
     * existir ou, se
     * existir, incrementando a repetição.
     * 
     * @param array de nós de huffman
     * @throws Exception
     */
    private void contarRepeticoes(NoHuffman[] array) throws Exception {
        RAF raf = new RAF(caminhoDados, "r");
        raf.movePointerToStart();
        char caractere;
        while (raf.canRead()) {
            int posicao = raf.readUnsignedByte();
            caractere = (char) posicao;
            if (array[posicao] == null) {
                array[posicao] = new NoHuffman(caractere);
            } else {
                array[posicao].incrementaRepeticao();
            }
        }
        raf.close();
    }

    /**
     * Cria a árvore a partir de uma fila de prioridade
     * 
     * @param array array de nós
     * @return retorna a raiz da árvore trie
     */
    private NoHuffman montaArvoreHuffman(NoHuffman[] array) {
        PriorityQueue<NoHuffman> fila = new PriorityQueue<NoHuffman>(tamanhoAlfabeto, new ComparadorNoHuffman());
        for (int c = 0; c < tamanhoAlfabeto; c++)// adiciona os elementos não nulos em uma fila de
                                                 // prioridade
        {
            if (array[c] != null) {
                fila.add(array[c]);
                numeroNosFolha++;
            }
        }
        while (fila.size() > 1)// enquanto a fila tiver mais de um elemento junta os dois nós com
                               // menor uso
        {
            NoHuffman novoNo = new NoHuffman();
            NoHuffman esq = fila.poll();
            NoHuffman dir = fila.poll();
            novoNo.repeticao = esq.repeticao + dir.repeticao;
            novoNo.esq = esq;
            novoNo.dir = dir;
            fila.add(novoNo);
        }
        NoHuffman raiz = fila.poll();
        return raiz;
    }

    /**
     * caminha pela árvore para se verificar o caminho a ser feito para chegar no
     * caractere folha.
     * 
     * @param no  nó huffman que será verificado
     * @param str array de strings que tem o tamanho do alfabeto, grava as posições
     *            uma a uma
     * @param s   concatena-se '0's e '1's até o caminho ser codificado
     */
    private void montaTabela(NoHuffman no, String[] str, String s) throws Exception {
        if (no.eFolha() == false) {
            montaTabela(no.esq, str, s + 0);
            montaTabela(no.dir, str, s + 1);
        } else {
            str[no.caractere] = s;
            // debugaTabela(no.caractere, s);
        }
    }

    /*
     * private void debugaTabela(char c, String s) throws Exception
     * {
     * File arq = new File(prefixo + "tabela.txt");
     * FileWriter teste = new FileWriter(arq, true);
     * debugaTabela(c, s, teste);
     * teste.close();
     * }
     */

    /*
     * private void debugaTabela(char c, String s, FileWriter teste) throws
     * Exception
     * {
     * teste.write((int)c+", "+c + ", " + s + "," + "\n");
     * }
     */

    /*
     * private void imprimeArvore(NoHuffman no) throws IOException { RAF teste = new
     * RAF(prefixo +
     * "testeArvore.db", "rw"); imprimeArvore(no, teste); teste.close(); }
     */

    /*
     * private void imprimeArvore(NoHuffman no, RAF teste) throws IOException { if
     * (no.eFolha() ==
     * true) { teste.writeUTF("true ");
     * 
     * teste.writeChar(no.caractere);
     * 
     * } else { teste.writeUTF("false "); imprimeArvore(no.esq, teste);
     * imprimeArvore(no.dir,
     * teste); } }
     */

    private void escreveArvore(NoHuffman no, RAF arquivoSaida, Cabecalho cabecalho) throws Exception {
        cabecalho.incrementaNumeroNos();
        if (no.eFolha() == true) {
            arquivoSaida.writeBoolean(true);
            int ch = (int) no.caractere;
            arquivoSaida.writeByte(ch);
            // debugaUmByte(no.caractere);
        } else {
            arquivoSaida.writeBoolean(false);
            escreveArvore(no.esq, arquivoSaida, cabecalho);
            escreveArvore(no.dir, arquivoSaida, cabecalho);
        }
    }

    /**
     * Este método concatena no final da representação da árvore trie o texto
     * compactado, no último
     * byte a ordem de escrita deve ser diferente, e por consequência a leitura.
     * 
     * @param caminho      contem as codificações de huffman para cada caractere do
     *                     texto
     * @param arquivoSaida arquivo compactado
     * @return retorna quantos são os bits importantes do último byte.
     * @throws Exception
     */
    private void escreveTexto(String[] caminho, RAF arquivoSaida, Cabecalho cabecalho)
            throws Exception {
        RAF arquivoEntrada = new RAF(caminhoDados, "r");
        arquivoEntrada.movePointerToStart();
        String compactado = "";
        int posicao;
        while (arquivoEntrada.canRead()) {
            posicao = arquivoEntrada.readUnsignedByte();
            // debugaUmByte((char)posicao);
            compactado = compactado + caminho[posicao];
            while (compactado.length() > 8) {
                String subString = compactado.substring(0, 8);
                int escrever = Integer.parseInt(subString, 2);
                compactado = compactado.substring(8);
                arquivoSaida.writeByte(escrever);
                cabecalho.incrementaNumeroBytes();

            }
        }
        // lógica de escrita do último byte, preencherá com zero o restante da string
        if (compactado.length() > 0) {
            cabecalho.setNumeroDeBitsUltimoByte(compactado.length());
            while (compactado.length() != 8) {
                compactado = compactado + '0';

            }
            int escrever = Integer.parseInt(compactado, 2);
            arquivoSaida.writeByte(escrever);
            cabecalho.incrementaNumeroBytes();
        }

        arquivoEntrada.close();
    }

    public long descompacta() throws Exception {
        long inicio = System.currentTimeMillis();
        RAF arquivoCompactado = new RAF(prefixo + nomeArquivoCompactado + versao + ".db", "r");
        Cabecalho cabecalho = new Cabecalho(arquivoCompactado);
        NoHuffman raiz = remontaArvoreHuffman(arquivoCompactado, cabecalho);
        leTexto(arquivoCompactado, raiz, cabecalho);
        long fim = System.currentTimeMillis();
        return fim - inicio;

    }

    /**
     * Remonta a árvore a partir do arquivo, para o correto funcionamento deve ser
     * acionado este método ao descompactar.
     * 
     * @param arquivoCompactado
     * @param cabecalho
     * @return
     * @throws Exception
     */
    private NoHuffman remontaArvoreHuffman(RAF arquivoCompactado, Cabecalho cabecalho)
            throws Exception {
        arquivoCompactado.seek(cabecalho.getInicioTabela());
        NoHuffman no = remontaArvoreHuffman(arquivoCompactado);
        return no;
    }

    /**
     * Este método não deve ser chamado diretamente e sim indiretamente pelo método
     * sobrecarregado! Lê um boolean, se verdadeiro, instancia um nó folha, se
     * falso, instancia um nó intermediário
     * 
     * @param arquivoCompactado
     * @return o nó raiz da árvore remontada
     * @throws Exception
     */
    private NoHuffman remontaArvoreHuffman(RAF arquivoCompactado) throws Exception {
        boolean folha = arquivoCompactado.readBoolean();
        NoHuffman no;
        if (folha == true) {
            int num = arquivoCompactado.readUnsignedByte();
            char c = (char) num;
            no = new NoHuffman(c);
        } else {
            no = new NoHuffman(-1, remontaArvoreHuffman(arquivoCompactado),
                    remontaArvoreHuffman(arquivoCompactado));
        }
        return no;
    }

    /**
     * Lógica para leitura do texto compactado, percorre a árvore TRIE e retira o
     * caractere desejado
     * 
     * @param arquivoCompactado
     * @param raiz
     * @param cabecalho
     * @throws Exception
     */
    private void leTexto(RAF arquivoCompactado, NoHuffman raiz, Cabecalho cabecalho)
            throws Exception {
        String instrucao = "";
        arquivoCompactado.seek(cabecalho.getInicioTexto());
        RAF arquivoDescompactado = new RAF(prefixo + nomeArquivoDescompactado + versao + ".db", "rw");
        int bytesLidos = 1;
        int posicaoChar = 0;
        while (bytesLidos < cabecalho.getNumeroBytes()) {
            NoHuffman no = raiz;
            while (no.eFolha() == false) {
                if (instrucao.length() <= posicaoChar) {
                    if (bytesLidos < cabecalho.getNumeroBytes()) {
                        instrucao = lerUmByte(arquivoCompactado);
                        posicaoChar = 0;
                        bytesLidos++;

                    } else {
                        instrucao = lerUltimoByte(arquivoCompactado, cabecalho);
                        posicaoChar = 0;
                        bytesLidos++;
                    }
                }
                if (instrucao.charAt(posicaoChar) == '1') {
                    no = no.dir;
                } else {
                    no = no.esq;
                }
                posicaoChar++;
            }
            char c = no.caractere;
            int posicao = (int) c;
            arquivoDescompactado.writeByte(posicao);
        }
        arquivoDescompactado.close();
    }

    /**
     * método para leitura de um byte do arquivo compactado,
     * 
     * @param arquivoCompactado
     * @return retorna uma String que representa o número guardado neste byte como
     *         um número binário
     * @throws IOException
     */
    private String lerUmByte(RAF arquivoCompactado) throws IOException {
        int valorByte = arquivoCompactado.readUnsignedByte();
        String resp = Integer.toBinaryString(valorByte);
        while (resp.length() < 8) {
            resp = '0' + resp;
        }
        return resp;
    }

    /**
     * Lógica de leitura do último byte
     * 
     * @param arquivoCompactado
     * @param cabecalho
     * @return
     * @throws IOException
     */
    private String lerUltimoByte(RAF arquivoCompactado, Cabecalho cabecalho) throws IOException {
        int valorByte = arquivoCompactado.readUnsignedByte();
        String resp = Integer.toBinaryString(valorByte);
        while (resp.length() < 8) {
            resp = resp + '0';
        }
        resp = resp.substring(0, cabecalho.getNumeroDeBitsUltimoByte());
        return resp;
    }

    /*
     * private void debugaUmByte(char c) throws Exception
     * {
     * RAF teste = new RAF(prefixo + "teste.db", "rw");
     * teste.writeByte((int) c);
     * teste.writeByte((byte) c);
     * teste.seek(teste.length() - 2);
     * System.out.println(
     * "CHAR " + c + " escrevendo como unsignedByte " + (char)
     * teste.readUnsignedByte());
     * teste.close();
     * 
     * }
     */

    public long getFileSize() {
        return new File(prefixo + nomeArquivoCompactado + versao + ".db").length();
    }
}
