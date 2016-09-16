package com.froist_inc.josh.completeprogrammingquiz;

public class ChislevSubjectInformation
{
    private String mSubjectName = null;
    private String mSubjectCode = null;
    private String mSubjectDataUrl = null;
    private String mSubjectAnswerUrl = null;
    private String mSubjectFilename = null;
    private String mSubjectUpdateFilename = null;
    private String mSubjectIconUrl = null;

    private boolean mIsAllSet = false;
    private String mIconFilename = null;
    /**
     *
     * @param name name given to the subject, e.g. C++, Java, Analogy
     * @param code name of the root directory containing the necessary information regarding
     *             the subject. e.g. for C++, code could be cpp.
     * @param dataUrl name of the URL containing the resource for `name`
     * @param answerUrl url leading to the answer's database online
     * @param filename local filename to save the resource file of the subject
     * @param updateFilename during an update, the `filename` resource file might change, this signifies
     *                       the new resource file to use in replacement of `filename`
     */
    public ChislevSubjectInformation( String name, String code, String dataUrl, String answerUrl,
                                      String filename, String updateFilename )
    {
        mSubjectName = name;
        mSubjectCode = code;
        mSubjectDataUrl = dataUrl;
        mSubjectAnswerUrl = answerUrl;
        mSubjectFilename = filename;
        mSubjectUpdateFilename = updateFilename;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public String getSubjectCode() {
        return mSubjectCode;
    }

    public String getSubjectDataUrl() {
        return mSubjectDataUrl;
    }

    public String getSubjectFilename() {
        return mSubjectFilename;
    }

    public String getSubjectAnswerUrl() {
        return mSubjectAnswerUrl;
    }

    public String getSubjectIconUrl() {
        return mSubjectIconUrl;
    }

    public void setSubjectIconUrl( String subjectIconUrl )
    {
        mSubjectIconUrl = subjectIconUrl;
        if( subjectIconUrl != null ){
            mIconFilename = "icon.png";
        }
    }

    public String getIconFilename()
    {
        return mIconFilename;
    }

    public void setIsAllSet(boolean isSet )
    {
        mIsAllSet = isSet;
    }
    public boolean isAllSet()
    {
        return mIsAllSet;
    }
}
