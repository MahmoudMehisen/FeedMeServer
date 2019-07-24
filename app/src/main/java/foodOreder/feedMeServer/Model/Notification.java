package foodOreder.feedMeServer.Model;

public class Notification {

    public String body;
    public String tilte;

    public Notification(String body, String title)
    {
        this.body = body;
        this.tilte = title;
    }

    public Notification() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTilte() {
        return tilte;
    }

    public void setTilte(String tilte) {
        this.tilte = tilte;
    }
}
