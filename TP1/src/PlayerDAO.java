import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PlayerDAO {
  private FileHandler playerService;
  private RandomAccessFile raf;

  public PlayerDAO() {
    playerService = null;
    raf = null;
  }

  public PlayerDAO(FileHandler playerService) throws FileNotFoundException {
    setPlayerService(playerService);
  }

  @Override
  public String toString() {
    return "PlayerDAO [" + playerService.toString() + "\n]";
  }

  public int create(Player player) throws IOException {
    raf.seek(0);
    player.setPlayerId(raf.readInt() + 1);

    raf.seek(0);
    raf.writeInt(player.getPlayerId()); // Set header

    PlayerRegister playerRegister = new PlayerRegister(false, player);

    raf.seek(raf.length());
    raf.write(playerRegister.toByteArray());

    return player.getPlayerId();
  }

  public Player read(int id) throws IOException {
    PlayerRegister register = seek(id);
    if (register != null) {
      return register.getPlayer();
    }

    return null;
  }

  public PlayerRegister seek(int id) throws IOException {
    raf.seek(Integer.SIZE / 8);

    while (raf.getFilePointer() < raf.length()) {
      PlayerRegister register = new PlayerRegister();
      Player player = register.fromFileIfNotTomb(raf);
      if (player != null && player.getPlayerId() == id) {
        return register;
      }
    }

    return null;
  }

  public boolean delete(int id) throws IOException {
    PlayerRegister register = seek(id);
    if (register != null && register.getPlayer().getPlayerId() == id) {
      raf.seek(register.getPosition());
      raf.writeBoolean(true);
      return true;
    }

    return false;
  }

  public void close() throws IOException {
    raf.close();
  }

  public FileHandler getPlayerService() {
    return playerService;
  }

  public void setPlayerService(FileHandler playerService) throws FileNotFoundException {
    this.playerService = playerService;
    this.raf = new RandomAccessFile(playerService.getDBFilePath(), "rw");
  }
}
