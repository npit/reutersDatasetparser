/**
 * Created by nik on 12/18/16.
 */
public class Article {
    String url;
    String text;
    String title;
    String publ;
    String crawled;
    String ymdl;

    public Article(String url, String text, String title, String publ, String crawled, String ymdl) {
        this.url = url;
        this.text = text;
        this.title = title;
        this.publ = publ;
        this.crawled = crawled;
        this.ymdl = ymdl;
        System.out.println("Adding article " + url + " | " + title);
    }
    public String toCQL()
    {
        String res = "insert into news_articles_per_published_date ";
        res += "(year_month_day_literal, published, entry_url, title, clean_text , crawled ) values (";
        res += "'" + ymdl + "'," + publ + ",'" + url + "','" + title + "','" + text + "'," + crawled;

        res +=");";
        return res;
    }

}
