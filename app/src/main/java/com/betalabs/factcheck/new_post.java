package com.betalabs.factcheck;

public class new_post {
    String Description, Image, Profile_Image, Role, Time_Stamp, Title, Verified, Name, voteplus, voteminus;


    public new_post() {

    }

    public new_post(String description, String image, String profile_Image, String role, String time_Stamp, String title, String verified, String name, String voteplus, String voteminus) {
        this.Description = description;
        this.Image = image;
        this.Profile_Image = profile_Image;
        this.Role = role;
        this.Time_Stamp = time_Stamp;
        this.Title = title;
        this.Verified = verified;
        this.Name = name;
        this.voteplus = voteplus;
        this.voteminus = voteminus;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        Profile_Image = profile_Image;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
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

    public String getVerified() {
        return Verified;
    }

    public void setVerified(String verified) {
        Verified = verified;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVoteplus() {
        return voteplus;
    }

    public void setVoteplus(String voteplus) {
        this.voteplus = voteplus;
    }

    public String getVoteminus() {
        return voteminus;
    }

    public void setVoteminus(String voteminus) {
        this.voteminus = voteminus;
    }
}
