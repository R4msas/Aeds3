import pandas as pd
import random as rd
import country_converter as cc

db = pd.read_csv("csgo_players.csv")
db["country"] = cc.convert(names=db["country"], to="ISO3")


def calculate_birth_date(age):
    current_year = pd.to_datetime("today").year
    birth_year = current_year - age
    return pd.Timestamp(year=birth_year, month=rd.randint(1, 12), day=rd.randint(1, 28))


db["birth_date"] = db["age"].apply(calculate_birth_date)
db.drop(columns=["age"], inplace=True)
db.to_csv("csgo_players_treated.csv")