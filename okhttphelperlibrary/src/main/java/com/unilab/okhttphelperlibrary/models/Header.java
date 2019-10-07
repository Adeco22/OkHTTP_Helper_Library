package com.unilab.okhttphelperlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Convenience class for Headers
 *
 * @author Anthony Deco
 * @since 5:20 PM 5/30/2019
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Header implements Parcelable {

    private String header_key;
    private String header_value;

    public Header() {
    }

    public Header(String header_key, String header_value) {
        this.header_key = header_key;
        this.header_value = header_value;
    }

    protected Header(Parcel in) {
        header_key = in.readString();
        header_value = in.readString();
    }

    public static final Creator<Header> CREATOR = new Creator<Header>() {
        @Override
        public Header createFromParcel(Parcel in) {
            return new Header(in);
        }

        @Override
        public Header[] newArray(int size) {
            return new Header[size];
        }
    };

    public String getHeader_key() {
        return header_key;
    }

    public void setHeader_key(String header_key) {
        this.header_key = header_key;
    }

    public String getHeader_value() {
        return header_value;
    }

    public void setHeader_value(String header_value) {
        this.header_value = header_value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Header)) return false;
        Header header = (Header) o;
        return Objects.equals(header_key, header.header_key) &&
                Objects.equals(header_value, header.header_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header_key, header_value);
    }

    @NonNull
    @Override
    public String toString() {
        return "Header{" +
                "header_key='" + header_key + '\'' +
                ", header_value='" + header_value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header_key);
        dest.writeString(header_value);
    }
}
