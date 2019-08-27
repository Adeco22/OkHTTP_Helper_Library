package com.unilab.okhttphelperlibrary;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * @author Anthony Deco
 * @since 5:24 PM 5/30/2019
 * <p>
 * Parameter object
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Parameter implements Parcelable {

    public static final int TYPE_TEXT = 963;
    public static final int TYPE_FILE = 326;

    @IntDef({TYPE_TEXT, TYPE_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ParameterType {
    }

    public static final String MEDIA_TYPE_TEXT = "";
    public static final String MEDIA_TYPE_JPEG = "image/jpeg";
    public static final String MEDIA_TYPE_PNG = "image/png";
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";

    @StringDef({MEDIA_TYPE_TEXT, MEDIA_TYPE_JPEG, MEDIA_TYPE_PNG, MEDIA_TYPE_JSON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ParameterMediaType {
    }

    private String parameter_key;
    private String parameter_value;
    private int parameter_type = TYPE_TEXT;
    private String parameter_media_type = MEDIA_TYPE_TEXT;

    public Parameter() {
    }

    public Parameter(String parameter_key, String parameter_value) {
        this.parameter_key = parameter_key;
        this.parameter_value = parameter_value;
    }

    public Parameter(String parameter_key, String parameter_value, @ParameterType int parameter_type, @ParameterMediaType String parameter_media_type) {
        this.parameter_key = parameter_key;
        this.parameter_value = parameter_value;
        this.parameter_type = parameter_type;
        this.parameter_media_type = parameter_media_type;
    }

    protected Parameter(Parcel in) {
        parameter_key = in.readString();
        parameter_value = in.readString();
        parameter_type = in.readInt();
        parameter_media_type = in.readString();
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

    public int getParameter_type() {
        return parameter_type;
    }

    public void setParameter_type(@ParameterType int parameter_type) {
        this.parameter_type = parameter_type;
    }

    public String getParameter_media_type() {
        return parameter_media_type;
    }

    public void setParameter_media_type(@ParameterMediaType String parameter_media_type) {
        this.parameter_media_type = parameter_media_type;
    }

    public void setCustomParameter_media_type(String parameter_media_type) {
        this.parameter_media_type = parameter_media_type;
    }

    public boolean isFileParameter() {
        return (parameter_type == TYPE_FILE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameter)) return false;
        Parameter parameter = (Parameter) o;
        return parameter_type == parameter.parameter_type &&
                Objects.equals(parameter_key, parameter.parameter_key) &&
                Objects.equals(parameter_value, parameter.parameter_value) &&
                Objects.equals(parameter_media_type, parameter.parameter_media_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter_key, parameter_value, parameter_type, parameter_media_type);
    }

    @Override
    @NonNull
    public String toString() {
        return "Parameter{" +
                "parameter_key='" + parameter_key + '\'' +
                ", parameter_value='" + parameter_value + '\'' +
                ", parameter_type=" + parameter_type +
                ", parameter_media_type='" + parameter_media_type + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parameter_key);
        dest.writeString(parameter_value);
        dest.writeInt(parameter_type);
        dest.writeString(parameter_media_type);
    }
}
