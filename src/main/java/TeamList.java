import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class TeamList implements Iterable<Team> {
    private final List<Team> teams;

     /**
     * Copy constructor
     * @param teamList The TeamList to copy
     */
    public TeamList(TeamList teamList) {
        this.teams = new ArrayList<>(teamList.teams);
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

    //Remove team
    public void removeTeam(Team team){
        teams.remove(team);
    }

    public void swapPlayerTeams(int team1, int team2, ELOPlayer player1, ELOPlayer player2){
        if (team1 == team2)
            return;

        teams.get(team1).removePlayer(player1);
        teams.get(team2).removePlayer(player2);
        teams.get(team1).AddPlayer(player2);
        teams.get(team2).AddPlayer(player1);
    }

    public void swapPlayerTeams(int team1, int team2, int player1Index, int player2Index){
        ELOPlayer player1 = teams.get(team1).getPlayer(player1Index);
        ELOPlayer player2 = teams.get(team2).getPlayer(player2Index);
        swapPlayerTeams(team1, team2, player1, player2);
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

        int playersPerTeam = teams.get(0).size();

        Random random = new Random();
        int randomTeam1 = random.nextInt(teams.size());
        int randomTeam2 = random.nextInt(teams.size());
        int randomPlayer1Index = random.nextInt(playersPerTeam);
        int randomPlayer2Index = random.nextInt(playersPerTeam);

        TeamList newTeams = new TeamList(this);

        newTeams.swapPlayerTeams(randomTeam1, randomTeam2, randomPlayer1Index, randomPlayer2Index);

        return newTeams;
    }

    @Override
    public String toString() {
        return "TeamList{" +
                "teams=" + teams +
                '}';
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

}
