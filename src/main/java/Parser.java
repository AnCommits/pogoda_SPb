// Приложение выводит в консоль прогноз погоды в СПб
// Используется информация с сайта https://pogoda.spb.ru
// Использованы материалы вебинара "Создаём приложение Погода на Java"

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Document getPage(String url) throws IOException {
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    private static final Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't extract date from string");
    }

    private static String[] getDateFromIndexValues(Elements values, int index) {
        Element valueLine = values.get(index);
        String[] result = new String[6];
        int i = 0;
        for (Element td : valueLine.select("td")) {
            result[i++] = td.text();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String url = "https://pogoda.spb.ru";
        Document page = getPage(url);
        //css query language
        Element tableWth = page.select("table[class=wt]").first();
        Elements names = tableWth.select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");

        int[] columnWidth = {7, 52, 13, 10, 11, 15};
        String[] columnNames = {"", "Явления", "Температура", "Давление", "Влажность", "Ветер"};
        StringBuilder topSB = new StringBuilder();
        for (int i = 1; i < columnWidth.length; i++) {
            topSB.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                int addsp = columnWidth[i] - columnNames[i].length();
                if (addsp < 0) addsp = 0;
                topSB.append(" ".repeat(addsp));
            }
        }
        System.out.println("Информация о погоде получена с сайта " + url + "\n");

        String top = topSB.toString();
        int index = 0;
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            int w0 = columnWidth[0] - date.length();
            if (w0 < 0) w0 = 0;
            date = date + " ".repeat(w0);
            System.out.println(date + top);
            String[] timeDate;
            do {
                timeDate = getDateFromIndexValues(values, index++);
                for (int i = 0; i < timeDate.length - 1; i++) {
                    int addsp = columnWidth[i] - timeDate[i].length();
                    if (addsp < 0) addsp = 0;
                    String s = timeDate[i] + " ".repeat(addsp);
                    if (s.length() >= columnWidth[i])
                        s = s.substring(0, columnWidth[i] - 1) + " ";
                    System.out.print(s);
                }
                String s = timeDate[timeDate.length - 1];
                if (s.length() > columnWidth[columnWidth.length - 1])
                    s = s.substring(0, columnWidth[columnWidth.length - 1]);
                System.out.println(s);
            } while (!timeDate[0].equals("Ночь"));
            System.out.println();
        }
    }
}
