import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PlayerDAO {
  private PlayerService playerService;
  private RandomAccessFile raf;

  public PlayerDAO() {
    playerService = null;
    raf = null;
  }

  public PlayerDAO(PlayerService playerService) throws FileNotFoundException {
    setPlayerService(playerService);
  }

  public int create(Player player) throws IOException {
    raf.seek(0);
    player.setPlayerId(raf.readInt() + 1);

    raf.seek(0);
    raf.writeInt(player.getPlayerId()); // Set header

    raf.seek(raf.length());
    raf.write(player.toByteArray());

    return player.getPlayerId();
  }

  public Player read(int id) throws IOException {
    raf.seek(Integer.SIZE / 8);
    long position = raf.getFilePointer();

    while (position < raf.length()) {
      boolean tombstone = raf.readBoolean();
      int registerSize = raf.readInt();
      position += registerSize;

      if (tombstone == false) {
        raf.seek(position);
        continue;
      }

      byte[] bytes = new byte[registerSize];
      raf.read(bytes);

      Player temp = new Player();
      temp.fromByteArray(bytes);
      if (temp.getPlayerId() == id) {
        return temp;
      }
    }

    return null;
  }

  public boolean delete(int id) throws IOException {
    raf.seek(Integer.SIZE / 8);
    long position = raf.getFilePointer();

    while (position < raf.length()) {
      boolean tombstone = raf.readBoolean();
      int registerSize = raf.readInt();

      if (tombstone == false) {
        position += registerSize;
        raf.seek(position);
        continue;
      }

      byte[] bytes = new byte[registerSize];
      raf.read(bytes);

      Player temp = new Player();
      temp.fromByteArray(bytes);
      if (temp.getPlayerId() == id) {
        raf.seek(position);
        raf.writeBoolean(false);
        return true;
      }
    }

    return false;
  }

  public void close() throws IOException {
    raf.close();
  }

  public PlayerService getPlayerService() {
    return playerService;
  }

  public void setPlayerService(PlayerService playerService) throws FileNotFoundException {
    this.playerService = playerService;
    this.raf = new RandomAccessFile(playerService.getDBFilePath(), "rw");
  }

  @Override
  public String toString() {
    return "PlayerDAO [" + playerService.toString() + "\n]";
  }
}
