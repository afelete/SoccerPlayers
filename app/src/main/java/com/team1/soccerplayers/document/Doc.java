package com.team1.soccerplayers.document;

/**
 * Created by Afelete Kita on 8/30/2016.
 * Define the structure of the document received from the internet via the bing search engine
 */
public class Doc {
    public final String title;
    public final String detail;
    public final String pictureUrl;
    public final String docId;

    public Doc(String title, String detail, String pictureUrl, String docId) {
        this.title = title;
        this.detail = detail;
        this.pictureUrl = pictureUrl;
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getDocId() {
        return docId;
    }

}
