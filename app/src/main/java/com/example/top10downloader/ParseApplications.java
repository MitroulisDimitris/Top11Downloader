package com.example.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    //class to parse thought the rss feed of top 10 applications
    private static final String TAG = "Parse Applications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        //initialize ArrayList
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean Status = true;
        FeedEntry currentRecord = null;   //for every new entry we create a new feedEntry obj
        boolean inEntry = false;
        String textValue = "";
        boolean gotImage = false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Log.d(TAG, "parse: Starting tag for" + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }else if(("image".equalsIgnoreCase(tagName))&& inEntry) {
                            String imageResolution = xpp.getAttributeValue(null,"height");
                            if(imageResolution != null){
                                gotImage = "53".equalsIgnoreCase(imageResolution);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        //Log.d(TAG, "parse: Ending Tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;

                            }else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            }else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            }else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            }else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            }else if ("image".equalsIgnoreCase(tagName)) {
                                if(gotImage) {
                                    currentRecord.setImageURL(textValue);
                                }
                            }
                        }
                        break;

                    default:
                        //Nothing to do here
                }
                eventType = xpp.next();

            }
//            for(FeedEntry app:applications){
//                Log.d(TAG, "*********************");
//                Log.d(TAG, app.toString() );
//            }

        } catch (XmlPullParserException e) {
            Status = false;
            e.printStackTrace();
        } catch (Exception e) {
            Status = false;
            e.printStackTrace();
        }


        return Status;

    }
}
