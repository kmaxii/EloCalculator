import java.util.List;
import java.util.stream.Stream;

public class Team {

    private final List<ELOPlayer> team;

    public Team(List<ELOPlayer> team) {
        this.team = team;
    }

    public boolean contains(ELOPlayer player) {
        return team.contains(player);
    }


    public void AddPlayer(ELOPlayer player) {

        if (team.size() == 5) {
            System.out.println("ERROR");
        }

        team.add(player);
    }

    /**
     * Looks for a player in the team that is not in a team with their REQ already.
     *
     * @param player The player which should not be selected
     * @return If there is no player in the team that is not with the req, return null. Otherwise, return the player that can be swapped
     */
    public ELOPlayer findOtherPlayerInTeam(ELOPlayer player) {
        for (int i = 0; i < team.size(); i++) {
            if (team.get(i) == player || team.contains(team.get(i).teamRequest)) {
                continue;
            }

            return team.get(i);
        }
        return null;
    }



    public void removePlayer(ELOPlayer player) {
        team.remove(player);

    }

    public Double getAverageElo() {
        return team.stream().mapToDouble(p -> p.elo).sum() / team.size();
    }

    public int size() {
        return team.size();
    }

    public ELOPlayer getPlayer(int index) {
        return team.get(index);
    }

    public Stream<ELOPlayer> stream() {
        return team.stream();
    }


    @Override
    public String toString() {
        return team.toString();
    }



}
