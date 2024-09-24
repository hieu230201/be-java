package org.example.basewebsub.logging.common;


import org.example.basewebsub.util.StringBuilderPlus;
import org.example.basewebsub.util.StringUtil;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageLog {
    String id;
    String className;
    String methodName;
    Map<String, ?> parameters;
    Map<String, ?> messages;
    Exception exception;
    Long duration;
    String path;

    public Long getDuration() {
        return duration == null ? 0 : duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public MessageLog() {
        this.id = UUID.randomUUID().toString();
    }

    public MessageLog(String className, String methodName,
                      Map<String, ?> parameters, Map<String, ?> messages,
                      Exception exception) {
        this();
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.messages = messages;
        this.exception = exception;
    }

    public MessageLog(String id, String className,
                      String methodName, Map<String, ?> parameters,
                      Map<String, ?> messages,
                      Exception exception) {
        this(className, methodName, parameters, messages, exception);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public MessageLog setId(String id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public MessageLog setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public MessageLog setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MessageLog setPath(String path) {
        this.path = path;
        return this;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public MessageLog setParameters(Map<String, ?> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Map<String, ?> getMessages() {
        return messages;
    }

    public MessageLog setMessages(Map<String, ?> messages) {
        this.messages = messages;
        return this;
    }

    public MessageLog setMessage(String message) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", message);
        this.messages = map;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public MessageLog setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    @Override
    public String toString() {
        StringBuilderPlus parameterBuilder = new StringBuilderPlus();
        if (parameters != null) {
            for (Map.Entry<String, ?> entry : parameters.entrySet()) {
                parameterBuilder.appendLine(MessageFormat.format("    + {0}: {1}", entry.getKey(), entry.getValue()));
            }
        }

        StringBuilderPlus strBuilder = new StringBuilderPlus();
        strBuilder.appendLine();
        strBuilder.appendLine("- Id: " + (StringUtil.isBlank(id) ? UUID.randomUUID() : id));
        strBuilder.appendLine("- Classname: " + className);
        strBuilder.appendLine("- MethodName: " + methodName);
        if (parameterBuilder.length() > 0) {
            strBuilder.appendLine("- Parameters: ");
            strBuilder.append(parameterBuilder.toString());
        }

        StringBuilderPlus messagesBuilder = new StringBuilderPlus();
        if (messages != null) {
            for (Map.Entry<String, ?> entry : messages.entrySet()) {
                messagesBuilder.appendLine(MessageFormat.format("    + {0}: {1}", entry.getKey(), entry.getValue()));
            }
        }
        if (messagesBuilder.length() > 0) {
            strBuilder.appendLine("- Messages: ");
            strBuilder.append(messagesBuilder.toString());
        }


        return strBuilder.toString();
    }
}