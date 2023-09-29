package hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.*;

/**
 * Armazena o id e a posição de um registro de jogador em um arquivo binário.
 * Possui também uma lápide que indica se deve ser lido ou não.
 */
public class Index {
  private boolean tombstone;
  private int id;
  private long pointer;

  /**
   * Construtor padrão de índice, setado como lápide.
   */
  public Index() {
    tombstone = true;
    id = 0;
    pointer = -1;
  }

  /**
   * Construtor da classe Index.
   * 
   * @param tombstone indica se o índice é válido ou não. Indices marcados como
   *                  lápide no arquivo binário são ignorados.
   * @param id        id do jogador que referencia.
   * @param pointer   posição do registro que referencia.
   */
  public Index(boolean tombstone, int id, long pointer) {
    this.tombstone = tombstone;
    this.id = id;
    this.pointer = pointer;
  }

  /**
   * Cria um índice a partir de um registro
   * 
   * @param playerRegister
   */
  public Index(PlayerRegister playerRegister) {
    this(playerRegister.isTombstone(), playerRegister.getPlayer().getPlayerId(), playerRegister.getPosition());
  }

  public static int sizeof() {
    return Byte.BYTES + Integer.BYTES + Long.BYTES;
  }

  public boolean isBiggerThan(Index that) {
    return this.id > that.id;
  }

  @Override
  public String toString() {
    return "Index {id=" + id + ", pointer=" + pointer + "}";
  }

  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    setTombstone(dis.readBoolean());
    setId(dis.readInt());
    setPointer(dis.readLong());

    dis.close();
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeBoolean(tombstone);
    dos.writeInt(id);
    dos.writeLong(pointer);

    dos.close();
    return baos.toByteArray();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getPointer() {
    return pointer;
  }

  public void setPointer(long pointer) {
    this.pointer = pointer;
  }

  public boolean isTombstone() {
    return tombstone;
  }

  public void setTombstone(boolean tombstone) {
    this.tombstone = tombstone;
  }

}
