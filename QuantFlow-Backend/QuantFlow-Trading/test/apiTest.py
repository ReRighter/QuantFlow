import requests


session = requests.Session()

response = session.get(
                f"http://localhost:8081/trading/sync_status/4"
            )
result = response.json()
code:int = result["code"]
data = result["data"]
message = result["message"]
print(code,message)


if code == 200:
    balance  = data["balances"]
    cash = balance["available"]
    for position in data['positions']:
        print("symbol:",position["stockCode"],"quantity:",position["quantity"])
        #self.positions[position['symbol']] = position['size']