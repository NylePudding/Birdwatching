package uk.co.walesbirds.birdwatching;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by NylePudding on 15-Jun-17.
 */

public class BirdEntry implements Comparable<BirdEntry> {

    public String getType() {
        return type;
    }

    public String getWelshType() {
        return welshType;
    }

    public String getLatin() {
        return latin;
    }

    public String getEnglish() {
        return english;
    }

    public String getWelsh() {
        return welsh;
    }

    public String getPlural() {
        return plural;
    }

    public String getGender() {
        return gender;
    }

    public String getUnknown() {
        return unknown;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getWelsheDetails() {
        return welsheDetails;
    }

    public String getEnglishDetails() {
        return englishDetails;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public String getPictureCredit() {
        return pictureCredit;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public String getVisitor() {
        return visitor;
    }

    public String getRare() {
        return rare;
    }

    public String getLocation() {
        return location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWelshType(String welshType) {
        this.welshType = welshType;
    }

    public void setLatin(String latin) {
        this.latin = latin;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public void setWelsh(String welsh) {
        this.welsh = welsh;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUnknown(String unknown) {
        this.unknown = unknown;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setWelsheDetails(String welsheDetails) {
        this.welsheDetails = welsheDetails;
    }

    public void setEnglishDetails(String englishDetails) {
        this.englishDetails = englishDetails;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public void setPictureCredit(String pictureCredit) {
        this.pictureCredit = pictureCredit;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor;
    }

    public void setRare(String rare) {
        this.rare = rare;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String type;
    private String welshType;
    private String latin;
    private String english;
    private String welsh;
    private String plural;
    private String gender;
    private String unknown;
    private String category;
    private String subcategory;
    private String welsheDetails;
    private String englishDetails;
    private String wikiLink;
    private String pictureCredit;
    private String pictureLink;
    private String visitor;
    private String rare;
    private String location;

    @Override
    public int compareTo(@NonNull BirdEntry o) {
        return this.getCategory().compareTo(o.getCategory());
    }
}
