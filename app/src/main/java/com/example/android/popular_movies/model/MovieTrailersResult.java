package com.example.android.popular_movies.model;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//import org.apache.commons.lang.builder.ToStringBuilder;

public class MovieTrailersResult implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieTrailer> results = new ArrayList<MovieTrailer>();
    public final static Parcelable.Creator<MovieTrailersResult> CREATOR = new Creator<MovieTrailersResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MovieTrailersResult createFromParcel(Parcel in) {
            return new MovieTrailersResult(in);
        }

        public MovieTrailersResult[] newArray(int size) {
            return (new MovieTrailersResult[size]);
        }

    }
            ;

    protected MovieTrailersResult(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (com.example.android.popular_movies.model.MovieTrailer.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public MovieTrailersResult() {
    }

    /**
     *
     * @param id
     * @param results
     */
    public MovieTrailersResult(Integer id, List<MovieTrailer> results) {
        super();
        this.id = id;
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieTrailer> getMovieTrailers() {
        return results;
    }

    public void setMovieTrailers(List<MovieTrailer> results) {
        this.results = results;
    }

  /*  @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("results", results).toString();
    }*/

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}