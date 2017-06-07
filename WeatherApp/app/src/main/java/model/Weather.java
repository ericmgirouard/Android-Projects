package model;

/**
 * Created by egirouard on 5/8/2017.
 */

public class Weather {
    public Place place;
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();



}
