package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class ChislevXMLSerializer
{
    private Context mContext;
    public static final String SUBJECT = "subject";

    public ChislevXMLSerializer( Context context )
    {
        mContext = context;
    }

    public ArrayList<ChislevSubjectInformation> ParseConfigData( String data )
            throws XmlPullParserException, IOException
    {
        if( data == null ) return null;
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
        xmlPullParser.setInput( new StringReader( data ) );

        ArrayList<ChislevSubjectInformation> informationList = new ArrayList<>();
        ParseData( xmlPullParser, informationList );
        return informationList;
    }

    public void ParseData(XmlPullParser xmlPullParser, ArrayList<ChislevSubjectInformation> informationList )
            throws XmlPullParserException, IOException
    {
        int xmlEvent = xmlPullParser.next();

        while( xmlEvent != XmlPullParser.END_DOCUMENT )
        {
            if( xmlEvent == XmlPullParser.START_TAG && SUBJECT.equals( xmlPullParser.getName() ) )
            {
                String subjectName = xmlPullParser.getAttributeValue( null, "name" ),
                        subjectCode = xmlPullParser.getAttributeValue( null, "code" ),
                        subjectUrl = xmlPullParser.getAttributeValue( null, "location" ),
                        subjectFilename = xmlPullParser.getAttributeValue( null, "filename" ),
                        subjectUpdatedFilename = xmlPullParser.getAttributeValue( null, "new_filename" ),
                        subjectAnswerUrl = xmlPullParser.getAttributeValue( null, "answer" ),
                        subjectIconUrl = xmlPullParser.getAttributeValue( null, "icon" );
                ChislevSubjectInformation subjectInformation = new ChislevSubjectInformation( subjectName,
                        subjectCode, subjectUrl, subjectAnswerUrl, subjectFilename, subjectUpdatedFilename );
                subjectInformation.setSubjectIconUrl( subjectIconUrl );

                informationList.add( subjectInformation );
            }
            xmlEvent = xmlPullParser.next();
        }
    }
}
