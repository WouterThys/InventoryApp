package com.waldo.inventory.database;

import com.waldo.inventory.classes.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.waldo.inventory.classes.Log.*;

public class LogManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

    public static LogManager LOG (Class logClass) {
        return new LogManager(logClass, ERROR);
    }

    public static LogManager LOG (Class logClass, int level) {
        return new LogManager(logClass, level);
    }

    private Class logClass;

    private boolean logInfo;
    private boolean logDebug;
    private boolean logWarnings;
    private boolean logErrors;

    private LogManager(Class logCLass, int level) {
        this.logClass = logCLass;
        // TODO: get setting booleans
        logInfo = true;
        logDebug = true;
        logWarnings = true;
        logErrors = true;

        // Overwrite default settings
        logLevel(level);
    }

    public void logLevel(int level) {
        switch (level) {
            case INFO:
                logInfo &= true;
                logDebug &= false;
                logWarnings &= false;
                logErrors &= false;
                break;
            case DEBUG:
                logInfo &= true;
                logDebug &= true;
                logWarnings &= false;
                logErrors &= false;
                break;
            case WARN:
                logInfo &= true;
                logDebug &= true;
                logWarnings &= true;
                logErrors &= false;
                break;
            case ERROR:
                logInfo &= true;
                logDebug &= true;
                logWarnings &= true;
                logErrors &= true;
                break;
        }
    }


    public void info(String info) {
        if (logInfo) {
            try {
                // Database
                if (DbManager.db().isInitialized()) {
                    Log log = new Log(INFO, logClass.getSimpleName(), info);
                    log.save();
                }
            } catch (Exception e) {
                System.err.println("COULD NOT LOG INFO");
            }
        }

        // Logger (info always logged?)
        LOGGER.info(info);
    }

    public void debug(String debug) {
        if (logDebug) {
            try {
                // Database
                if (DbManager.db().isInitialized()) {
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
                if (DbManager.db().isInitialized()) {
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
        if (logWarnings) {
            try {
                // Database
                if (DbManager.db().isInitialized()) {
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
        if (logWarnings) {
            try {
                // Database
                if (DbManager.db().isInitialized()) {
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
                if (DbManager.db().isInitialized()) {
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
                if (DbManager.db().isInitialized()) {
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
