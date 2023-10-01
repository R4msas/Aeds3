package fileHandler;

import java.io.IOException;

import dao.IndexDAO;
import indexacao.Indexacao;
import indexacao.hash.Index;
import model.PlayerRegister;

/**
 * Extende DBHandler para indexar os registros arquivo binário criado.
 */
public class IndexFileHandler extends DBHandler {
  protected Indexacao indexacao;

  /**
   * Construtor da classe IndexFileHandler.
   * 
   * @param biggestID  será o cabeçalho do arquivo .db. É alterado pelas funções
   *                   de leitura pelo maior cabeçalho lido.
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   * @param indexacao  estrutura que indexará os registros.
   */
  public IndexFileHandler(int biggestID, String dbFilePath, Indexacao indexacao) {
    super(biggestID, dbFilePath);
    this.indexacao = indexacao;
  }

  /**
   * Construtor da classe IndexFileHandler. Seta o atributo maior id para -1.
   * 
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   * @param indexacao  estrutura que indexará os registros.
   */
  public IndexFileHandler(String dbFilePath, Indexacao indexacao) {
    this(-1, dbFilePath, indexacao);
  }

  /**
   * Construtor que converte um objeto DBHandler em um IndexFileHandler.
   * 
   * @param dbHandler objeto antigo que deve ser convertido.
   * @param indexacao estrutura que indexará os registros.
   */
  public IndexFileHandler(DBHandler dbHandler, Indexacao indexacao) {
    this(dbHandler.biggestID, dbHandler.dbFilePath, indexacao);
  }

  /**
   * Indexa e constrói o arquivo binário de índices dos registros lidos em um
   * arquivo .db.
   * 
   * @return Objeto DAO que permite alterar o arquivo criado.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  public IndexDAO buildIndexFromDB() throws IOException {
    PlayerRegister[] toInsert = readRegistersFromDB();
    for (PlayerRegister playerRegister : toInsert) {
      indexacao.insert(new Index(playerRegister));
    }

    return new IndexDAO(dbFilePath, indexacao);
  }

  public Indexacao getIndexacao() {
    return indexacao;
  }

  public void setIndexacao(Indexacao indexacao) {
    this.indexacao = indexacao;
  }
}
