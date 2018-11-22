package tw.com.edu.ntue.toybabysitter.model;

import com.google.gson.annotations.SerializedName;

public class DbNoteSelect {
    @SerializedName("date")
    private String date;
    @SerializedName("message")
    private String message;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
