package com.ai.pojo;

public class Log {

    private String _index;
    private String _type;
    private String _id;
    private String remote_ip;
    private String time;
    private String http_version;
    private String logtype;
    private String request;
    private String request_action;
    private String message;
    private String user_name;

    public Log(String _index, String _type, String _id, String remote_ip,
               String time, String http_version, String logtype, String request,
               String request_action, String message, String user_name) {
        this._index = _index;
        this._type = _type;
        this._id = _id;
        this.remote_ip = remote_ip;
        this.time = time;
        this.http_version = http_version;
        this.logtype = logtype;
        this.request = request;
        this.request_action = request_action;
        this.message = message;
        this.user_name = user_name;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRemote_ip() {
        return remote_ip;
    }

    public void setRemote_ip(String remote_ip) {
        this.remote_ip = remote_ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHttp_version() {
        return http_version;
    }

    public void setHttp_version(String http_version) {
        this.http_version = http_version;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequest_action() {
        return request_action;
    }

    public void setRequest_action(String request_action) {
        this.request_action = request_action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
