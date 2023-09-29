package fileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import dao.PlayerDAO;
import model.Player;

/**
 * Extende DBHandler para permitir sua integração com um arquivos fonte CSV.
 * <p>
 * <strong>Para operações de inserir, ler, atualizar e deletar utilize classes
 * DAO.</strong>
 * </p>
 */
public class CSVDBHandler extends DBHandler {
  private String csvFilePath;

  /**
   * Construtor da classe CSVDBHandler.
   * 
   * @param biggestID   será o cabeçalho do arquivo .db. É alterado pelas funções
   *                    readFrom pelo maior id encontrado.
   * @param dbFilePath  diretório e nome do arquivo csv onde registros serão
   *                    lidos/criados.
   * @param csvFilePath diretório e nome do arquivo csv de
   *                    onde os jogadores serão
   *                    extraídos.
   */
  public CSVDBHandler(int biggestID, String dbFilePath, String csvFilePath) {
    super(biggestID, dbFilePath);
    this.csvFilePath = csvFilePath;
  }

  public CSVDBHandler(String dbFilePath, String csvFilePath) {
    this(-1, dbFilePath, csvFilePath);
  }

  /**
   * Construtor que converte um objeto da classe DBHandler em CSVDBHandler.
   * 
   * @param dbHandler   objeto que será convertido.
   * @param csvFilePath diretório e nome do arquivo csv de
   *                    onde os jogadores serão
   *                    extraídos.
   */
  public CSVDBHandler(DBHandler dbHandler, String csvFilePath) {
    this(dbHandler.biggestID, dbHandler.dbFilePath, csvFilePath);
  }

  /**
   * Extrai os jogadores do arquivo .csv especificado.
   * 
   * @return Array de jogadores lidos.
   * @throws Exception Erro de leitura.
   */
  public Player[] readFromCSV() throws Exception {
    Scanner scanner = new Scanner(new File(csvFilePath));
    scanner.nextLine(); // Ignora o cabeçalho csv

    ArrayList<Player> array = new ArrayList<>();
    biggestID = -1; // Reset maior id
    while (scanner.hasNext()) {
      Player temp = new Player();
      temp.fromCSVLine(scanner.nextLine());
      array.add(temp);

      biggestID = biggestID >= temp.getPlayerId() ? biggestID : temp.getPlayerId();
    }
    scanner.close();

    return array.toArray(new Player[0]);
  }

  /**
   * Converte diretamente do arquivo csv para o arquivo .db.
   * <p>
   * <strong>Se o arquivo .db já existir ele será apagado.</strong>
   * </p>
   * 
   * @return Objeto DAO que permite alterar o arquivo criado.
   * @throws Exception   Erro de leitura
   * @throws IOException Erro de escrita
   */
  public PlayerDAO csvToDBFile() throws Exception {
    return buildDBFile(readFromCSV());
  }

  public String getCSVFilePath() {
    return csvFilePath;
  }

  public void setCSVFilePath(String csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  @Override
  public String toString() {
    return "CSVDBHandler" + "\nBiggest ID: " + biggestID + "\nCSV File Path : " + csvFilePath
        + "\nDB File Path: " + dbFilePath;
  }
}
