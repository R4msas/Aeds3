package dao;

import main.*;
import model.*;
import hash.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Realiza operações de CRUD em um arquivo binário de jogadores indexado por uma
 * classe que herda da interface Indexacao. Todas as operações são refletidas no
 * arquivo de índices.
 */
public class IndexDAO extends PlayerDAO {
  private Indexacao indexacao;

  /**
   * Construtor da classe HashDAO.
   * 
   * @param dbFilePath Nome e caminho do arquivo .db onde os registros serão
   *                   manipuladaos.
   * @param indexacao  de onde as operações de CRUD vão se basear, deve indexar o
   *                   arquivo binário de jogadores.
   * @throws FileNotFoundException Se não se encontrar o caminho procurado.
   */
  public IndexDAO(String dbFilePath, Indexacao indexacao) throws FileNotFoundException {
    this(new File(dbFilePath), indexacao);
  }

  /**
   * Construtor da classe HashDAO.
   * 
   * @param dbFile    Arquivo .db.
   * @param indexacao de onde as operações de CRUD vão se basear, deve indexar o
   *                  arquivo binário de jogadores.
   */
  public IndexDAO(File dbFile, Indexacao indexacao) {
    super(dbFile);
    this.indexacao = indexacao;
  }

  /**
   * Construtor que converte um objeto PlayerDAO em um IndexDAO.
   * 
   * @param playerDAO objeto antigo que deve ser convertido.
   * @param indexacao de onde as operações de CRUD vão se basear, deve indexar o
   *                  arquivo binário de jogadores.
   */
  public IndexDAO(PlayerDAO playerDAO, Indexacao indexacao) {
    this(playerDAO.dbFile, indexacao);
  }

  @Override
  public int create(Player player) throws IOException {
    long insertPosition = this.dbFile.length();
    int id = super.create(player);
    indexacao.insert(new Index(false, id, insertPosition));

    return id;
  }

  @Override
  public PlayerRegister seek(int id) throws IOException {
    Index index = indexacao.read(id);
    if (index == null) {
      return null;
    }

    RAF raf = new RAF(dbFile, "rw");
    raf.seek(index.getPointer());

    PlayerRegister register = new PlayerRegister();
    register.fromFile(raf, true);

    return register;
  }

  @Override
  public long update(Player player) throws IOException {
    long insertPosition = super.update(player);
    if (insertPosition >= 0) {
      Index newIndex = new Index(false, player.getPlayerId(), insertPosition);
      System.out.println(newIndex);
      indexacao.update(newIndex);
    }

    return insertPosition;
  }

  @Override
  public boolean delete(int id) throws IOException {
    return super.delete(id) && indexacao.delete(id);
  }

  public Indexacao getIndexacao() {
    return indexacao;
  }

  public void setIndexacao(Indexacao indexacao) {
    this.indexacao = indexacao;
  }

}
