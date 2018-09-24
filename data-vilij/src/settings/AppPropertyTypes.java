package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    SETTINGS_ICON,

    SETTINGS_TOOLTIP,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    INVALID_TEXTAREA_TITLE,

    INVALID_TEXTAREA_MSG,

    SAME_DATA_TITLE,

    SAME_DATA_MSG,

    INVALID_SAVE_TITLE,

    INVALID_SAVE,



    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,
    INDENTICAL_NAME_MSG,
    IDENTICAL_name,

    GUI_RESOURCE_PATH1,
    CSS_RESOURCE_PATH1,
    CSS_RESOURCE_FILENAMES,

    DATA_RESOURCE_PATH1,
    CLASSIFIYING_FILENAMES,
    CLUSTERING_FILENAMES

    /* application-specific parameters
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
     error messages
    RESOURCE_SUBDIR_NOT_FOUND*/
}
