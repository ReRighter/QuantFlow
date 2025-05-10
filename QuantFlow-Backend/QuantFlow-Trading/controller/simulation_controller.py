from fastapi import APIRouter
from service import SimulationManager,start_live_trading,SimulationObj
from pydantic import BaseModel

class SimulationParams(BaseModel):
    stockCode: str
    strategyCode: str
    simulationId: int
    frequency: str


router = APIRouter()
#模拟交易任务管理, 用于控制交易的生命周期
sim_manager = SimulationManager()

@router.get("/simulation/health")
async def health_check():
    return {"code":200,"message": "healthy"}

@router.post("/start_simulation")
async def start_simulation(params: SimulationParams):
    simulationObj:SimulationObj=start_live_trading(stock_code=params.stockCode, simulationId=params.simulationId,strategy_code=params.strategyCode,frequency=params.frequency)
    sim_manager.add_simulation(params.simulationId,simulationObj)
    sim_manager.getSimulationObject(simulation_id=params.simulationId).thread.start()
    return {"code":200,"message":"ok"}


@router.post("/stop_simulation/{sim_id}")
async def stop_simulation(sim_id: int):
    simObj=sim_manager.getSimulationObject(sim_id)
    if simObj is None:
        return {"code":500,"message":"simulation not found"}
    simObj.stop_event.set()
    simObj.cerebro.runstop()
    simObj.thread.join()
    return {"code":200,"message":"ok"}

