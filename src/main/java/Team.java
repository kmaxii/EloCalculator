import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Team {

    private final HashSet<ELOPlayer> team;

    public Team(List<ELOPlayer> team) {
        this.team = new HashSet<>(team);
    }

    public boolean contains(ELOPlayer player) {
        return team.contains(player);
    }


    public void AddPlayer(ELOPlayer player) {

        if (team.size() == 5) {
            System.out.println("TEAM SIZE EXCEEDED 5");
        }

        team.add(player);
    }

    public Team() {
        this.team = new HashSet<>();
    }

    //Copy
    public Team copy() {
        Team team1 = new Team();
        for (var player : team) {
            team1.AddPlayer(player);
        }
        return team1;
    }

    /**
     * Looks for a player in the team that is not in a team with their REQ already.
     *
     * @param player The player which should not be selected
     * @return If there is no player in the team that is not with the req, return null. Otherwise, return the player that can be swapped
     */
    public ELOPlayer findOtherPlayerInTeam(ELOPlayer player) {
        for (var inTeam : team) {
            if (inTeam == player || team.contains(inTeam.teamRequest)) {
                continue;
            }

            return inTeam;
        }
        return null;
    }


    public void removePlayer(ELOPlayer player) {
        if (!team.remove(player)) {
            System.out.println("Player not in team");
        }

    }

    public Double getAverageElo() {
        return team.stream().mapToDouble(p -> p.elo).sum() / team.size();
    }

    public int size() {
        return team.size();
    }

    public ELOPlayer getRandomPlayer() {
        int size = team.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for (var obj : team) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    public ELOPlayer getRandomPlayer(ELOPlayer notThis) {

        HashSet<ELOPlayer> teamCopy = new HashSet<>(this.team);
        teamCopy.remove(notThis);

        int size = teamCopy.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for (var obj : teamCopy) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    public Stream<ELOPlayer> stream() {
        return team.stream();
    }


    @Override
    public String toString() {
        return team.toString();
    }


    public boolean compare(Team otherTeam) {
        if (team.size() != otherTeam.size()) {
            return false;
        }
        for (ELOPlayer i : team) {
            if (!otherTeam.contains(i)) {
                return false;
            }
        }
        return true;
    }


}
