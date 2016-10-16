package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.util.Log;

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

class ChislevXMLSerializer
{
    private final Context mContext;
    private static final String SUBJECT = "subject";

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

    private String GetDataFromDirectoryFilename( final String parentDirectory, final String filename )
            throws IOException
    {
        ChislevFileManager fileManager = new ChislevFileManager( mContext );
        if( !fileManager.FileExists( filename, parentDirectory ) ){
            throw new IOException( "File does not exist." );
        }
        File parentDirectoryFile = new File( mContext.getFilesDir(), parentDirectory );
        File file = new File( parentDirectoryFile.getCanonicalPath(), filename );

        return fileManager.ReadDataFromFile( file.getCanonicalPath() );
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
                        subjectChecksumFilename = xmlPullParser.getAttributeValue( null, "detail" ),
                        subjectSolutionDB = xmlPullParser.getAttributeValue( null, "solution" ),
                        subjectIconUrl = xmlPullParser.getAttributeValue( null, "icon" );
                ChislevSubjectInformation subjectInformation = new ChislevSubjectInformation( subjectName,
                        subjectCode, subjectUrl, subjectFilename, subjectSolutionDB, subjectChecksumFilename );
                subjectInformation.setSubjectIconUrl( subjectIconUrl );

                informationList.add( subjectInformation );
            }
            xmlEvent = xmlPullParser.next();
        }
    }

    public Map<String, ArrayList<ChislevQuestion>> ParseQuestions( final String parentDirectory, final String filename )
            throws IOException, XmlPullParserException
    {
        final String data = GetDataFromDirectoryFilename( parentDirectory, filename );
        XmlPullParser xmlPullParser = GetXMLParserForData( data );
        return ParseQuestionData( xmlPullParser );
    }

    private Map<String, ArrayList<ChislevQuestion>> ParseQuestionData(XmlPullParser xmlPullParser)
            throws IOException, XmlPullParserException
    {
        Map<String, ArrayList<ChislevQuestion>> questionMap = Collections.synchronizedMap( new HashMap<String,
                ArrayList<ChislevQuestion>>());

        final String element = "Element", question = "Question", code = "Code", level = "Level", options = "Options";

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
                        newQuestion.setReferenceID( xmlPullParser.getAttributeValue( null, "reference_id" ) );
                    } else if( newQuestion != null ){
                        if( question.equals( name )){
                            newQuestion.setQuestion( xmlPullParser.nextText() );
                        } else if( code.equals( name )){
							eventType = xmlPullParser.nextToken();
							while( eventType != XmlPullParser.END_DOCUMENT ){
								if( eventType == XmlPullParser.CDSECT ){
		                            newQuestion.setCode( xmlPullParser.getText() );
									break;
								}
								eventType = xmlPullParser.nextToken();
							}
                        } else if( level.equals( name )){
                            newQuestion.setDifficultyLevel( xmlPullParser.getAttributeValue( null, "value" ) );
                        } else if( options.equals( name ) ){
                            final String VALUE = "value", OPTION = "option";
                            ArrayList<String> elements = new ArrayList<>();
                            int event = xmlPullParser.next();
                            String tagName = xmlPullParser.getName();

                            while( event != XmlPullParser.END_DOCUMENT && !name.equals( tagName ) )
                            {
                                if( event == XmlPullParser.START_TAG && OPTION.equals( xmlPullParser.getName() ) )
                                {
                                    elements.add( xmlPullParser.getAttributeValue( null, VALUE ));
                                }
                                event = xmlPullParser.next();
                                tagName = xmlPullParser.getName();
                            }
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

    public String GetSubjectDetailChecksum( String directory, String filename )
    {
        try {
            final String data = GetDataFromDirectoryFilename( directory, filename );
            final XmlPullParser xmlParserForData = GetXMLParserForData( data );
            final String solEvent = "solution", updateMd5Value = "update_md5_value";
            int event = xmlParserForData.next();

            while ( event != XmlPullParser.END_DOCUMENT ) {
                switch ( event ) {
                    case XmlPullParser.START_TAG:
                        final String tagName = xmlParserForData.getName();
                        if ( tagName.equals( solEvent ) ) {
                            return xmlParserForData.getAttributeValue( null, updateMd5Value );
                        }
                        break;
                    default:
                        break;
                }
                event = xmlParserForData.next();
            }
        } catch( Exception exception ){
            Log.d( "GetSubjectChecksum", exception.getLocalizedMessage() );
        }
        return null;
    }
}
