package uta.group23.wurdle.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class Leaderboard {
    private Player player;

    private HashMap<Player, Integer> scores;

    public Leaderboard() {
        this.scores = new HashMap<Player, Integer>();
    }

    public void storeScores() {
        scores.put(player, score);
    }

    List<Map.Entry<Player, Integer>> scoreList = new ArrayList<>(scores.entrySet());
    
    public void sortScores() 
    {
        //sort the scores in ascending order
        scoreList.sort(new Comparator<Map.Entry<Player, Integer>>() 
        {
            @Override
            public int compare(Map.Entry<Player, Integer> e1, Map.Entry<Player, Integer> e2) 
            {
                return e1.getValue().compareTo(e2.getValue());
            }
        });
        
        //clear the scores map for next game
        scores.clear();

        //add the sorted entries back into the scores map
        for (Map.Entry<Player, Integer> entry : scoreList) 
        {
            scores.put(entry.getKey(), entry.getValue());
        }    
    }

    public void updateScores() {

    }

    public void displayScores() 
    {
    //check is game contains complete scores
    if (scoreList != null && !scoreList.isEmpty()) 
        {
            //iterate over sorterd scores
            for (Map.Entry<Player, Integer> entry : scoreList) 
                {
                    //get player name and score
                    Player player = entry.getKey();
                    Integer score = entry.getValue();

                    //have to implement html display, but will do once grid is playable
                }
        }

    }
}
