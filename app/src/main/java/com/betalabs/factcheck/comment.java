package com.betalabs.factcheck;

public class comment {
    String Name, comment, comment_id, profile_image;



    public  comment()
    {

    }


    public comment(String name, String comment, String comment_id, String profile_image) {
        this.Name= name;
        this.comment = comment;
        this.comment_id = comment_id;
        this.profile_image = profile_image;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
