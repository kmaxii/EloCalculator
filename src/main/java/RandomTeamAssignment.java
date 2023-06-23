import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomTeamAssignment {

    public static TeamAssignment getRandomTeamAssignment(int nPlayers, int preferenceAmount) {
        List<ELOPlayer> players = randomPlayers(nPlayers);
        List<Pair<ELOPlayer, ELOPlayer>> preferences = randomPreferences(preferenceAmount, players);
        return new TeamAssignment(players, preferences);
    }

    public static List<ELOPlayer> randomPlayers(int nPlayers) {

        List<Integer> playersElo = List.of(
                1627, 2343, 1683, 980, 1285, 826, 1307, 1682, 1488, 1504, 1627, 1948, 1390, 1204,
                975, 1370, 1044, 1497, 1526, 1427, 1515, 1414, 1141, 764, 1844, 1451, 1864, 1379,
                2072, 1210, 1683, 1006, 1030, 872, 985, 1016, 1512, 1203, 1442, 1015
        );

        List<ELOPlayer> result = new ArrayList<>();
        for (int i = 0; i < nPlayers; i++) {
            result.add(new ELOPlayer(generateRandomString(), playersElo.get(i), null));
        }
        return result;
    }

    /**
     * Randomly sets team preferences for testing
     *
     * @param k The amount of pairs that have a team request
     * @return A list of pairs that want to team
     */
    public static List<Pair<ELOPlayer, ELOPlayer>> randomPreferences(int k, List<ELOPlayer> players) {

        List<ELOPlayer> playersCopy = new ArrayList<>(players);

        Collections.shuffle(playersCopy);
        List<Pair<ELOPlayer, ELOPlayer>> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            ELOPlayer player1 = playersCopy.get(0);
            ELOPlayer player2 = playersCopy.get(1);
            result.add(new Pair<>(player1, player2));
            playersCopy.remove(player1);
            playersCopy.remove(player2);
        }
        return result;
    }


    public static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private static int getRandomElo() {
        Random r = new Random();
        return r.nextInt((1000) + 1) + 1000;
    }

    private static ELOPlayer getRandomPlayer() {
        return new ELOPlayer(generateRandomString(), getRandomElo(), null);
    }

    public static TeamList getRandomTeamList(int teamAmount, int playersPerTeam){
        TeamList teamList = new TeamList();

        for (int i = 0; i < teamAmount; i++) {
            ArrayList<ELOPlayer> team = new ArrayList<>();
            for (int j = 0; j < playersPerTeam; j++) {
                team.add(RandomTeamAssignment.getRandomPlayer());
            }
            teamList.addTeam(new Team(team));
        }
        return teamList;
    }
}
