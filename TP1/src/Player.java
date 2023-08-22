import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Player {
  private String name;
  private String teams;
  private int playerId;
  private String birthDate;
  private String country;
  private float rating;

  public Player() {
  }

  public Player(String name, String teams, int playerId, String birthDate, String country, float rating) {
    this.name = name;
    this.teams = teams;
    this.playerId = playerId;
    this.birthDate = birthDate;
    this.country = country;
    this.rating = rating;
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

  String getBirthDate() {
    return this.birthDate;
  }

  void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
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

  @Override
  public String toString() {
    return "Player" +
        "\nNickname: " + this.name +
        "\nTeams: " + this.teams +
        "\nID: " + this.playerId +
        "\nBirthdate: " + this.birthDate +
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
    dos.writeUTF(this.getBirthDate());
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
    this.setBirthDate(dis.readUTF());
    this.setCountry(dis.readUTF());
    this.setRating(dis.readFloat());

    dis.close();
  }

}
