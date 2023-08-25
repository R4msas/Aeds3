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
    raf.writeInt(player.getPlayerId());

    raf.seek(raf.length());
    raf.write(player.toByteArray());

    return player.getPlayerId();
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
