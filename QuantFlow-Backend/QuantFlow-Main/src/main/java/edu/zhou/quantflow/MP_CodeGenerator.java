package edu.zhou.quantflow;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

/**
 * @Author Righter
 * @Description
 * @Date since 3/5/2025
 */
public class MP_CodeGenerator {
    public static void main(String[] args) {

        String url = "jdbc:postgresql://127.0.0.1:5432/quantflow";
        String username = "postgres";
        String password = "postgres";
        //System.out.println(System.getProperty("user.dir")); //
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("Zhouyue")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/QuantFlow-Main/quantflow-generator/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("edu.zhou.quantflow")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .addInclude("simulation_log")//指定表
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}

