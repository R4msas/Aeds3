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

  PlayerRegister() {
  }

  PlayerRegister(boolean tombstone, Player player) {
    this(-1, tombstone, player);
  }

  PlayerRegister(long position, boolean tombstone, Player player) {
    setSize(size);
    setTombstone(tombstone);
    setPlayer(player);
  }

  @Override
  public String toString() {
    return "PlayerRegister" + "\nPosition: " + this.position + "\n Is Tombstone: " + this.tombstone + "\nSize : "
        + this.getSize() + "\nPlayer: {" +
        this.player.toString() + "}";
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    byte[] playerBytes = player.toByteArray();

    dos.writeBoolean(false);
    dos.writeInt(playerBytes.length);
    dos.write(playerBytes);
    dos.close();

    setSize(playerBytes.length);
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

  public int getSize() {
    if (this.size < 1 && this.player != null) {
      try {
        setSize(this.player.toByteArray().length);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return size;
  }

  private void setSize(int size) {
    this.size = size;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public long getPosition() {
    return position;
  }

  public void setPosition(long position) {
    this.position = position;
  }

}
