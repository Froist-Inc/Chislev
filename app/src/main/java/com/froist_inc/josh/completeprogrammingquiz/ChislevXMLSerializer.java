package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, ArrayList<ChislevQuestion>> ParseQuestions(final String parentDirectory, final String filename )
            throws IOException, XmlPullParserException
    {
        ChislevFileManager fileManager = new ChislevFileManager( mContext );
        if( !fileManager.FileExists( filename, parentDirectory, false ) ){
            throw new IOException( "File does not exist." );
        }
        File parentDirectoryFile = new File( mContext.getFilesDir(), parentDirectory );
        File file = new File( parentDirectoryFile.getCanonicalPath(), filename );

        String data = fileManager.ReadDataFromFile( file.getCanonicalPath() );
        XmlPullParser xmlPullParser = GetXMLParserForData( data );
        return ParseQuestionData( xmlPullParser );
    }

    Map<String, ArrayList<ChislevQuestion>> ParseQuestionData( XmlPullParser xmlPullParser )
            throws IOException, XmlPullParserException
    {
        Map<String, ArrayList<ChislevQuestion>> questionMap = Collections.synchronizedMap( new HashMap<String,
                ArrayList<ChislevQuestion>>());

        final String element = "Element", question = "Question", explanation = "Explanation", hint = "Hint",
                code = "Code", level = "Level", options = "Options";

        int eventType = xmlPullParser.next();
        ChislevQuestion newQuestion = null;

        while( eventType != XmlPullParser.END_DOCUMENT )
        {
            switch ( eventType )
            {
                case XmlPullParser.START_TAG:
                    final String name = xmlPullParser.getName();
                    if( element.equals( name ) ){
                        newQuestion = new ChislevQuestion();
                    } else if( newQuestion != null ){
                        if( question.equals( name )){
                            newQuestion.setQuestion( xmlPullParser.nextText() );
                        } else if( explanation.equals( name ) ){
                            newQuestion.setCorrectAnswer( xmlPullParser.nextText() );
                        } else if( hint.equals( name ) ){
                            newQuestion.setHint( xmlPullParser.nextText() );
                        } else if( code.equals( name )){
                            newQuestion.setCode( xmlPullParser.nextText() );
                        } else if( level.equals( name )){
                            newQuestion.setDifficultyLevel( xmlPullParser.nextText().trim() );
                        } else if( options.equals( name ) ){
                            final String text = xmlPullParser.nextText();
                            ArrayList<String> elements = new ArrayList<>();
                            String [] availableOptions = text.split( "\\r?\\n" );
                            Collections.addAll( elements, availableOptions );
                            newQuestion.setAvailableOptions( elements );
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    final String tagName = xmlPullParser.getName();
                    if( tagName.equalsIgnoreCase( element ) && newQuestion != null ){
                        if( questionMap.get( newQuestion.getDifficultyLevel() ) == null ){
                            questionMap.put( newQuestion.getDifficultyLevel(), new ArrayList<ChislevQuestion>() );
                        }
                        questionMap.get( newQuestion.getDifficultyLevel() ).add( newQuestion );
                    }
            }
            eventType = xmlPullParser.next();
        }
        return questionMap;
    }
}
