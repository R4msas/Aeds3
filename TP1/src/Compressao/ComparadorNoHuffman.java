package Compressao;

import java.util.Comparator;

public class ComparadorNoHuffman implements Comparator<NoHuffman> {
    public int compare(NoHuffman x, NoHuffman y) {
        return x.repeticao - y.repeticao;
      }
}
