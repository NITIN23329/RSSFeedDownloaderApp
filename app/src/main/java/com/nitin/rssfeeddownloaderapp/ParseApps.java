package com.nitin.rssfeeddownloaderapp;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApps {
    private static final String TAG = "ParseApps";
    private ArrayList<FeedEntry> appInfoList;
    public ParseApps(){
        appInfoList = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getAppInfoList() {
        return appInfoList;
    }

    public boolean parse(String xmlData){
        /* isAllGood will tell if everything is good or not,
         we will return true if we parse successfully else false */
        boolean isAllGood = true;
        /* insideEntry will tell whether we are inside a <entry> or not. we are checking for insideEntry as we have a tag "name" in FeedEntry.
        * There is a tag with same name outside of entry which tells name of the RSS Feed we are in and another inside each entry which tells the name of app
        * We need the name of app of each entry only*/
        boolean insideEntry = false;
        // this is the value of our current tag.
        String currTagValue = "";
        // this holds a current FeedEntry
        FeedEntry currEntry = null;
        try{
            // its a singleton class used for parsing a xml data
            // more information about xmlParser: https://www.javatpoint.com/android-XMLPullParser-tutorial
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // we will get value of a tag by giving name of that tag
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            // passing the input we intend to parse which take an InputStream as a parameter
            xmlPullParser.setInput(new StringReader(xmlData));
            // eventType will tell us whether we are at start of a tag or end of a tag etc
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String currTagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag of: "+currTagName);
                        if(currTagName.equalsIgnoreCase("entry")){
                            // if our currTagName is <entry> then we are going to see another new entry and we will create another new FeedEntry object.
                            insideEntry = true;
                            currEntry = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:    // if our eventType is text, then store it in currTagValue
                        currTagValue = xmlPullParser.getText();
                        break;
                    case XmlPullParser.END_TAG: // if our eventType is end_tag, then store our currTagValue appropriately
                        if(currTagName==null)break; // currTagName can be null, in that case we don't check it
                        Log.d(TAG, "parse: ending tag of: "+currTagName);
                        if(currTagName.equalsIgnoreCase("entry")) {
                            appInfoList.add(currEntry);// if we reached at the end of <entry> then we successfully create a FeedEntry object hence add it to our appInfoList
                            insideEntry = false;
                        }
                        else if(insideEntry && currTagName.equalsIgnoreCase("name"))    // if we reached at end of <name> inside <entry> update name of FeedEntry
                            currEntry.setName(currTagValue);
                        else if(currTagName.equalsIgnoreCase("artist"))// if we reached at end of <artist> inside <entry> update artist of FeedEntry
                            currEntry.setArtist(currTagValue);
                        else if(currTagName.equalsIgnoreCase("summary"))
                            currEntry.setSummery(currTagValue);
                        else if(insideEntry && currTagName.equalsIgnoreCase("id"))
                            currEntry.setAppURL(currTagValue);
                        else if(currTagName.equalsIgnoreCase("releasedate"))
                            currEntry.setReleaseDate(currTagValue);
                }
                // after processing currTagName, goto next tag
                eventType = xmlPullParser.next();
            }

        }catch (Exception e){
            Log.d(TAG, "parsing is not successful");
            e.printStackTrace();
            isAllGood = false;
        }

        return isAllGood;
    }
}
