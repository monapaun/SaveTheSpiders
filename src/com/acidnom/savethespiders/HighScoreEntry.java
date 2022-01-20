package com.acidnom.savethespiders;

public class HighScoreEntry
{
    public String mName;
    public Integer mScore;

    public HighScoreEntry()
    {
        mName="";
        mScore=0;
    }

    public HighScoreEntry(String strName, Integer iScore)
    {
        mName = strName;
        mScore = iScore;
    }
}