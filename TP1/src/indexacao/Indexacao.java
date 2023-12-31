package indexacao;

import java.io.IOException;

import indexacao.hash.Index;

public interface Indexacao {
  public void insert(Index indexToInsert) throws IOException;

  public Index read(int id) throws IOException;

  public boolean update(Index index) throws IOException;

  public boolean delete(int id) throws IOException;
}
