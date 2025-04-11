from fastapi import APIRouter
from pydantic import BaseModel
from service import StrategyService
from utils import Result 

router = APIRouter()

class BackTestParams(BaseModel):
    stock_code: str
    start_time: str
    end_time: str
    strategy_code: str
    cash: float = 1000000.0
    sizer: int = 10


@router.get("/health")
async def health_check():
    return {"status": "healthy"}

@router.post("/strategy/backtest")
async def backtest_strategy(params: BackTestParams):
    #print(f"Received request to backtest strategy: {params.strategy_code} for stock: {params.stock_code} from {params.start_time} to {params.end_time}")
    result = await StrategyService.exec_strategy_code(params.start_time,params.end_time,params.stock_code, params.strategy_code, params.cash, params.sizer)
    return Result.success(data=result)