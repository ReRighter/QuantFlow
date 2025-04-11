package edu.zhou.quantflow;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.PositionsRatioCriterion;
import org.ta4j.core.criteria.ReturnOverMaxDrawdownCriterion;
import org.ta4j.core.criteria.VersusEnterAndHoldCriterion;
import org.ta4j.core.criteria.pnl.ReturnCriterion;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.StopLossRule;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/30/2025
 */
public class TaTest {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TushareParams{
        String ts_code;
        String start_date;
        String end_date;
        String trade_Date;
        //开始行数
        String offset;
        //最大行数
        String limit;
        public TushareParams(){};
        public TushareParams(String ts_code,String start_date,String end_date){
            this.ts_code = ts_code;
            this.start_date = start_date;
            this.end_date = end_date;
        };

    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TushareRequest {
        String api_name;
        String token;
        TushareParams params;
        String fields;
        public TushareRequest(){};
    }

    @Data
    public static class TushareResponse{
        String code;
        TushareData data;
        String request_id;
        String msg;
    }
    @Data
    public static class TushareData{
       List<String> fields;
       List<List<String>> items;
       boolean has_more;
       int count;

    }
    @Data
    public static class TushareItem{
        String ts_code;	//股票代码
        String trade_date;//交易日期
        float open; //开盘价
        float high;//最高价
        float low;//最低价
        float close;//收盘价
        float pre_close;//昨收价
        float change;//涨跌额
        float pct_chg;//涨跌幅
        float vol;//成交量
        float amount;//成交额
    }

    @Test
    public void test(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.tushare.pro";
        String token = "32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4";
        String body = JSON.toJSONString(new TushareRequest("daily", token,
                        new TushareParams("000001.SZ",
                                "20240101",
                                "20241231"),"open,high,low,close,vol"));
        RequestEntity<String> requestEntity = RequestEntity.post(url)
                .header("Content-Type", "application/json")
                .body(body);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        //将返回的Json格式字符串转换为CSV文件
        TushareResponse tushareResponse = JSON.parseObject(response.getBody(), TushareResponse.class);
        try(FileWriter fw = new FileWriter("D:\\school\\GraduationProj\\data\\test\\000001.SZ_20240101_20241231.csv")){
            //写入表头
            assert tushareResponse != null;
            for (String field : tushareResponse.getData().getFields()) {
                fw.write(field + ",");
            }
            fw.write("\n");
            //写入数据
            for (List<String> item : tushareResponse.getData().getItems()) {
                for (String value : item) {
                    fw.write(value + ",");
                }
                fw.write("\n");
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        //BarSeries series = null;
    }
    @Test
    public void JsonTest(){
        TushareParams params = new TushareParams();
        params.ts_code = "000001.SZ";
        params.start_date = "20200101";
        params.end_date = "20201231";
        TushareRequest request = new TushareRequest();
        request.api_name = "daily";
        request.params = params;
        request.token = "32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4";

        String json = JSON.toJSONString(request);
        System.out.println(json);

    }

    @Test
    public void Ta4jTest(){
        BarSeries series =new BaseBarSeriesBuilder().withName("test").build();
        ZonedDateTime now = ZonedDateTime.now();
        String path = "D:\\school\\GraduationProj\\data\\test\\000001.SZ_20240101_20241231.csv";
        try(BufferedReader reader =new BufferedReader( new FileReader(path))) {
            String line;
            reader.readLine();
            int cnt = 0;
            while((line = reader.readLine())!=null) {
                // 读取每一行数据
                String[] record = line.split(",");
                float open = Float.parseFloat(record[0]);
                float high = Float.parseFloat(record[1]);
                float low = Float.parseFloat(record[2]);
                float close = Float.parseFloat(record[3]);
                float vol = Float.parseFloat(record[4]);
                // 将数据添加到BarSeries中
                series.addBar(now.plusDays(cnt++), open, high, low, close, vol);
            }
            ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
            SMAIndicator shortSMA = new SMAIndicator(closePrice, 5);
            System.out.println("5日均线: " + shortSMA.getValue(series.getEndIndex()));
            SMAIndicator longSMA = new SMAIndicator(closePrice, 30);
            Rule buyingRule = new org.ta4j.core.rules.CrossedUpIndicatorRule(shortSMA, longSMA);
            Rule sellingRule = new org.ta4j.core.rules.CrossedDownIndicatorRule(shortSMA, longSMA)
                    .or(new StopLossRule(closePrice, 0.05))
                    .or(new StopGainRule(closePrice, 0.05));
            Strategy strategy = new BaseStrategy(buyingRule, sellingRule);
            //运行策略
            BarSeriesManager seriesManager = new BarSeriesManager(series);
            TradingRecord tradingRecord = seriesManager.run(strategy);
            int positionCount = tradingRecord.getPositionCount();
            System.out.println("持仓数量: " + positionCount);

            AnalysisCriterion winningPositionsRatio = new PositionsRatioCriterion(AnalysisCriterion.PositionFilter.PROFIT);
            System.out.println("盈利仓位比例: " + winningPositionsRatio.calculate(series, tradingRecord));

            AnalysisCriterion romad = new ReturnOverMaxDrawdownCriterion();
            System.out.println("收益/最大回撤比: " + romad.calculate(series, tradingRecord));

            AnalysisCriterion vsBuyAndHold = new VersusEnterAndHoldCriterion(new ReturnCriterion());
            System.out.println("策略收益对比BuyAndHold: " + vsBuyAndHold.calculate(series, tradingRecord));


        }
         catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
