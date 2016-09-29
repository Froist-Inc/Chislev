package com.froist_inc.josh.completeprogrammingquiz;

public class ChislevSubjectInformation
{
    public static final String SOLUTION_FILENAME = "solution.db";
    public static final String ICON_FILENAME = "icon.png";
    public static final String CHECKSUM_FILENAME = "detail.xml";

    private String mSubjectName = null;
    private String mSubjectCode = null;
    private String mSubjectDataUrl = null;
    private String mSubjectFilename = null;
    private String mSubjectSolutionDBUrl = null;
    private String mSubjectIconUrl = null;
    private String mSubjectDetailsCheckSums = null;

    private boolean mIsAllSet = false;
    private String mIconFilename = null;
    /**
     *
     * @param name name given to the subject, e.g. C++, Java, Analogy
     * @param code name of the root directory containing the necessary information regarding
     *             the subject. e.g. for C++, code could be cpp.
     * @param dataUrl name of the URL containing the resource for `name`
     * @param solutionDbUrl url leading to the solution's database online
     * @param filename local filename to save the resource file of the subject
     * @param checksum filename to the location of the checksums
     */
    public ChislevSubjectInformation( String name, String code, String dataUrl, String filename,
                                      String solutionDbUrl, String checksum )
    {
        mSubjectName = name;
        mSubjectCode = code;
        mSubjectDataUrl = dataUrl;
        mSubjectFilename = filename;
        mSubjectSolutionDBUrl = solutionDbUrl;
        mSubjectDetailsCheckSums = checksum;
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

    public String getSubjectSolutionDBUrl() {
        return mSubjectSolutionDBUrl;
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

    public String getSubjectDetailsCheckSums() {
        return mSubjectDetailsCheckSums;
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
