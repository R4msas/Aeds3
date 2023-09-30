package fileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dao.PlayerDAO;
import main.RAF;
import model.Player;
import model.PlayerRegister;

/**
 * Responsável por criar o arquivo binário de jogadores e fazer sua leitura
 * completa.
 * <p>
 * <strong>Para operações de inserir, ler, atualizar e deletar utilize classes
 * DAO.</strong>
 * </p>
 */
public class DBHandler {
  protected int biggestID;
  protected String dbFilePath;

  /**
   * Construtor da classe DBHandler.
   * 
   * @param biggestID  será o cabeçalho do arquivo .db. É alterado pelas funções
   *                   de leitura pelo maior cabeçalho lido.
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   */
  public DBHandler(int biggestID, String dbFilePath) {
    this.biggestID = biggestID;
    this.dbFilePath = dbFilePath;
  }

  /**
   * Construtor da classe DBHandler. Seta o atributo maior id para -1.
   * 
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   */
  public DBHandler(String dbFilePath) {
    this(-1, dbFilePath);
  }

  /**
   * Extrai os jogadores do arquivo .db especificado, ignorando lápides e
   * substitui o maior id armazenado pelo que ler no cabeçalho do arquivo.
   * 
   * @return Array de jogadores lidos
   * @throws IOException Erro de leitura.
   */
  public Player[] readFromDB() throws IOException {
    RAF raf = new RAF(dbFilePath, "r");
    biggestID = raf.readInt();

    ArrayList<Player> players = new ArrayList<>();
    while (raf.canRead()) {
      PlayerRegister pr = new PlayerRegister();
      Player player = pr.fromFile(raf, true);
      players.add(player);
    }

    raf.close();
    return players.toArray(new Player[0]);
  }

  /**
   * Extrai os registros do arquivo .db especificado, ignorando lápides e
   * substitui o maior id armazenado pelo que ler no cabeçalho do arquivo.
   * 
   * @return Array de registros lidos
   * @throws IOException Erro de leitura.
   */
  public PlayerRegister[] readRegistersFromDB() throws IOException {
    RAF raf = new RAF(dbFilePath, "r");
    biggestID = raf.readInt();

    ArrayList<PlayerRegister> players = new ArrayList<>();
    while (raf.canRead()) {
      PlayerRegister pr = new PlayerRegister();
      pr.fromFile(raf, true);
      players.add(pr);
    }

    raf.close();
    return players.toArray(new PlayerRegister[0]);
  }

  /**
   * Constrói um arquivo .db a partir de um array de jogadores.
   * <p>
   * <strong>Importante:</strong> se o arquivo .db já existir ele será apagado.
   * </p>
   * 
   * @param players array de jogadores que será inserido
   * @return Objeto DAO que permite alterar o arquivo criado.
   * 
   * @throws IOException Erro de escrita.
   */
  public PlayerDAO buildDBFile(Player[] players) throws IOException {
    File dbFile = new File(dbFilePath);
    if (dbFile.exists()) {
      dbFile.delete();
    }

    RAF raf = new RAF(dbFile, "rw");
    raf.writeInt(biggestID); // Cria cabeçalho

    for (Player player : players) {
      PlayerRegister pr = new PlayerRegister(false, player);
      raf.write(pr.toByteArray());
    }

    raf.close();
    return new PlayerDAO(dbFilePath);
  }

  public int getBiggestID() {
    return biggestID;
  }

  public void setBiggestID(int biggestID) {
    this.biggestID = biggestID;
  }

  public String getDbFilePath() {
    return dbFilePath;
  }

  public void setDbFilePath(String dbFilePath) {
    this.dbFilePath = dbFilePath;
  }

  @Override
  public String toString() {
    return "DBHandler [biggestID=" + biggestID + ", dbFilePath=" + dbFilePath + "]";
  }

}
