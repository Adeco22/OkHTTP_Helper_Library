package com.unilab.okhttphelperlibrary;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * Convenience class for Parameters
 *
 * @author Anthony Deco
 * @since 5:24 PM 5/30/2019
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Parameter implements Parcelable {
    private String parameter_key;
    private String parameter_value;
    private File parameter_file;
    private String parameter_type = TYPE_TEXT;
    private String MIME_type = MEDIA_TYPE_PLAIN_TEXT;

    public Parameter() {
    }

    public Parameter(String parameter_key, String parameter_value) {
        this.parameter_key = parameter_key;
        this.parameter_value = parameter_value;
    }

    public Parameter(String parameter_key, File parameter_file) {
        this.parameter_key = parameter_key;
        this.parameter_file = parameter_file;
    }

    public Parameter(String parameter_key, String parameter_value, @ParameterType String parameter_type, @MIMEType String MIME_type) {
        this.parameter_key = parameter_key;
        this.parameter_value = parameter_value;
        this.parameter_type = parameter_type;
        this.MIME_type = MIME_type;
    }

    public Parameter(String parameter_key, File parameter_file, @MIMEType String MIME_type) {
        this.parameter_key = parameter_key;
        this.parameter_file = parameter_file;
        this.parameter_type = TYPE_FILE;
        this.MIME_type = MIME_type;
    }

    protected Parameter(Parcel in) {
        parameter_key = in.readString();
        parameter_value = in.readString();
        parameter_type = in.readString();
        MIME_type = in.readString();
        parameter_file = new File(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parameter_key);
        dest.writeString(parameter_value);
        dest.writeString(parameter_type);
        dest.writeString(MIME_type);
        dest.writeString(parameter_file.getAbsolutePath());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Parameter> CREATOR = new Creator<Parameter>() {
        @Override
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        @Override
        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };

    public String getParameter_key() {
        return parameter_key;
    }

    public void setParameter_key(String parameter_key) {
        this.parameter_key = parameter_key;
    }

    public String getParameter_value() {
        return parameter_value;
    }

    public void setParameter_value(String parameter_value) {
        this.parameter_value = parameter_value;
    }

    public File getParameter_file() {
        return parameter_file;
    }

    public void setParameter_file(File parameter_file) {
        this.parameter_file = parameter_file;
    }

    public String getParameter_type() {
        return parameter_type;
    }

    public void setParameter_type(@ParameterType String parameter_type) {
        this.parameter_type = parameter_type;
    }

    public String getMIME_type() {
        return MIME_type;
    }

    public void setMIME_type(@MIMEType String MIME_type) {
        this.MIME_type = MIME_type;
    }

    public void setCustomParameter_media_type(String parameter_media_type) {
        this.MIME_type = parameter_media_type;
    }

    public boolean isFileParameter() {
        return (TYPE_FILE.equals(parameter_type));
    }

    public boolean isFileValue(){
        return this.parameter_file != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameter)) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(parameter_key, parameter.parameter_key) &&
                Objects.equals(parameter_value, parameter.parameter_value) &&
                Objects.equals(parameter_file, parameter.parameter_file) &&
                Objects.equals(parameter_type, parameter.parameter_type) &&
                Objects.equals(MIME_type, parameter.MIME_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter_key, parameter_value, parameter_file, parameter_type, MIME_type);
    }

    @Override
    @NonNull
    public String toString() {
        return "Parameter{" +
                "parameter_key='" + parameter_key + '\'' +
                ", parameter_value='" + parameter_value + '\'' +
                ", parameter_file=" + parameter_file +
                ", parameter_type='" + parameter_type + '\'' +
                ", MIME_type='" + MIME_type + '\'' +
                '}';
    }

    public static final String TYPE_FILE = "TYPE_FILE";
    public static final String TYPE_RAW = "TYPE_RAW";
    public static final String TYPE_TEXT = "TYPE_TEXT";

    @StringDef({
            TYPE_FILE,
            TYPE_RAW,
            TYPE_TEXT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ParameterType {
    }

    public static final String MEDIA_TYPE_BITMAP = "image/bmp";
    public static final String MEDIA_TYPE_GIF = "image/gif";
    public static final String MEDIA_TYPE_HTML = "text/html";
    public static final String MEDIA_TYPE_JAVASCRIPT= "application/javascript";
    public static final String MEDIA_TYPE_JPEG = "image/jpeg";
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_TYPE_PLAIN_TEXT = "text/plain";
    public static final String MEDIA_TYPE_PNG = "image/png";
    public static final String MEDIA_TYPE_RICH_TEXT = "text/html";
    public static final String MEDIA_TYPE_XML = "application/xml";
    public static final String MEDIA_TYPE_ZIP = "application/zip";

    @StringDef({
            MEDIA_TYPE_BITMAP,
            MEDIA_TYPE_GIF,
            MEDIA_TYPE_HTML,
            MEDIA_TYPE_JAVASCRIPT,
            MEDIA_TYPE_JPEG,
            MEDIA_TYPE_JSON,
            MEDIA_TYPE_PLAIN_TEXT,
            MEDIA_TYPE_PNG,
            MEDIA_TYPE_RICH_TEXT,
            MEDIA_TYPE_XML,
            MEDIA_TYPE_ZIP,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MIMEType {
    }
}
