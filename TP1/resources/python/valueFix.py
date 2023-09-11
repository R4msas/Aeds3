import pandas as pd
import random as rd
import country_converter as cc

db = pd.read_csv("../data/csgo_players.csv", index_col=None)
# db.reset_index(drop=True, inplace=True)
db["country"] = cc.convert(names=db["country"], to="ISO3")


def calculate_birth_date(age):
    current_year = pd.to_datetime("today").year
    birth_year = current_year - age
    return pd.Timestamp(year=birth_year, month=rd.randint(1, 12), day=rd.randint(1, 28))


db["birth_date"] = db["age"].apply(calculate_birth_date)
db.drop(columns=["age"], inplace=True)

newDatabase = db[["nickname", "teams", "player_id", "birth_date", "country", "rating"]]
newDatabase.to_csv("../data/csgo_players_treated.csv", index=False)

sorted = newDatabase.sort_values(by=["player_id"])
sorted.to_csv("../data/csgo_players_sorted.csv", index=False)
