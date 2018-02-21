package models;

public class Match {
    private String url;
    private String content;

    public Match(String url, String content) {
        this.setUrl(url);
        this.setContent(content);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
