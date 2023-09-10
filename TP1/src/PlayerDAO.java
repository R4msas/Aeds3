import java.io.FileNotFoundException;
import java.io.IOException;

public class PlayerDAO {
  private FileHandler fileHandler;
  private RAF raf;

  public PlayerDAO() {
    fileHandler = null;
    raf = null;
  }

  public PlayerDAO(FileHandler fileHandler) throws FileNotFoundException {
    setPlayerService(fileHandler);
  }

  @Override
  public String toString() {
    return "PlayerDAO [" + fileHandler.toString() + "\n]";
  }

  public int create(Player player) throws IOException {
    raf.movePointerToStart();
    player.setPlayerId(raf.readInt() + 1);

    raf.movePointerToStart();
    raf.writeInt(player.getPlayerId()); // Set header

    PlayerRegister playerRegister = new PlayerRegister(false, player);

    raf.movePointerToEnd();
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

    while (raf.canRead()) {
      PlayerRegister register = new PlayerRegister();
      Player player = register.fromFileIfNotTomb(raf);
      if (!register.isTombstone() && player.getPlayerId() == id) {
        return register;
      }
    }

    return null;
  }

  public boolean update(Player player) throws IOException {
    PlayerRegister pr = this.seek(player.getPlayerId());
    if (pr == null) {
      return false;
    }
    raf.seek(pr);

    int previousSize = pr.getSize();
    pr.setPlayer(player);

    if (pr.getSize() <= previousSize) {
      raf.write(pr.toByteArray());
    } else {
      raf.writeBoolean(true);
      raf.movePointerToEnd();
      pr.resetSize();
      raf.write(pr.toByteArray());
    }

    return true;
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
    return fileHandler;
  }

  public void setPlayerService(FileHandler fileHandler) throws FileNotFoundException {
    this.fileHandler = fileHandler;
    this.raf = new RAF(fileHandler.getDBFilePath(), "rw");
  }
}
