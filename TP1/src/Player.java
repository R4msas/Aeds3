import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Player {
  private String name;
  private String teams;
  private int playerId;
  private long birthDate;
  private String country;
  private float rating;

  public Player() {
    this.name = this.teams = this.country = "";
    this.playerId = 0;
    this.birthDate = 0L;
    this.rating = 0F;
  }

  public Player(String name, String teams, int playerId, String birthDate, String country, float rating) {
    this.setName(name);
    this.setTeams(teams);
    this.setPlayerId(playerId);
    this.setBirthDate(birthDate);
    this.setCountry(country);
    this.setRating(rating);
  }

  public Player(String name, String teams, int playerId, long birthDate, String country, float rating) {
    this.setName(name);
    this.setTeams(teams);
    this.setPlayerId(playerId);
    this.setBirthDate(birthDate);
    this.setCountry(country);
    this.setRating(rating);
  }

  @Override
  public String toString() {
    String birthDateString = "";
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date(this.birthDate);
      birthDateString = dateFormat.format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "Player" +
        "\nNickname: " + this.name +
        "\nTeams: " + this.teams +
        "\nID: " + this.playerId +
        "\nBirthdate: " + birthDateString +
        "\nCountry: " + this.country +
        "\nRating: " + this.rating;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    // dos.writeUTF(header);

    dos.writeUTF(this.getName());
    dos.writeUTF(this.getTeams());
    dos.writeInt(this.getPlayerId());
    dos.writeLong(this.getBirthDate());
    dos.writeUTF(this.getCountry());
    dos.writeFloat(this.getRating());

    dos.close();
    return baos.toByteArray();
  }

  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    this.setName(dis.readUTF());
    this.setTeams(dis.readUTF());
    this.setPlayerId(dis.readInt());
    this.setBirthDate(dis.readLong());
    this.setCountry(dis.readUTF());
    this.setRating(dis.readFloat());

    dis.close();
  }

  String getName() {
    return this.name;
  }

  void setName(String name) {
    this.name = name;
  }

  String getTeams() {
    return this.teams;
  }

  void setTeams(String teams) {
    this.teams = teams;
  }

  int getPlayerId() {
    return this.playerId;
  }

  void setPlayerId(int id) {
    this.playerId = id;
  }

  long getBirthDate() {
    return this.birthDate;
  }

  void setBirthDate(long birthDate) {
    this.birthDate = birthDate;
  }

  void setBirthDate(String birthDate) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = dateFormat.parse(birthDate);
      this.birthDate = date.getTime();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  String getCountry() {
    return this.country;
  }

  void setCountry(String country) {
    this.country = country;
  }

  float getRating() {
    return this.rating;
  }

  void setRating(float rating) {
    this.rating = rating;
  }

}
