package org.cuit.app;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.io.File;
import java.util.*;

/**
 * mybatis plus 代码自动生成器
 *
 */
public class MyBatisPlusGenerator {
    private static String module;
    private static String PACKAGE = "org.cuit.app";
    private static String AUTHOR = "jirafa";
    private static String DATA_USER_NAME = "root";
    private static String DATA_PASSWORD = "123456";
    private static String DATA_DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static String DATA_URL = "jdbc:mysql://47.113.185.11:3319/elderly-care?useSSL=false";
    private static String PATH_MAPPER  = "src/main/java/org/cuit/app/mapper";
    private static String PATH_MAPPER_XML = "src/main/java/org/cuit/app/mapper/xml";
    private static String PATH_CONTROLLER = "/src/main/java/org/cuit/app/controller";
    private static String PATH_SERVICE = "/src/main/java/org/cuit/app/service";
    private static String PATH_SERVICE_IMP = "/src/main/java/org/cuit/app/service/sign/";
    private static String PATH_ENTITY = "/src/main/java/org/cuit/app/entity";

        public final static String PROJECT_PATH="D:\\IdeaProject\\elderly-care\\elderly-care-code";


    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();


        // 生成文件的输出目录
        gc.setOutputDir(PROJECT_PATH+ "/src/main/java");
        // 作者
        gc.setAuthor("Jirafa");
        // 生成后打开目录
        gc.setOpen(false);
        //是否覆盖
        gc.setFileOverride(false);
        // 设置时间类型使用哪个包下的
        gc.setDateType(DateType.ONLY_DATE);

        gc.setServiceName("%sService");
        // 主键策略
        gc.setIdType(IdType.INPUT);
        //实体属性 Swagger2 注解
        gc.setSwagger2(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://47.113.185.11:3319/elderly-care?useSSL=false");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("org.cuit.app")
                .setEntity("entity")
                .setMapper("mapper")
                .setXml("mapper.xml");
        mpg.setPackageInfo(pc);

        //配置自定义属性注入
        InjectionConfig injectionConfig = new InjectionConfig() {
            //.vm模板中，通过${cfg.abc}获取属性
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
//                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                map.put("abc", "自定义属性描述");
                this.setMap(map);
            }
        };


        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 关闭原有生成
        templateConfig.setService(null);
        templateConfig.setServiceImpl(null);

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();

        // 模板文件位置
        String serviceTemplatePath = "templates/myService.java.vm";

        focList.add(new FileOutConfig(serviceTemplatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！    下面是service实现类位置：com.fzg.service包下
                return PROJECT_PATH + "/src/main/java/org/cuit/app/service"
                        + "/" + tableInfo.getEntityName() + "Service" + StringPool.DOT_JAVA;
            }
        });

        injectionConfig.setFileOutConfigList(focList);
        mpg.setCfg(injectionConfig);
        mpg.setTemplate(templateConfig);



        //配置策略
        StrategyConfig strategy = new StrategyConfig();
        // 数据库中表的名字，表示要对哪些表进行自动生成controller service、mapper...
        strategy.setInclude("t_todo_list");
        // 数据库表映射到实体的命名策略,驼峰命名法
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 生成实体时去掉表前缀，比如edu_course，如果不加下面这句，生成的实体类名字就是：EduCourse
        strategy.setTablePrefix("t_");
        //生成实体时去掉表前缀
        // strategy.setTablePrefix(pc.getModuleName() + "_");

        //数据库表字段映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符

        mpg.setStrategy(strategy);

        mpg.execute();
    }
}

