import java.util.*;
import java.lang.*;



class EloCalculator
{
    private final ArrayList<ELOPlayer> players = new ArrayList<ELOPlayer>();

    public void addPlayer(String name, int place, int elo)
    {
        ELOPlayer player = new ELOPlayer(name, elo, null);

        player.place   = place;

        players.add(player);
    }

    public int getELO(String name)
    {
        for (ELOPlayer p : players)
        {
            if (p.name.equals(name))
                return p.eloPost;
        }
        return 1500;
    }

    public int getELOChange(String name)
    {
        for (ELOPlayer p : players)
        {
            if (p.name.equals(name))
                return p.eloChange;
        }
        return 0;
    }

    public void calculateELOs()
    {
        int n = players.size();

        //The higher the k the more the Elos will change
        float K = 256 / (float)(n - 1);

        for (int i = 0; i < n; i++)
        {
            int curPlace = players.get(i).place;
            int curELO   = players.get(i).eloPre;

            for (int j = 0; j < n; j++)
            {
                if (i != j)
                {
                    int opponentPlace = players.get(j).place;
                    int opponentELO   = players.get(j).eloPre;

                    //work out S
                    float S;
                    if (curPlace < opponentPlace)
                        S = 1.0F;
                    else if (curPlace == opponentPlace)
                        S = 0.5F;
                    else
                        S = 0.0F;

                    float EA = 1 / (1.0f + (float)Math.pow(10.0f, (opponentELO - curELO) / 400.0f));

                    //calculate ELO change vs this one opponent, add it to our change bucket
                    players.get(i).eloChange += Math.round(K * (S - EA));
                }
            }
            //add accumulated change to initial ELO for final ELO   
            players.get(i).eloPost = players.get(i).eloPre + players.get(i).eloChange;
        }
    }

    @Override
    public String toString() {
        //Return a string with all ELOPlayer
        StringBuilder s = new StringBuilder();
        for (ELOPlayer p : players)
        {
            s.append(p.toString()).append("\n");
        }
        return s.toString();
    }
}