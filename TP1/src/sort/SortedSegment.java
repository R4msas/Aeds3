package sort;

import java.io.IOException;

import main.RAF;
import model.PlayerRegister;

public class SortedSegment {
  private RAF raf;
  private PlayerRegister firstRegister;
  private int remainingReads;

  public SortedSegment(RAF raf) throws IOException {
    this(raf, 1);
  }

  public SortedSegment(RAF raf, PlayerRegister firstRegister) throws IOException {
    this.raf = raf;
    this.remainingReads = 1;
    setFirstRegister(firstRegister);
  }

  public SortedSegment(RAF raf, int remainingReads) throws IOException {
    this.raf = raf;
    this.remainingReads = remainingReads;
    this.firstRegister = new PlayerRegister();
    loadNextRegister();
  }

  public boolean isBiggerThan(SortedSegment that) {
    return this.firstRegister.isBiggerThan(that.firstRegister);
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
    if (!raf.canRead()) {
      setFirstRegister(null);
    } else if (this.canLoadNextRegister()) {
      PlayerRegister pr = new PlayerRegister();
      do {
        pr.fromFile(raf, true);
      } while (pr.isTombstone() && raf.canRead());

      if (!pr.isTombstone()) {
        setFirstRegister(pr);
      } else {
        setFirstRegister(null);
      }
    }
  }

  public SortedSegment loadNextIfBigger() throws IOException {
    if (!raf.canRead()) {
      setFirstRegister(null);
    } else if (this.canLoadNextRegister()) {
      PlayerRegister currentRegister = new PlayerRegister();
      do {
        currentRegister.fromFile(raf, true);
      } while (currentRegister.isTombstone() && raf.canRead());

      if (!currentRegister.isTombstone() && currentRegister.isBiggerThan(firstRegister)) {
        setFirstRegister(currentRegister);
      } else {
        setFirstRegister(null);
        if (!currentRegister.isTombstone()) {
          return new SortedSegment(raf, currentRegister);
        }
      }
    }

    return null;
  }

  public int getRemainingReads() {
    return remainingReads;
  }

  public void setRemainingReads(int remainingReads) {
    this.remainingReads = remainingReads;
  }

  public RAF getRaf() {
    return raf;
  }

  public void setRaf(RAF raf) {
    this.raf = raf;
  }

  public PlayerRegister getFirstRegister() {
    return firstRegister;
  }

  public void setFirstRegister(PlayerRegister firstRegister) {
    this.firstRegister = firstRegister;
  }
}
