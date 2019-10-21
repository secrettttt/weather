package com.example.weather;

public class City {
    private String CityChineseName;
    private String CityPinyinName;

    public City()
    {

    }

    public City(String CityChineseName, String CityPinyinName)
    {
        this.CityChineseName = CityChineseName;
        this.CityPinyinName = CityPinyinName;
    }

    public String getCityChineseName()
    {
        return this.CityChineseName;
    }

    public String getCityPinyinName()
    {
        return this.CityPinyinName;
    }

    public void setCityChineseName(String CityChineseName)
    {
        this.CityChineseName = CityChineseName;
    }

    public void setCityPinyinName(String CityPinyinName)
    {
        this.CityPinyinName = CityPinyinName;
    }
}
