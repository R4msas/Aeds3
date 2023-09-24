package hash;

import main.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class Directory {
  private int depth;
  private long bucketPosition;

  public Directory(long position) {
    this();
    bucketPosition = position;
  }

  public Directory(int depth, long bucketPosition) {
    this.depth = depth;
    this.bucketPosition = bucketPosition;
  }

  public Directory() {
    depth = 0;
    bucketPosition = 0;
  }

  public static int sizeof() {
    return Integer.BYTES + Long.BYTES;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public long getBucketPosition() {
    return bucketPosition;
  }

  public void setBucketPosition(long bucketPosition) {
    this.bucketPosition = bucketPosition;
  }

  @Override
  public String toString() {
    return "Directory [depth=" + depth + ", bucketPosition=" + bucketPosition + "]";
  }

  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    depth = dis.readInt();
    bucketPosition = dis.readLong();

    dis.close();
  }

  public boolean fromFile(File inputFile, long registerStartingPosition) throws IOException {
    if (!inputFile.exists()) {
      return false;
    }

    RAF randomAccesFile = new RAF(inputFile, "r");
    randomAccesFile.seek(registerStartingPosition);

    byte[] bytes = new byte[Directory.sizeof()];
    randomAccesFile.read(bytes);
    fromByteArray(bytes);

    randomAccesFile.close();
    return true;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(depth);
    dos.writeLong(bucketPosition);

    dos.close();
    return baos.toByteArray();
  }
}
