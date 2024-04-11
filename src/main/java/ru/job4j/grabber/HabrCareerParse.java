package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int NUM_PAGES = 5;
    public static String description;

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= NUM_PAGES; i++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, i, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element vacancyDate = row.select(".vacancy-card__date").first();
                Element date = vacancyDate.child(0);
                String vacDate = date.attr("datetime");
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String descriptions;
                try {
                    descriptions = retrieveDescription(link);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("%s %s %s%n %s%n", vacancyName, vacDate, link, descriptions);
            });
        }
    }
    private static String retrieveDescription(String link) throws IOException {
     Connection connection = Jsoup.connect(link);
     Document document = connection.get();
     Elements rows = document.select(".faded-content__container");
     rows.forEach(row -> {
                 Element titleElement = row.select(".faded-content").first();
                 description = titleElement.text();
             });
     return description;
    }
}
