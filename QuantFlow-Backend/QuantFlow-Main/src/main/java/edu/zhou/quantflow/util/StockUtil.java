package edu.zhou.quantflow.util;


import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 4/22/2025
 */
public class StockUtil {
    private static String token = "32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4";
    private static String url = "https://api.tushare.pro";
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
    public static BigDecimal getStockPrice(String stock_code){
        return null;
    }
}
