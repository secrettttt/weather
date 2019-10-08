package com.example.weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public  class WeatherInfo {
        private String date,week,theLunarCalendar;
        private String realTimeTemperature,realTimeWeatherStatus;
        private String updateTime;
        private String cityName;
        private String lowTemperature,highTemperature;
        private String weatherState;
        private String wind;

        public void setDate(String date){this.date = date;}

        public String getDate() {
            String month;
            String day;
            String temp = "";
            String result;

            //从字符串中获取数字
            if (this.date != null) {
                temp = this.date.replaceAll("[^0-9]","");
            }
            if(temp.charAt(4)!='0') {
                month = temp.substring(4,6);
            }
            else {
                month = temp.substring(5,6);
            }
            if(temp.charAt(6)!='0') {
                day = temp.substring(6,8);
            }
            else{
                day = temp.substring(7,8);
            }
            result = month + "月" + day + "日";
            return result;
        }

        public String getWeek(){
            Calendar cal = Calendar.getInstance();
            int i = cal.get(Calendar.DAY_OF_WEEK);
            switch (i) {
                case 1:
                    return "周日";
                case 2:
                    return "周一";
                case 3:
                    return "周二";
                case 4:
                    return "周三";
                case 5:
                    return "周四";
                case 6:
                    return "周五";
                case 7:
                    return "周六";
                default:
                    return "";
            }
        }

        public void  setRealTimeTemperature(String realTimeTemperature) { this.realTimeTemperature=realTimeTemperature; }

        public String getRealTimeTemperature()
        {
            return this.realTimeTemperature;
        }

        public void setRealTimeWeatherStatus(String realTimeWeatherStatus) { this.realTimeWeatherStatus = realTimeWeatherStatus; }

        public String getRealTimeWeatherStatus() {
            return this.realTimeWeatherStatus;
        }

        /*
        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateTime()
        {
            String hour;
            String minute;
            String temp="";
            String result;

            //从字符串中获取数字
            if (this.updateTime != null) {
                temp = this.updateTime.replaceAll("[^0-9]","");
            }
            if(temp.charAt(8)!='0') {
                hour = temp.substring(8,10);
            }
            else {
                hour = temp.substring(9,10);
            }
            /* 错误：16:00 显示成 16:0 16:03 显示成16:3
            if(temp.charAt(10)!='0') {
                minute = temp.substring(10,12);
            }
            else{
                minute = temp.substring(11,12);
            }
             */
        /*
            minute = temp.substring(10,12);
            result = hour + ":" + minute;
            return result;
        }
        */

        public String getUpdateTime() {
            //设置默认时区：东八区
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
            //格式化时间(HH是24小时制，hh是12小时制)
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //获取当前时间
            Date date = new Date();
            // 返回已经格式化的时间
            return sdf.format(date);
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCityName()
        {
            return this.cityName;
        }

        public void setLowTemperature(String lowTemperature) { this.lowTemperature = lowTemperature; }

        public String getLowTemperature() { return this.lowTemperature; }

        public void setHighTemperature(String highTemperature) { this.highTemperature = highTemperature; }

        public String getHighTemperature() {
            return this.highTemperature;
        }

        public void setWeatherState(String weatherState) {
            this.weatherState = weatherState;
        }

        public String getWeatherState() {
            return this.weatherState;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getWind() {
            return this.wind;
        }

        public boolean isNight() {
            //设置默认时区：东八区
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
            //格式化时间(HH是24小时制，hh是12小时制)
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            //获取当前时间
            Date date = new Date();
            // 返回已经格式化的时间
            if(Integer.parseInt(sdf.format(date))>=19||Integer.parseInt(sdf.format(date))<=6)
                return true;
            else
                return false;
        }
}
