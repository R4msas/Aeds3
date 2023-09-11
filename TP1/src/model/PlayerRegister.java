package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
        + "\nSize : " + this.size + "\n";

    if (player == null) {
      returnString += "null";
    } else {
      returnString += this.player.toString();
    }
    return returnString;
  }

  public boolean isBiggerThan(PlayerRegister that) throws NullPointerException {
    return this.player.isBiggerThan(that.player);
  }

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
   * @return Player, even if the register is marked as tombstone.
   */
  public Player fromFile(RandomAccessFile raf) throws IOException {
    setPosition(raf.getFilePointer());
    setTombstone(raf.readBoolean());
    setSize(raf.readInt());
    return setPlayer(raf);
  }

  /**
   * @return true if active register, fa if tombstone. FilePointer will still
   *         be set to the end of the register.
   * @throws IOException
   */
  public Player fromFileIfNotTomb(RandomAccessFile raf) throws IOException {
    setPosition(raf.getFilePointer());
    setTombstone(raf.readBoolean());
    setSize(raf.readInt());

    if (isTombstone()) {
      raf.seek(raf.getFilePointer() + size);
      return null;
    }

    return setPlayer(raf);
  }

  private Player setPlayer(RandomAccessFile raf) throws IOException {
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

  public int getSize() throws IOException {
    if (player != null) {
      int playerSize = this.player.toByteArray().length;
      setSize(playerSize > this.size ? playerSize : this.size);
      return this.size;
    } else {
      return -1;
    }
  }

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
