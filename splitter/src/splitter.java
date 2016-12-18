import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by nik on 12/18/16.
 */
public class splitter {
    public  static void main(String [] args) {
        if (args.length < 2) {
            System.out.println("Usage : $ java " + splitter.class.getSimpleName() + " <file|files> path/to/file");

            return;
        }
        String mode = args[0];
        String argfilepath = args[1];
        ArrayList<String> filesToParse = new ArrayList<>();

        if (mode.equals("files")) {
            try {
                BufferedReader bf = new BufferedReader(new FileReader(argfilepath));
                String line;
                while ((line = bf.readLine()) != null) {
                    filesToParse.add(line.trim());
                }
                bf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            filesToParse.add(argfilepath);


        ArrayList<Article> parsedArticles = new ArrayList<>();
        for (String filepath : filesToParse) {
            System.out.println("Parsing " + filepath);


            try {
                String fileContents = new String(Files.readAllBytes(Paths.get(filepath)));
                String[] articles = fileContents.split("</REUTERS>");
                System.out.println(articles.length + " articles.");
                for (String article : articles) {
                    article = article.trim();
                    if (article.isEmpty()) continue;

                    // url from newID
                    int id_idx = article.indexOf("NEWID");
                    id_idx += 7;
                    String idnum = "";
                    while (article.charAt(id_idx) != '"') {
                        idnum += article.charAt(id_idx);
                        id_idx++;
                    }

                    String url = "article_url_" + idnum;

                    String title = getTagContents("<TITLE>", "</TITLE>", article);
                    title = preprocessText(title);
                    String text = getTagContents("<BODY>", "</BODY>", article);
                    if(text.isEmpty())
                    {
                        System.err.println("\t\tSkipping article with title:[" + url + "] due to empty text.");
                        continue;
                    }
                    text = preprocessText(text);
                    if(title.isEmpty())
                    {
                        title = text.substring(text.indexOf('.')) ;
                    }


                    String date = getTagContents("<DATE>", "</DATE>", article);
                    SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS");
                    Date parsed_date = Calendar.getInstance().getTime();
                    Date crawled_date = Calendar.getInstance().getTime();
                    String crawled = Long.toString(crawled_date.getTime());
                    try {
                        parsed_date = sf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sf = new SimpleDateFormat("yyyy-MM-dd");
                    String ymdl = sf.format(parsed_date);
                    date = Long.toString(parsed_date.getTime());
                    parsedArticles.add(new Article(url, text, title, date, crawled, ymdl));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Read " + parsedArticles.size() + " articles from file.");
            try {

                String outpath = filepath + ".queries.cql";

                BufferedWriter bw = new BufferedWriter(new FileWriter(outpath));
                for (Article art : parsedArticles) {
                    bw.write(art.toCQL() + "\n");
                }

                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getTagContents(String startTag, String endTag, String rootContent)
    {
        int b1 = rootContent.indexOf(startTag);
        int b2 = rootContent.indexOf(endTag);
        String ret = "";
        try {
            ret = rootContent.substring(b1 + startTag.length(), b2);
        }
        catch(StringIndexOutOfBoundsException ex)
        {
            ;
        }
        return ret;
    }
    private static String preprocessText(String text)
    {

        return text.replaceAll("'","''")
                .replaceAll("\n", " ").replaceAll("  ", " ").replaceAll(" Reuter&#3;", "");
    }


}
