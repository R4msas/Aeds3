import java.io.EOFException;
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

    /// TODO Refactoring
    byte[] ba = player.toByteArray();
    raf.writeBoolean(true); // Writes tombstone
    raf.writeInt(ba.length);
    raf.write(ba);

    return player.getPlayerId();
  }

  public Player read(int id) throws IOException {
    raf.seek(Integer.SIZE);
    long position = raf.getFilePointer();

    while (position < raf.length()) {
      boolean tombstone = raf.readBoolean();
      int registerSize = raf.readInt();

      if (tombstone == false) {
        position += registerSize;
        raf.seek(position);
        continue;
      }

      // TODO Refactoring
      byte[] bytes = new byte[registerSize];
      raf.read(bytes);
      Player temp = new Player();
      temp.fromByteArray(bytes);
    }
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
