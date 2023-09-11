package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import model.PlayerRegister;

public class RAF extends RandomAccessFile {

  public RAF(File file, String mode) throws FileNotFoundException {
    super(file, mode);
  }

  public RAF(String filePath, String mode) throws FileNotFoundException {
    super(filePath, mode);
  }

  public boolean canRead() throws IOException {
    return this.getFilePointer() < this.length();
  }

  public void seek(PlayerRegister playerRegister) throws IOException {
    this.seek(playerRegister.getPosition());
  }

  public void movePointerToStart() throws IOException {
    this.seek(0);
  }

  public void movePointerToEnd() throws IOException {
    this.seek(this.length());
  }
}
