package ru.geekbrains.android1.stas.individual_minute;

import java.io.Serializable;

/**
 * Created by Stars on 03.10.2016.
 */
class MyData implements Serializable {
    private long id;
    private String date;
    private String time;


    MyData(long id, String title, String note) {
        this.id = id;
        this.date = title;
        this.time = note;
    }

    long getID() {
        return id;
    }

    String getTime() {
        return time;
    }

    String getDate() {
        return date;
    }


}
