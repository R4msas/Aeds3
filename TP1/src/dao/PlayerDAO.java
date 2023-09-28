package dao;

import java.io.File;
import main.RAF;
import java.io.FileNotFoundException;
import java.io.IOException;

import model.Player;
import model.PlayerRegister;

/**
 * Realiza operações de CRUD de Jogadores em arquivo sequencial.
 */
public class PlayerDAO {
  private File dbFile;

  public PlayerDAO() {
    dbFile = null;
  }

  /**
   * Construtor da classe PlayerDAO.
   * 
   * @param dbFilePath Nome e caminho do arquivo .db onde os registros serão
   *                   manipuladaos.
   * @throws FileNotFoundException Se não se encontrar o caminho procurado.
   */
  public PlayerDAO(String dbFilePath) throws FileNotFoundException {
    this(new File(dbFilePath));
  }

  /**
   * Construtor da classe PlayerDAO.
   * 
   * @param dbFile Arquivo .db.
   */
  public PlayerDAO(File dbFile) {
    this.dbFile = dbFile;
  }

  /**
   * Insere um jogador ao final arquivo .db e altera seu id para o maior id no
   * arquivo
   * .db + 1.
   * 
   * @param player Jogador que será criado um registro e escrito.
   * @return Novo id do jogador escrito (maior + 1).
   * @throws IOException Erro ao manipular o arquivo.
   */
  public int create(Player player) throws IOException {
    RAF dbFileRAF = new RAF(dbFile, "rw");
    dbFileRAF.movePointerToStart();
    int biggestID = dbFileRAF.readInt();
    player.setPlayerId(biggestID + 1);

    // Substitui o antigo maior id pelo id do novo jogador no arquivo binário.
    dbFileRAF.movePointerToStart();
    dbFileRAF.writeInt(player.getPlayerId());

    // Escreve o novo registro no final.
    PlayerRegister playerRegister = new PlayerRegister(false, player);
    dbFileRAF.movePointerToEnd();
    dbFileRAF.write(playerRegister.toByteArray());

    dbFileRAF.close();
    return player.getPlayerId();
  }

  /**
   * Procura um jogador pelo id informado.
   * 
   * @param id do jogador que se deseja encontrar
   * @return O Jogador com id correspondente, se encontrado um registro válido.
   *         Nulo do contrário.
   * @throws IOException Erro na manipulaçao do arquivo.
   */
  public Player read(int id) throws IOException {
    PlayerRegister register = seek(id);
    if (register != null) {
      return register.getPlayer();
    }

    return null;
  }

  /**
   * Procura sequencialmente o id do jogador desejado no arquivo com registros.
   * Ajuda nos métodos de leitura, atualização e deleção.
   * 
   * @param id Id do jogador que se quer encontrar a posição
   * @return Registro com posição, status (lápide ou não) e o jogador armazenado,
   *         caso seja encontrado um registro válido com o id informado e null do
   *         contrário.
   * @throws IOException Erro na manipulação do arquivo.
   */
  public PlayerRegister seek(int id) throws IOException {
    RAF dbFileRAF = new RAF(dbFile, "r");
    dbFileRAF.movePointerToStart();

    // Pula o cabeçalho
    dbFileRAF.skipBytes(Integer.BYTES);

    while (dbFile.canRead()) {
      PlayerRegister register = new PlayerRegister();
      Player player = register.fromFile(dbFileRAF, true);

      // Retorna o registro se o id for igual ao procurado.
      if (!register.isTombstone() && player.getPlayerId() == id) {
        dbFileRAF.close();
        return register;
      }
    }

    dbFileRAF.close();
    return null;
  }

  /**
   * Substitui os dados de um jogador por outro de mesmo id. Se o tamanho do novo
   * jogador em bytes for maior do que o antigo, o antigo é marcado como lápide e
   * o novo jogador é inserido ao final do arquivo. Se o novo jogador tiver o
   * mesmo tamanho ou o tamanho for menor, utiliza-se a mesma posição.
   * 
   * @param player contém o id do jogador que será atualizado e os dados que devem
   *               ser escrtios.
   * @return true se encontrou um jogador válido com o id igual do jogador que se
   *         passou como parâmetro, false do contrário.
   * @throws IOException Erro na manipulação do arquivo.
   */
  public boolean update(Player player) throws IOException {
    PlayerRegister pr = seek(player.getPlayerId());
    if (pr == null) {
      return false;
    }

    // Posiciona o ponteiro raf no começo do registro que se vai alterar.
    RAF dbFileRAF = new RAF(dbFile, "rw");
    dbFileRAF.seek(pr);

    // Prepara o registro para escrita e salva o tamanho anterior.
    int previousSize = pr.getSize();
    pr.setPlayer(player);

    if (pr.getSize() <= previousSize) {
      dbFileRAF.write(pr.toByteArray());
    } else {
      dbFileRAF.writeBoolean(true);
      dbFileRAF.movePointerToEnd();
      pr.resetSize();
      dbFileRAF.write(pr.toByteArray());
    }

    dbFileRAF.close();
    return true;
  }

  /**
   * Marca um registro como lápide se encontrar um registro válido com o id
   * informado.
   * 
   * @param id do registro que se quer deletar
   * @returntrue se encontrou um jogador válido com o id igual ao que se passou
   *             como parâmetro, false do contrário.
   * @throws IOException Erro de manipulação de arquivo.
   */
  public boolean delete(int id) throws IOException {
    PlayerRegister register = seek(id);
    if (register != null) {
      RAF dbFileRaf = new RAF(dbFile, "rw");
      dbFileRaf.seek(register.getPosition());

      dbFileRaf.writeBoolean(true);
      dbFileRaf.close();

      return true;
    }

    return false;
  }

  public File getDbFile() {
    return dbFile;
  }

  public void setDbFile(String dbFilePath) throws FileNotFoundException {
    setDbFile(new File(dbFilePath, "rw"));
  }

  public void setDbFile(File dbFile) {
    this.dbFile = dbFile;
  }
}
