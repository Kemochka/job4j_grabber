package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private Connection connection;
    private final Properties properties;

    public AlertRabbit(Properties properties) {
        this.properties = properties;
        init();
    }

    private static Properties getProperty() {
        Properties properties = new Properties();
        try (InputStream inputStream = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void init() {
        Properties config = getProperty();
        try {
            String url = config.getProperty("url");
            String username = config.getProperty("username");
            String password = config.getProperty("password");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void  runScheduler() {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();//задачи, которые хотим выполнять периодически
            scheduler.start();//старт
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)//создается объект(создание задачи для выполнения)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()//создание расписания для выполнения задачи
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()//запуск через триггер, в этом случае запуск сразу
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);//вызов выполнения задачи
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Properties pr = getProperty();
        AlertRabbit alertRabbit = new AlertRabbit(pr);
        alertRabbit.runScheduler();
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try {
                String sql = "INSERT INTO rabbit (created_date) VALUES (TO_TIMESTAMP(?))";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setLong(1, System.currentTimeMillis());
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


