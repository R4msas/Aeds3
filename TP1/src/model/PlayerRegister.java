package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Representa o registro do Jogador no arquivo .db, possui como atributos a
 * posição em que o registro é lido ou foi escrito no arquivo, se é lápide, qual
 * o tamanho e qual tupla de jogador foi representada.
 */
public class PlayerRegister {
  private long position;
  private boolean tombstone;
  private int size;
  private Player player;

  public PlayerRegister() {
  }

  public PlayerRegister(boolean tombstone, Player player) throws IOException {
    this(-1, tombstone, player);
  }

  public PlayerRegister(long position, boolean tombstone, Player player) throws IOException {
    setPosition(position);
    setTombstone(tombstone);
    setPlayer(player);
  }

  @Override
  public String toString() {
    String returnString = "PlayerRegister" + "\nPosition: " + this.position + "\nIs Tombstone: " + this.tombstone
        + "\nSize : " + this.size + "\n" + (player == null ? "Player: null" : player);
    return returnString;
  }

  /**
   * Compara o ID do jogador do registro que chamou a função com o ID do registro
   * que é passado como parâmetro.
   * 
   * @param that Registro que quer se descobrir se o ID do jogador contido no
   *             registro é maior ou não.
   * @return Se ID neste registro for maior que naquele ou falso se o jogador do
   *         outro registro for null.
   */
  public boolean isBiggerThan(PlayerRegister that) {
    return this.player.isBiggerThan(that.player);
  }

  /**
   * Transforma o Regstro em array de bytes.
   * 
   * @return byte[] conténdo os dados do jogador na seguinte ordem: lápide
   *         (boolean), tamanho (int), jogador (player.toByteArray()), se não
   *         nulo.
   * @throws IOException Erro na escrita dos bytes.
   */
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeBoolean(isTombstone());
    dos.writeInt(getSize());
    if (player != null) {
      dos.write(player.toByteArray());
    }
    dos.close();

    return baos.toByteArray();
  }

  /**
   * Substitui os atributos do registro pelos que lê em um array de bytes.
   * 
   * @param byteArray deve conter as informações a serem importadas pelo registro,
   *                  na seguinte ordem: lápide (boolean), tamanho do registro
   *                  (int), jogador (player.fromByteArray()).
   * @throws IOException Erro na leitura dos bytes
   * @return O jogador que foi lido ao se contruir o registro. Útil de se
   *         armazenar em uma variável se o método getPlayer() estiver se
   *         repetindo em um escopo.
   */
  public Player fromByteArray(byte[] bytes) throws IOException {
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

    setTombstone(dis.readBoolean());
    setSize(dis.readInt());

    Player temp = new Player();
    temp.fromByteArray(dis.readNBytes(size));
    dis.close();

    setPlayer(temp);
    return temp;
  }

  /**
   * Substitui os atributos do registro pelos que lê em um arquivo de bytes e anda
   * o RandomAccessFile o tamanho do registro, seja ele lápide ou não.
   * 
   * @param raf                   Accesso ao arquivo de onde o registro será
   *                              extraído a partir da posição que se encontra -
   *                              RandomAccessFile.getFilePointer().
   * @param shouldIgnoreTombstone Indica se o jogador armazenado no arquivo deve
   *                              ser lido mesmo se for lápide. Se marcado como
   *                              true, o objeto de tipo PlayerRegister permance
   *                              válido, porém o jogador apontado permanecerá o
   *                              que já era armazenado pela classe. Não ler o
   *                              jogador lápide se ele não for útil torna o
   *                              programa mais eficiente, já que a quantidade de
   *                              consultas em memória secundária é reduzida.
   * @return O jogador que foi lido ao se contruir o registro. Útil de se
   *         armazenar em uma variável se o método getPlayer() estiver se
   *         repetindo em um escopo.
   * @throws IOException Erro lendo o arquivo ou fazendo parse dos bytes lidos.
   */
  public Player fromFile(RandomAccessFile raf, boolean shouldIgnoreTombstone) throws IOException {
    setPosition(raf.getFilePointer());
    setTombstone(raf.readBoolean());
    setSize(raf.readInt());

    if (shouldIgnoreTombstone && isTombstone()) {
      raf.seek(raf.getFilePointer() + size);
      return null;
    }

    byte[] bytes = new byte[size];
    raf.read(bytes);

    Player temp = new Player();
    temp.fromByteArray(bytes);
    setPlayer(temp);
    return temp;
  }

  public boolean isTombstone() {
    return tombstone;
  }

  public void setTombstone(boolean tombstone) {
    this.tombstone = tombstone;
  }

  /**
   * Verifica se o tamanho do jogador que armazena em bytes é maior do que o
   * tamanho que lhe foi informado, se sim aumenta, se não, mantém o que possui.
   * 
   * @return O tamanho atualizado se possuir um jogador ou -1 se não possuir um
   *         jogador salvo.
   * @throws IOException Erro na conversão do jogador para um array de bytes.
   */
  public int getSize() throws IOException {
    if (player != null) {
      int playerSize = this.player.toByteArray().length;
      setSize(playerSize > this.size ? playerSize : this.size);
      return this.size;
    } else {
      return -1;
    }
  }

  /**
   * Muda o tamanho armazenado na classe independente do valor anterior, caso o
   * jogador seja não nulo.
   * 
   * @throws IOException Erro convertendo o jogador para array de bytes.
   */
  public void resetSize() throws IOException {
    if (player != null) {
      setSize(this.player.toByteArray().length);
    }
  }

  private void setSize(int size) {
    this.size = size;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) throws IOException {
    this.player = player;
  }

  public long getPosition() {
    return position;
  }

  public void setPosition(long position) {
    this.position = position;
  }

}
