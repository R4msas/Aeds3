package dao;

import main.*;
import model.*;
import hash.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Realiza operações de CRUD em um arquivo binário de jogadores indexado por
 * Hash. Todas as operações são refletidas no arquivo de índices
 */
public class HashDAO extends PlayerDAO {
  private Hash hash;

  /**
   * Construtor da classe HashDAO.
   * 
   * @param dbFilePath Nome e caminho do arquivo .db onde os registros serão
   *                   manipuladaos.
   * @param hash       de onde as operações de CRUD vão se basear, deve estar
   *                   linkado ao arquivo .db.
   * @throws FileNotFoundException Se não se encontrar o caminho procurado.
   */
  public HashDAO(String dbFilePath, Hash hash) throws FileNotFoundException {
    this(new File(dbFilePath), hash);
  }

  /**
   * Construtor da classe HashDAO.
   * 
   * @param dbFile Arquivo .db.
   * @param hash   de onde as operações de CRUD vão se basear, deve estar linkado
   *               ao arquivo .db.
   */
  public HashDAO(File dbFile, Hash hash) {
    super(dbFile);
    this.hash = hash;
  }

  @Override
  public int create(Player player) throws IOException {
    long insertPosition = this.dbFile.length();
    int id = super.create(player);
    hash.insert(new Index(false, id, insertPosition));

    return id;
  }

  @Override
  public PlayerRegister seek(int id) throws IOException {
    Index index = hash.read(id);
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
      hash.update(new Index(false, player.getPlayerId(), insertPosition));
    }

    return insertPosition;
  }

  @Override
  public boolean delete(int id) throws IOException {
    return super.delete(id) && hash.delete(id);
  }

  public Hash getHash() {
    return hash;
  }

  public void setHash(Hash hash) {
    this.hash = hash;
  }

}
