package core;

import display.Window;
import models.Match;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Spyder extends Canvas implements Runnable {

    private static final int SCALE = 3;
    public static final int WIDTH = 320 * SCALE;
    public static final int HEIGHT = WIDTH * 9 / 12;
    private static final String TITLE = "Spyder 0.1";
    private static Random RANDOM = new Random();
    private boolean running = false;
    private Thread thread;
    private Window window;
    private List<String> results;
    private Set<String> urls;

    public Spyder() {
        window = new display.Window(WIDTH, HEIGHT, TITLE, this);
        results = new ArrayList<>();
        urls = new HashSet<>();
        String hardcode = "Hello World";
        getURLs(hardcode, "https://www.google.com/search?q=");

        List<String> urlList = new ArrayList();

        for (String url : urls) {
            urlList.add(url);
        }

        for (String url : urlList) {
            getURLs("", url);
        }

        for (String url : urls) {
            getResults(hardcode, url);
        }

        for (String result : results) {
            System.out.println(result);
        }
        System.out.println("Done");
    }

    private void getResults(String searchPhrase, String url) {
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line + "\n");
            }

            try {
                Pattern regex = Pattern.compile(String.format("\"(?!\").*?%s.*?(?<!\")\"", searchPhrase),Pattern.CASE_INSENSITIVE); // DOESNT WORK PROPERLY DEBUG SEE WHATS UP
                Matcher regexMatcher = regex.matcher(sb);
                while (regexMatcher.find()) {
//                    results.add(regexMatcher.group(0));
                    System.out.println(regexMatcher.group(0));
                }
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void getURLs(String searchPhrase, String googleSearchUrl) {
        URLConnection connection = null;
        try {
            String fixedSearchPhrase = searchPhrase.replaceAll(" ", "+");
            String completeUrl = googleSearchUrl + fixedSearchPhrase;
            connection = new URL(completeUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line + "\n");
            }

            try {
                Pattern regex = Pattern.compile("href=\"(https:\\/\\/www\\.(?!google).*?)\"");
                Matcher regexMatcher = regex.matcher(sb);
                while (regexMatcher.find()) {
                    urls.add(regexMatcher.group(1));
//                    System.out.println(regexMatcher.group(1)); // sout as it searches
                }
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        while (running) {
            long lastTime = System.nanoTime();
            double amountOfTicks = 20.0;
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            long timer = System.currentTimeMillis();
            int frames = 0;
            while (running) {

                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (delta >= 1) {
                    tick();
                    delta--;
                }
                if (running) {
                    render();
                }
                frames++;
                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    frames = 0;
                }
            }
            stop();
        }
    }

    private void tick() {

    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Spyder.WIDTH, Spyder.HEIGHT);

//        g.setColor(Color.BLACK);
//        for (int i = 0; i < results.size(); i++) {
//            g.drawString(results.get(i).getUrl(),10,i*20);
//            g.drawString(results.get(i).getContent(),10,i*20 + 10);
//        }

        g.dispose();
        bs.show();
    }

    private synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public synchronized void crawl() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }
}
