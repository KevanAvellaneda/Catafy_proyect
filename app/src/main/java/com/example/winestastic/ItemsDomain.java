package com.example.winestastic;

import java.io.Serializable;

public class ItemsDomain implements Serializable {

    private String title;
    private String address;
    private String pic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public ItemsDomain(String title, String address, String pic) {
        this.title = title;
        this.address = address;
        this.pic = pic;
    }


}
