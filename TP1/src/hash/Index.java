package hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.*;

public class Index {
  private boolean tombstone;
  private int id;
  private long pointer;

  public Index() {
    tombstone = true;
    id = 0;
    pointer = -1;
  }

  public Index(boolean tombstone, int id, long pointer) {
    this.tombstone = tombstone;
    this.id = id;
    this.pointer = pointer;
  }

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
