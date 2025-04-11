import pandas as pd
from pandas import DataFrame

df:DataFrame = pd.read_csv("../datas/daily/000001.SZ_20240101_20241231.csv", index_col=0)
#delete first colum

print(df)