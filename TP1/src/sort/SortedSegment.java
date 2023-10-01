package sort;

import java.io.IOException;

import main.RAF;
import model.PlayerRegister;

/**
 * Ajuda a gerenciar os arquivos temporários e os registros lidos durante a
 * intercalação.
 */
public class SortedSegment {
  /**
   * Setado como null se o arquivo chegar ao final ou se não possuir mais
   * leituras.
   */
  private PlayerRegister firstRegister;
  private RAF raf;
  private int remainingReads;

  /**
   * Construtor da classe SortedSegment para ser utilizado em situações em que o
   * segmento deve ter tamanho variável. Carrega o primeiro registro diretamente
   * do RAF.
   * 
   * @param raf Random Access File que acessa o arquivo temporário.
   * @throws IOException Erro de manipulação do arquivo.
   */
  public SortedSegment(RAF raf) throws IOException {
    this(raf, 1);
  }

  /**
   * Construtor da classe SortedSegment para ser utilizado em situações em que o
   * segmento deve ter tamanho variável. Apenas seta o primeiro registro, não faz
   * leitura do RAF.
   * 
   * @param raf           Random Access File que acessa o arquivo temporário.
   * @param firstRegister primeiro registro lido para o novo segmento ordenado.
   * @throws IOException Erro de manipulação do arquivo.
   */
  public SortedSegment(RAF raf, PlayerRegister firstRegister) throws IOException {
    this.raf = raf;
    this.remainingReads = 0;
    setFirstRegister(firstRegister);
  }

  /**
   * Construtor da classe SortedSegment para ser utilizado em situações em que o
   * segmento deve ter tamanho fixo. Carrega o primeiro jogador diretamente do
   * RAF.
   * 
   * @param raf Random Access File que acessa o arquivo temporário.
   * @throws IOException Erro de manipulação do arquivo.
   */
  public SortedSegment(RAF raf, int remainingReads) throws IOException {
    this.raf = raf;
    this.remainingReads = remainingReads;
    this.firstRegister = new PlayerRegister();
    loadNextRegister();
  }

  /**
   * Verifica se este segmento tem um registro maior do que aquele.
   * 
   * @param that o segmento que quer-se descobrir se é maior ou não.
   * @return True se o registro for maior, false do contrário.
   */
  public boolean isBiggerThan(SortedSegment that) {
    return this.firstRegister.isBiggerThan(that.firstRegister);
  }

  /**
   * Procura o segmento que tem o menor registro.
   * 
   * @param sortedSegments
   * @return O menor segmento encontrado, null se nenhum possuir registro salvo.
   * @throws IOException
   */
  public static SortedSegment getSmallest(SortedSegment[] sortedSegments) throws IOException {
    boolean validSegment = false;
    int i;
    for (i = 0; i < sortedSegments.length; i++) {
      if (sortedSegments[i].canLoadNextRegister()) {
        validSegment = true;
        break;
      }
    }

    if (validSegment) {
      SortedSegment smallest = sortedSegments[i];
      for (int j = i; j < sortedSegments.length; j++) {
        if (sortedSegments[j].canLoadNextRegister() && smallest.isBiggerThan(sortedSegments[j])) {
          smallest = sortedSegments[j];
        }
      }

      return smallest;
    }

    return null;
  }

  /**
   * Indica se pode ler o próximo registro.
   * 
   * @return True se possui leituras remanescentes e se possui um primeiro
   *         registro não nulo, false do contrário.
   */
  public boolean canLoadNextRegister() {
    return remainingReads >= 0 && this.firstRegister != null;
  }

  /**
   * Substitui o primeiro registro pelo que ler no arquivo, se o arquivo ainda
   * possuir registros e se as leituras disponíveis não foram esgotadas.
   * 
   * @throws IOException Erro de manipulação do arquivo.
   */
  public void loadNextRegister() throws IOException {
    setRemainingReads(--remainingReads);
    if (!raf.canRead()) {
      setFirstRegister(null);
    } else if (this.canLoadNextRegister()) {
      PlayerRegister pr = new PlayerRegister();
      do {
        pr.fromFile(raf, true);
      } while (pr.isTombstone() && raf.canRead());

      if (!pr.isTombstone()) {
        setFirstRegister(pr);
      } else {
        setFirstRegister(null);
      }
    }
  }

  /**
   * Substitui o primeiro registro pelo que ler no arquivo, se o arquivo ainda
   * possuir registros e se o novo registro for maior. Do contrário seta o
   * primeiro jogador como null e retorna um novo segmento ordenado com o jogador
   * lido.
   * 
   * @return Novo segmento se o jogador lido for menor do que o anterior, null do
   *         contrário e se não houver mais jogadores para ler.
   * @throws IOException Erro de manipulação do arquivo.
   */
  public SortedSegment loadNextIfBigger() throws IOException {
    if (!raf.canRead()) {
      setFirstRegister(null);
    } else if (this.canLoadNextRegister()) {
      PlayerRegister currentRegister = new PlayerRegister();
      do {
        currentRegister.fromFile(raf, true);
      } while (currentRegister.isTombstone() && raf.canRead());

      if (!currentRegister.isTombstone() && currentRegister.isBiggerThan(firstRegister)) {
        setFirstRegister(currentRegister);
      } else {
        setFirstRegister(null);
        if (!currentRegister.isTombstone()) {
          return new SortedSegment(raf, currentRegister);
        }
      }
    }

    return null;
  }

  public int getRemainingReads() {
    return remainingReads;
  }

  public void setRemainingReads(int remainingReads) {
    this.remainingReads = remainingReads;
  }

  public RAF getRaf() {
    return raf;
  }

  public void setRaf(RAF raf) {
    this.raf = raf;
  }

  public PlayerRegister getFirstRegister() {
    return firstRegister;
  }

  public void setFirstRegister(PlayerRegister firstRegister) {
    this.firstRegister = firstRegister;
  }
}
