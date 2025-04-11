from fastapi import FastAPI
from fastapi.responses import JSONResponse
from controller import StrategyController as Controller
from utils import Result
import uvicorn

app = FastAPI()

#全局异常处理
@app.exception_handler(Exception)
async def exception_handler(request, exc):
    result= Result.error(message=str(exc), code=500)
    return JSONResponse(
        status_code=200,
        content=result.to_dict()
    )

app.include_router(Controller.router, tags=["strategy"])


if __name__ == "__main__":
    uvicorn.run(app,host="0.0.0.0", port=8081, log_level="warning")