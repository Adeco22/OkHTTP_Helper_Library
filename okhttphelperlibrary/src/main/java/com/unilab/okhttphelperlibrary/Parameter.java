package com.unilab.okhttphelperlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @author Anthony Deco
 * @since 5:24 PM 5/30/2019
 *
 * Parameter object
 */
public class Parameter implements Parcelable {

    private String parameter_key;
    private String parameter_value;

    public Parameter() {
    }

    public Parameter(String parameter_key, String parameter_value) {
        this.parameter_key = parameter_key;
        this.parameter_value = parameter_value;
    }

    protected Parameter(Parcel in) {
        parameter_key = in.readString();
        parameter_value = in.readString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameter)) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(parameter_key, parameter.parameter_key) &&
                Objects.equals(parameter_value, parameter.parameter_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter_key, parameter_value);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "parameter_key='" + parameter_key + '\'' +
                ", parameter_value='" + parameter_value + '\'' +
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
    }
}
