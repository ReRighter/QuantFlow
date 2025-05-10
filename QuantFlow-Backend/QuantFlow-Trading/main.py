from fastapi import FastAPI
from fastapi.responses import JSONResponse
from controller import simlation_router,strategy_router 
from utils import Result
import uvicorn
import tushare as ts

app = FastAPI()

#全局异常处理
@app.exception_handler(Exception)
async def exception_handler(request, exc):
    result= Result.error(message=str(exc), code=500)
    return JSONResponse(
        status_code=200,
        content=result.to_dict()
    )

app.include_router(simlation_router, tags=["simulation"])
app.include_router(strategy_router,tags=["strategy"])

if __name__ == "__main__":
    ts.set_token("32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4")
    uvicorn.run(app,host="0.0.0.0", port=8082)