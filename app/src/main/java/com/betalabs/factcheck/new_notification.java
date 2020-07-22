package com.betalabs.factcheck;

public class new_notification {
    String Name, Profile_Image, Time_Stamp, Title, Image;

    public new_notification() {

    }


    public new_notification(String name, String profile_Image, String time_Stamp, String title, String image) {
        Name = name;
        Profile_Image = profile_Image;
        Time_Stamp = time_Stamp;
        Title = title;
        Image = image;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        Profile_Image = profile_Image;
    }

    public String getTime_Stamp() {
        return Time_Stamp;
    }

    public void setTime_Stamp(String time_Stamp) {
        Time_Stamp = time_Stamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

