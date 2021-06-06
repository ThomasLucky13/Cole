package com.example.developerslife;

public class MemStruct {
    String Text;
    String gifURL;
    String author;
    String date;
    public MemStruct ()
    {
        Text = "none";
        gifURL = "none";
        author = "none";
        date = "none";
    }
    public MemStruct (String newText, String newURL, String newAuthor, String newDate)
    {
        Text = newText;
        gifURL = newURL.replaceFirst("http:", "https:");
        author = newAuthor;
        date = newDate;
    }
}
