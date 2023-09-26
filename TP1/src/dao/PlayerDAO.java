package dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import main.RAF;
import model.Player;
import model.PlayerRegister;

public class PlayerDAO {
  private RAF raf;

  public PlayerDAO() {
    raf = null;
  }

  public PlayerDAO(String dbFilePath) throws FileNotFoundException {
    raf = new RAF(dbFilePath, "rw");
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
    raf.seek(Integer.BYTES);

    while (raf.canRead()) {
      PlayerRegister register = new PlayerRegister();
      Player player = register.fromFile(raf, true);
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
    if (register != null) {
      raf.seek(register.getPosition());
      raf.writeBoolean(true);
      return true;
    }

    return false;
  }

  public void close() throws IOException {
    raf.close();
  }

  public RAF getRaf() {
    return raf;
  }

  public void setRaf(String dbFilePath) throws FileNotFoundException {
    raf = new RAF(dbFilePath, "rw");
  }
}
