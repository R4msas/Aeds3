package casamentoDePadroes;

public class KMP {
    private final int R;
    private final int m;
    private int[][] dfa;
    public static int comparisons;
/**
 * Preprocessa a string
 * @param pat
 */
    public KMP(String pat) {
        this.R = 256;
        this.m = pat.length();

        dfa = new int[R][m];
        dfa[pat.charAt(0)][0] = 1;
        for (int x = 0, j = 1; j < m; j++)
        {
            for (int c = 0; c < R; c++)
                dfa[c][j] = dfa[c][x];
            dfa[pat.charAt(j)][j] = j + 1;
            x = dfa[pat.charAt(j)][x];
        }
    }
/**
 * Preprocessa o vetor de caracteres
 * @param pattern
 * @param R
 */
    public KMP(char[] pattern, int R) {
        this.R = R;
        this.m = pattern.length;

        int m = pattern.length;
        dfa = new int[R][m];
        dfa[pattern[0]][0] = 1;
        for (int x = 0, j = 1; j < m; j++)
        {
            for (int c = 0; c < R; c++)
                dfa[c][j] = dfa[c][x];
            dfa[pattern[j]][j] = j + 1;
            x = dfa[pattern[j]][x];
        }
    }
/**
 * Retorna um booleano se o padrão for encontrado
 * @param txt
 * @return
 */
    public boolean search(String txt)
    {
        boolean resp = false;
        int n = txt.length();
        int i, j;
        for (i = 0, j = 0; i < n && j < m; i++)
        {
            comparisons++;
            j = dfa[txt.charAt(i)][j];
        }
        if (j == m)
        {
            resp = true;
        }
        return resp;
    }
    /**
     * Retorna um booleano indicando se o padrão foi encontrado no texto
     * @param text
     * @return
     */
    public boolean search(char[] text)
    {
        boolean resp = false;
        int n = text.length;
        int i, j;
        for (i = 0, j = 0; i < n && j < m; i++)
        {
            j = dfa[text[i]][j];
            comparisons++;
        }
        if (j == m)
        {
            resp = true;
        }
        return resp;
    }
}

