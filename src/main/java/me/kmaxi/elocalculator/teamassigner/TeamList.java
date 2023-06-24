package me.kmaxi.elocalculator.teamassigner;

import me.kmaxi.elocalculator.ELOPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class TeamList implements Iterable<Team> {
    private final List<Team> teams;

     /**
     * Copy constructor
     * @param teamList The me.kmaxi.elocalculator.teamassigner.TeamList to copy
     */
    public TeamList(TeamList teamList) {
        this.teams = teamList.stream().map(Team::copy).toList();
    }


    public TeamList() {
        this.teams = new ArrayList<>();
    }


    public Team getTeam(int index){
        return teams.get(index);
    }

    //Add team
    public void addTeam(Team team){
        teams.add(team);
    }


    public void swapPlayerTeams(int team1, int team2, ELOPlayer player1, ELOPlayer player2){
        if (team1 == team2)
            return;

        teams.get(team1).removePlayer(player1);
        teams.get(team2).removePlayer(player2);
        teams.get(team1).AddPlayer(player2);
        teams.get(team2).AddPlayer(player1);
    }


    public int teamAmount(){
        return teams.size();
    }

    public Stream<Team> stream(){
        return teams.stream();
    }


    public int findPlayersTeam(ELOPlayer player) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).contains(player)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Player " + player + " not found in any team.");
    }



    /**
     * Swaps two random players in a random team.
     *
     * @return A new list of teams with the swapped players (not a reference)
     */
    public TeamList makeChild() {

        Random random = new Random();

        TeamList newTeams = new TeamList(this);


        int randomTeam1 = random.nextInt(newTeams.teamAmount());
        int randomTeam2 = random.nextInt(newTeams.teamAmount());

        ELOPlayer randomPlayer1 = newTeams.getTeam(randomTeam1).getRandomPlayer();
        ELOPlayer randomPlayer2 = newTeams.getTeam(randomTeam2).getRandomPlayer();

        newTeams.swapPlayerTeams(randomTeam1, randomTeam2, randomPlayer1, randomPlayer2);

        return newTeams;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (Team team : teams) {
            sb.append(team.toString()).append("\n");
        }
        return sb.toString();
    }


    public static boolean inTeamWithReq(Team team, ELOPlayer player){
        return team.contains(player.teamRequest);
    }

    public Iterator<Team> iterator() {
        return new TeamIterator();
    }

    private class TeamIterator implements Iterator<Team> {
        private int index = 0;

        public boolean hasNext() {
            return index < teams.size();
        }

        public Team next() {
            return teams.get(index++);
        }
    }

    public void printELOS(){
        for (Team team : teams) {
            List<Double> eLOS = team.stream().map(p -> p.elo * 1.0d).toList();
            System.out.println(eLOS + " " + TeamAssignment.mean(eLOS));
        }
    }

    public boolean compare(TeamList otherTeam) {
        if (teamAmount() != otherTeam.teamAmount()) {
            return false;
        }

        for (int i = 0; i < teams.size(); i++){
            if (!teams.get(i).compare(otherTeam.teams.get(i)))
                return false;
        }

        return true;
    }

}
