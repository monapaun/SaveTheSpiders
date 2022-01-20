package com.acidnom.savethespiders;

import java.util.List;

import android.content.Context;

public class HighScoreTable {

    private DataHelper mDBsql;
    private HighScoreEntry[] mHighScoreArray;
    public static final int HIGHSCORE_NUM_ENTRIES    = 8;
    private static final int HIGHSCORE_DEFAULT_SCORE = 0;
    
    public HighScoreTable(Context context)
    {
    	mHighScoreArray = new HighScoreEntry[HIGHSCORE_NUM_ENTRIES];
 	    mDBsql = new DataHelper(context);
 	    List<HighScoreEntry> hsList = mDBsql.selectAll();

 	    if (hsList.size() < HIGHSCORE_NUM_ENTRIES)
 	    {
 	        //create default highscores (strategy)
 	        // note: starting at index 0 = highest score
 	        mDBsql.deleteAll();
 	        
 	        for (int i=0; i<HIGHSCORE_NUM_ENTRIES; i++)
 	        {
 	            mHighScoreArray[i]=new HighScoreEntry();
 	            mHighScoreArray[i].mName="Anonymous";
 	            mHighScoreArray[i].mScore=(HIGHSCORE_NUM_ENTRIES-i)*HIGHSCORE_DEFAULT_SCORE;
                mDBsql.insertData(mHighScoreArray[i]);
 	        }
 	    }
 	    else
 	    {
 	        //Load Highscores
 	        //int i=0;
 	        for (int i=0; i < HIGHSCORE_NUM_ENTRIES; i++)//(HighScoreEntry hsEntry : hsList)
 	        {
 	            mHighScoreArray[i]=hsList.get(i);
 	        }
 	    }
    }
    
    public void addNewHighScore(String name, int score)
    {
    	HighScoreEntry nHighScore = new HighScoreEntry();
		
		nHighScore.mName = name;
		nHighScore.mScore = score;
		
		mDBsql.insertData(nHighScore);
    }
    
    public int getLowestHighScore()
    {
    	return mHighScoreArray[HIGHSCORE_NUM_ENTRIES-1].mScore.intValue();
    }
    
    public HighScoreEntry[] getHighScores()
    {
    	return mHighScoreArray;
    }
    
    public void closeDB()
    {
    	mDBsql.closeDB();
    }
    
    
	
}
