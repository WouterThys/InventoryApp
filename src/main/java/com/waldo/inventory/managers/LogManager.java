package com.waldo.inventory.managers;

import com.waldo.inventory.classes.dbclasses.Log;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.database.interfaces.DbSettingsListener;
import com.waldo.inventory.database.settings.settingsclasses.LogSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.waldo.inventory.Utils.Statics.LogTypes.*;
import static com.waldo.inventory.database.settings.SettingsManager.settings;


public class LogManager implements DbSettingsListener<LogSettings> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

    public static LogManager LOG (Class logClass) {
        return new LogManager(logClass, ERROR);
    }

    public static LogManager LOG (Class logClass, int level) {
        return new LogManager(logClass, level);
    }

    private int level;
    private final Class logClass;

    private boolean logInfo;
    private boolean logDebug;
    private boolean logWarn;
    private boolean logErrors;

    private LogManager(Class logCLass, int level) {
        this.logClass = logCLass;

        settings().addLogSettingsListener(this);

        // Overwrite default settings
        setLogLevel(level);
    }

    @Override
    public void onSettingsChanged(LogSettings newSettings) {
        updateSettings(newSettings);
        setLogLevel(level);
    }

    private void updateSettings(LogSettings logSettings) {
        if (logSettings != null) {
            logInfo = logSettings.isLogInfo();
            logDebug = logSettings.isLogDebug();
            logWarn = logSettings.isLogWarn();
            logErrors = logSettings.isLogError();
        }
    }

    public void setLogLevel(int level) {
        this.level = level;
        switch (level) {
            case INFO:
                logInfo &= true;
                logDebug &= false;
                logWarn &= false;
                logErrors &= false;
                break;
            case DEBUG:
                logInfo &= true;
                logDebug &= true;
                logWarn &= false;
                logErrors &= false;
                break;
            case WARN:
                logInfo &= true;
                logDebug &= true;
                logWarn &= true;
                logErrors &= false;
                break;
            case ERROR:
                logInfo &= true;
                logDebug &= true;
                logWarn &= true;
                logErrors &= true;
                break;
        }
    }

    public void startup(String startupPath)  {
        LOGGER.info("\n \t Starting application \n *******************************************************************\n");
        LOGGER.info("Start application at " + startupPath);
    }

    public void info(String info) {
        if (logInfo) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(INFO, logClass.getSimpleName(), info);
                    log.save();
                }

                // Logger (info always logged?)
                LOGGER.info(info);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG INFO");
            }
        }
    }

    public void debug(String debug) {
        if (logDebug) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(DEBUG, logClass.getSimpleName(), debug);
                    log.save();
                }

                // Logger
                LOGGER.debug(debug);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG DEBUG");
            }
        }
    }

    public void debug(String debug, Throwable throwable) {
        if (logDebug) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(DEBUG, logClass.getSimpleName(), debug, throwable);
                    log.save();
                }

                // Logger
                LOGGER.debug(debug, throwable);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG DEBUG");
            }
        }
    }

    public void warning(String warning) {
        if (logWarn) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(WARN, logClass.getSimpleName(), warning);
                    log.save();
                }

                // Logger
                LOGGER.warn(warning);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG WARNING");
            }
        }
    }

    public void warning(String warning, Throwable throwable) {
        if (logWarn) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(WARN, logClass.getSimpleName(), warning, throwable);
                    log.save();
                }

                // Logger
                LOGGER.warn(warning, throwable);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG WARNING");
            }
        }
    }

    public void error(String error) {
        if (logErrors) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(ERROR, logClass.getSimpleName(), error);
                    log.save();
                }

                // Logger
                LOGGER.error(error);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG ERROR");
            }
        }
    }

    public void error(String error, Throwable throwable) {
        if (logErrors) {
            try {
                // Database
                if (DatabaseAccess.db().isInitialized()) {
                    Log log = new Log(ERROR, logClass.getSimpleName(), error, throwable);
                    log.save();
                }

                // Logger
                LOGGER.error(error, throwable);
            } catch (Exception e) {
                System.err.println("COULD NOT LOG ERROR");
            }
        }
    }
}
