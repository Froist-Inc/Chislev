package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
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

    private XmlPullParser GetXMLParserForData( final String data )
            throws XmlPullParserException
    {
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
        xmlPullParser.setInput( new StringReader( data ) );

        return xmlPullParser;
    }

    public ArrayList<ChislevSubjectInformation> ParseConfigData( String data )
            throws XmlPullParserException, IOException
    {
        if( data == null ) return null;
        ArrayList<ChislevSubjectInformation> informationList = new ArrayList<>();
        XmlPullParser xmlPullParser = GetXMLParserForData( data );
        ParseData( xmlPullParser, informationList );
        return informationList;
    }

    private void ParseData( XmlPullParser xmlPullParser, ArrayList<ChislevSubjectInformation> informationList )
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

    public ArrayList<ChislevQuestion> ParseQuestions( final String parentDirectory, final String filename )
            throws IOException, XmlPullParserException
    {
        File path = new File( parentDirectory, filename );
        if( !path.exists() ){
            throw new IOException( "File does not exist." );
        }
        String data = new ChislevFileManager( mContext ).ReadDataFromFile( path.getCanonicalPath() );
        XmlPullParser xmlPullParser = GetXMLParserForData( data );
        return ParseQuestionData( xmlPullParser );
    }

    ArrayList<ChislevQuestion> ParseQuestionData( XmlPullParser xmlPullParser )
            throws IOException, XmlPullParserException
    {
        ArrayList<ChislevQuestion> questionList = new ArrayList<>();
        int event = xmlPullParser.next();

        while( event != XmlPullParser.END_DOCUMENT )
        {
            ChislevQuestion currentQuestion = new ChislevQuestion();
            if( event == XmlPullParser.START_TAG && xmlPullParser.getName().toLowerCase().equals( "element" ) )
            {
                // todo lint says isStartTag is always true, I beg to defer. I'll look into this.
                boolean isStartTag = event == XmlPullParser.START_TAG;
                do {
                    if( isStartTag && xmlPullParser.getName().toLowerCase().equals( "question" ) ){
                        currentQuestion.setQuestion( xmlPullParser.getText().trim() );
                    } else if( isStartTag && xmlPullParser.getName().toLowerCase().equals( "explanation" )){
                        currentQuestion.setCorrectAnswer( xmlPullParser.getText().trim() );
                    } else if( isStartTag && xmlPullParser.getName().toLowerCase().equals( "hint" ) ){
                        currentQuestion.setHint( xmlPullParser.getText().trim() );
                    } else if( isStartTag && xmlPullParser.getName().toLowerCase().equals( "code" ) ){
                        currentQuestion.setCode( xmlPullParser.getText().trim() );
                    } else if ( isStartTag && xmlPullParser.getName().toLowerCase().equals( "level" )){
                        currentQuestion.setCode( xmlPullParser.getText().trim() );
                    } else {
                        if( isStartTag && xmlPullParser.getName().toLowerCase().equals( "options" ) ){
                            ArrayList<String> options = new ArrayList<>();
                            String[] text = xmlPullParser.getText().trim().split( "\\r?\\n" );
                            for ( String t : text ) options.add( t );
                            currentQuestion.setAvailableOptions( options );
                        }
                    }
                    event = xmlPullParser.next();
                    isStartTag = ( event == XmlPullParser.START_TAG );
                } while ( !( XmlPullParser.END_TAG == event && xmlPullParser.getName().toLowerCase().equals( "element" ) )
                        && event != XmlPullParser.END_DOCUMENT );
            }
            questionList.add( currentQuestion );
            event = xmlPullParser.next();
        }
        return questionList;
    }
}
