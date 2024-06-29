package org.example.util;
public class LoggerConstants {
    public static final String LOG_BREAKER_OPEN = "**********************************************************************";
    public static final String LOG_BREAKER_CLOSE = "############################## xxxxxxxx ##############################";
    public static final String LOG_PREFIX = "----------  ";
    public static final String LOG_SUFFIX = "  ----------";
    public static final String DATA_INTEGRITY_VIOLATION_MSG = "Rejected : Deleting process was failed because this entity was connected with other resources.If you try to forcely remove it, entire database will loose consistency";
    public static final String DUPLICATE_KEY_INSERT_FAILED_MSG = "Inserting process was failed due to Unique Key constraint";
    public static final String DUPLICATE_KEY_UPDATE_FAILED_MSG = "Updating process was failed due to Unique Key constraint";
}