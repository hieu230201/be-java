package org.example.basewebsub.logging.bases;


import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.common.MessageLog;

public interface ILogManage {
    boolean debug(String message);

    boolean debug(MessageLog messageLog);

    boolean debug(MessageLog messageLog, LogManage.Configuration configuration);

    boolean trace(String message);

    boolean trace(MessageLog messageLog);

    boolean trace(MessageLog messageLog, LogManage.Configuration configuration);

    boolean info(String message);

    boolean info(MessageLog messageLog);

    boolean info(MessageLog messageLog, LogManage.Configuration configuration);

    boolean error(String message, Exception e);

    boolean error(MessageLog messageLog);

    boolean error(MessageLog messageLog, LogManage.Configuration configuration);

    boolean fatal(String message, Exception e);

    boolean fatal(MessageLog messageLog);

    boolean fatal(MessageLog messageLog, LogManage.Configuration configuration);
}