package com.georgev22.library.utilities;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerWrapper extends java.util.logging.Logger {
    private final Logger log4j;
    private final org.slf4j.Logger slf4j;

    public LoggerWrapper(Logger logger) {
        super("logger", null);
        this.log4j = logger;
        this.slf4j = null;
    }

    public LoggerWrapper(org.slf4j.Logger logger) {
        super("logger", null);
        this.log4j = null;
        this.slf4j = logger;
    }

    @Override
    public void log(@NotNull LogRecord record) {
        log(record.getLevel(), record.getMessage());
    }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg);
            } else if (slf4j != null) {
                slf4j.debug(msg);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg);
            } else if (slf4j != null) {
                slf4j.warn(msg);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg);
            } else if (slf4j != null) {
                slf4j.error(msg);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg);
            } else if (slf4j != null) {
                slf4j.info(msg);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg);
            } else if (slf4j != null) {
                slf4j.trace(msg);
            }
        }
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg, param1);
            } else if (slf4j != null) {
                slf4j.debug(msg, param1);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg, param1);
            } else if (slf4j != null) {
                slf4j.warn(msg, param1);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg, param1);
            } else if (slf4j != null) {
                slf4j.error(msg, param1);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg, param1);
            } else if (slf4j != null) {
                slf4j.info(msg, param1);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg, param1);
            } else if (slf4j != null) {
                slf4j.trace(msg, param1);
            }
        }
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        log(level, MessageFormat.format(msg, params)); // workaround not formatting correctly
    }

    @Override
    public void log(Level level, String msg, Throwable params) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg, params);
            } else if (slf4j != null) {
                slf4j.debug(msg, params);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg, params);
            } else if (slf4j != null) {
                slf4j.warn(msg, params);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg, params);
            } else if (slf4j != null) {
                slf4j.error(msg, params);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg, params);
            } else if (slf4j != null) {
                slf4j.info(msg, params);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg, params);
            } else if (slf4j != null) {
                slf4j.trace(msg, params);
            }
        }
    }

}
