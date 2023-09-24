package dao;

import main.*;
import model.*;
import hash.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HashDAO {
  private Hash hash;
  private RAF raf;

  public HashDAO() {
    raf = null;
    hash = null;
  }

  public HashDAO(String dbFilePath, Hash hash) throws FileNotFoundException {
    raf = new RAF(dbFilePath, "rw");
    this.hash = hash;
  }

  public int create(Player player) throws IOException {
    raf.movePointerToStart();
    player.setPlayerId(raf.readInt() + 1);

    raf.movePointerToStart();
    raf.writeInt(player.getPlayerId()); // Set header
    raf.movePointerToEnd();

    PlayerRegister playerRegister = new PlayerRegister(raf.getFilePointer(), false, player);
    raf.write(playerRegister.toByteArray());

    hash.insert(playerRegister);
    return player.getPlayerId();
  }

  public Player read(int id) throws IOException {
    PlayerRegister register = seek(id);
    if (register != null && register.getPlayer().getPlayerId() == id) {
      return register.getPlayer();
    }

    return null;
  }

  public PlayerRegister seek(int id) throws IOException {
    Index index = hash.read(id);
    if (index == null) {
      return null;
    }
    raf.seek(index.getPointer());

    PlayerRegister register = new PlayerRegister();
    register.fromFileIfNotTomb(raf);

    return register;
  }

  public boolean update(Player player) throws IOException {
    PlayerRegister pr = seek(player.getPlayerId());
    if (pr == null || pr.getPlayer().getPlayerId() != player.getPlayerId()) {
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

      pr.setPosition(raf.getFilePointer());
      hash.update(pr);

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

      return hash.delete(id);
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

  public Hash getHash() {
    return hash;
  }

}
