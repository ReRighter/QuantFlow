from controller import simulation_controller as con

class Params:
    stockCode: str
    strategyCode: str
    simulationId: int
    frequency: str
    def __init__(self):
        self.stockCode= "000001.SZ"
        with open("Strategy/DMA.py", "r",encoding='utf-8') as file:
            self.strategyCode =file.read()
        self.simulationId = 6
        self.frequency = "minute"


params = Params()

def main():
    con.start_simulation(params)

