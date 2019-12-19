package com.example.ashoktechforchange.Models;

import java.util.List;

public class ComplaintNew {

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOfficerComment() {
        return officerComment;
    }

    public void setOfficerComment(String officerComment) {
        this.officerComment = officerComment;
    }

    public String getCompID() {
        return compID;
    }

    public void setCompID(String compID) {
        this.compID = compID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFirstComplaint() {
        return isFirstComplaint;
    }

    public void setFirstComplaint(String firstComplaint) {
        isFirstComplaint = firstComplaint;
    }

    public String getIsFirstComplaint() {
        return isFirstComplaint;
    }

    public void setIsFirstComplaint(String isFirstComplaint) {
        this.isFirstComplaint = isFirstComplaint;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String officerName;
    public String officerComment;
    public String name;
    public String uid;
    public String compID;
    public String description;
    public String location;
    public int status;
    public String date;
    public String videoUrl;
    public String latLong;
    public String officer;
    public int likes;
    public String department;
    public String time;
    public String tag;
    public String isFirstComplaint;
    public String liked;


    public ComplaintNew(String officerName, String officerComment, String name, String uid, String department, String description,
                     String location, int status, String date, String videoUrl, String latLong, String officer, int likes,
                     String compID, String time, String tag, String isFirstComplaint, String liked) {
        this.officerName = officerName;
        this.officerComment = officerComment;
        this.name = name;
        this.uid = uid;
        this.compID = compID;
        this.description = description;
        this.location = location;
        this.status = status;
        this.date = date;
        this.videoUrl = videoUrl;
        this.latLong = latLong;
        this.officer = officer;
        this.likes = likes;
        this.department = department;
        this.time = time;
        this.tag = tag;
        this.isFirstComplaint = isFirstComplaint;
        this.liked = liked;
    }

    public ComplaintNew(){

    }

}

