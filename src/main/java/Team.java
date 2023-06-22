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
