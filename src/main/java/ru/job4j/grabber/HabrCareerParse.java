package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final int NUM_PAGES = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) {
        String text;
        try {
            text = Jsoup.connect(link).get()
                    .select(".faded-content")
                    .text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i < NUM_PAGES; i++) {
            try {
                Connection connection = Jsoup.connect(String.format("%s?page=%s", link, i));
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                for (Element row : rows) {
                    postList.add(postParse(row));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return postList;
    }

    private Post postParse(Element row) throws IOException {
        Post post = new Post();
        post.setTitle(row.select(".vacancy-card__title").first().text());
        post.setDescription(retrieveDescription(String.format("%s%s", SOURCE_LINK, row.select(".vacancy-card__title")
                .first().child(0)
                .attr("href"))));
        post.setLink(String.format("%s%s", SOURCE_LINK, row.select(".vacancy-card__title")
                .first()
                .child(0)
                .attr("href")));
        post.setCreated(dateTimeParser.parse(row.select(".vacancy-card__date")
                .first()
                .child(0)
                .attr("datetime")));
        return post;
    }
}
