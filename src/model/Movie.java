package model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String duration;
    private String[] showTimes;
    private double price;

    public Movie(int id, String title, String genre, String duration, String[] showTimes, double price) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.showTimes = showTimes;
        this.price = price;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public String[] getShowTimes() { return showTimes; }
    public double getPrice() { return price; }

    @Override
    public String toString() { return title; }
}
