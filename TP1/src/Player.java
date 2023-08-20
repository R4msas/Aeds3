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

}
