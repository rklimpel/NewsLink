package de.ricoklimpel.newslink;

/**
 * Created by ricoklimpel on 03.12.16.
 *
 * NewsSource Object, created for saving one news sources data into this class
 *
 */
public class NewsSource {

    String sourceID;
    String sourceName;
    String description;
    String url;
    String category;
    String language;
    String country;
    String[] urlLogo;
    Boolean[] sortAvailable;

    public NewsSource(){

    }

    public String getSourceID() {
        return sourceID;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String[] getUrlLogo() {
        return urlLogo;
    }

    public Boolean[] getSortAvailable() {
        return sortAvailable;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUrlLogo(String[] urlLogo) {
        this.urlLogo = urlLogo;
    }

    public void setSortAvailable(Boolean[] sortAvailable) {
        this.sortAvailable = sortAvailable;
    }
}
