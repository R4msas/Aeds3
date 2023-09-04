import java.io.IOException;
import java.io.RandomAccessFile;

public class SortedSegment {
  private RandomAccessFile raf;
  private PlayerRegister firstRegister;
  private int remainingReads;

  public SortedSegment(RandomAccessFile raf, int remainingReads) throws IOException {
    this.raf = raf;
    this.remainingReads = remainingReads;
    this.firstRegister = new PlayerRegister();
    loadNextRegister();
  }

  public boolean isBiggerThan(SortedSegment that) {
    return this.firstRegister.getPlayer().getPlayerId() > that.firstRegister.getPlayer().getPlayerId();
  }

  public static SortedSegment getSmallest(SortedSegment[] sortedSegments) throws IOException {
    int i;
    for (i = 0; i < sortedSegments.length && !sortedSegments[i].canLoadNextRegister(); i++) {
    }

    if (i < sortedSegments.length) {
      SortedSegment smallest = sortedSegments[i];
      for (int j = i; j < sortedSegments.length; j++) {
        if (sortedSegments[j].canLoadNextRegister() && smallest.isBiggerThan(sortedSegments[j])) {
          smallest = sortedSegments[j];
        }
      }

      return smallest;
    }

    return null;
  }

  public boolean canLoadNextRegister() throws IOException {
    return remainingReads >= 0 && this.firstRegister != null;
  }

  public void loadNextRegister() throws IOException {
    setRemainingReads(--remainingReads);
    if (raf.getFilePointer() >= raf.length()) {
      setFirstRegister(null);
    } else if (canLoadNextRegister()) {
      PlayerRegister pr = new PlayerRegister();
      pr.fromFileIfNotTomb(raf);
      setFirstRegister(pr);
    }
  }

  public int getRemainingReads() {
    return remainingReads;
  }

  public void setRemainingReads(int remainingReads) {
    this.remainingReads = remainingReads;
  }

  public RandomAccessFile getRaf() {
    return raf;
  }

  public void setRaf(RandomAccessFile raf) {
    this.raf = raf;
  }

  public PlayerRegister getFirstRegister() {
    return firstRegister;
  }

  private void setFirstRegister(PlayerRegister firstRegister) {
    this.firstRegister = firstRegister;
  }
}
