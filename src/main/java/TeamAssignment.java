import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamAssignment {

    private static final int nPlayers = 40;
    private static final int nTeams = 8;
    private static final int playersPerTeam = 5;

    List<ELOPlayer> players;

    public static void main(String[] args) {

        TeamAssignment teamAssignment = new TeamAssignment();
        teamAssignment.run();

    }

    public void run() {

        players = randomPlayers();
        System.out.println("There are " + players.size() + " players");

        List<Pair<ELOPlayer, ELOPlayer>> preferences = randomPreferences(7);



        System.out.println("PLAYERS:\n" + players + "\n");
        System.out.println("PREFERENCES:\n" + preferences + "\n");

        List<Pair<Integer, TeamList>> scoredPool = makeAssignments(preferences, 400);
        TeamList teams =  scoredPool.get(0).value();

        Collections.sort(scoredPool, Collections.reverseOrder());

        System.out.println("TEAMS:");
        for (Team team : teams) {
            System.out.print(team + " ");
            List<Double> eLOS = team.stream().map(p -> p.elo * 1.0d).toList();
            System.out.println(eLOS + " " + mean(eLOS));
        }
        System.out.println();
    }


    private List<Pair<Integer, TeamList>> makeAssignments(List<Pair<ELOPlayer, ELOPlayer>> preferences, int nGenerations) {
        final int podium = 10;
        var pool = initPool(100, preferences);

        //Gets how good all team assignments are
        List<Pair<Integer, TeamList>> scoredPool = pool.stream()
                .map(teams -> new Pair<>(scoreFunction(teams, preferences), teams))
                .sorted(Comparator.comparing(e -> e.key))
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());



        for (int i = 0; i < nGenerations; i++) {
            var children1 = scoredPool.stream()
                    .limit(scoredPool.size() - podium)
                    .map(o -> makeChild(o.value))
                    .toList();

            var children2 = scoredPool.stream()
                    .limit(scoredPool.size() - podium)
                    .map(o -> makeChild(o.value))
                    .toList();

            Random random = new Random();
            List<Pair<Integer, TeamList>> finalScoredPool = scoredPool;
            List<TeamList> newPool = IntStream.range(0, scoredPool.size() - podium)
                    .mapToObj(j -> {
                        int parentScore = finalScoredPool.get(j).key();
                        TeamList parent = finalScoredPool.get(j).value();
                        TeamList child1 = children1.get(j);
                        TeamList child2 = children2.get(j);
                        double w0 = Math.exp(parentScore);
                        double w1 = Math.exp(scoreFunction(child1, preferences));
                        double w2 = Math.exp(scoreFunction(child2, preferences));
                        double[] weights = {w0, w1, w2};
                        return ChoicesFunction.choices(Arrays.asList(parent, child1, child2), weights, random);
                    })
                    .toList();


            List<Pair<Integer, TeamList>> top10TeamsBefore = IntStream.range(0, podium)
                    .mapToObj(finalScoredPool::get).toList();

            scoredPool = newPool.stream()
                    .map(teams -> new Pair<>(scoreFunction(teams, preferences), teams))
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());

            scoredPool.addAll(top10TeamsBefore);

        }

        return scoredPool;
    }

    /**
     * Creates a pool of random team assignments. Elo is not cared about here
     *
     * @param poolSize    The amount of random team assignments
     * @param preferences The team preferences, these will be meet when possible.
     * @return The pool of random team assignments
     */
    private List<TeamList> initPool(int poolSize, List<Pair<ELOPlayer, ELOPlayer>> preferences) {
        List<TeamList> pool = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            pool.add(randomTeamsWithPreferences(preferences));
        }
        return pool;
    }

    private TeamList randomTeamsWithPreferences(List<Pair<ELOPlayer, ELOPlayer>> preferences) {

        Collections.shuffle(players);
        Collections.shuffle(preferences);

        //Throws all players into random teams
        TeamList teams = new TeamList();
        for (int teamNumber = 0; teamNumber < nTeams; teamNumber++) {
            List<ELOPlayer> teamPlayers = new ArrayList<>(players.subList(playersPerTeam * teamNumber, playersPerTeam * teamNumber + playersPerTeam));
            Team team = new Team(teamPlayers);
            teams.addTeam(team);
        }

        //Assigns all team requests that are possible to make.
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            ELOPlayer player1 = pair.key();
            ELOPlayer player2 = pair.value();
            int player1Team = teams.findPlayersTeam(player1);
            int player2Team = teams.findPlayersTeam(player2);

            //If the team request is already in a team, skip it.
            if (player1Team == player2Team)
                continue;

            ELOPlayer replacePlayerTeam1 = findOtherPlayerInTeam(teams.getTeam(player1Team), player1);
            if (replacePlayerTeam1 != null) {
                teams.swapPlayerTeams(player1Team, player2Team, player1, player2);
            } else {
                ELOPlayer replacePlayerTeam2 = findOtherPlayerInTeam(teams.getTeam(player2Team), player2);
                if (replacePlayerTeam2 != null) {
                    teams.swapPlayerTeams(player1Team, player2Team, player1, player2);
                }
            }
        }

        return teams;
    }


    /**
     * Looks for a player in the team that is not in a team with their REQ already.
     *
     * @param team The team to look for a player
     * @param player The player which should not be selected
     * @return If there is no player in the team that is not with the req, return null. Otherwise, return the player that can be swapped
     */
    private static ELOPlayer findOtherPlayerInTeam(Team team, ELOPlayer player) {
        for (int i = 0; i < team.size(); i++) {
            if (team.getPlayer(i) == player || TeamList.inTeamWithReq(team, player)) {
                continue;
            }

            return team.getPlayer(i);
        }
        return null;
    }

    /**
     * Randomly sets team preferences for testing
     *
     * @param k The amount of pairs that have a team request
     * @return A list of pairs that want to team
     */
    private List<Pair<ELOPlayer, ELOPlayer>> randomPreferences(int k) {

        List<ELOPlayer> playersCopy = new ArrayList<>(players);

        Collections.shuffle(playersCopy);
        List<Pair<ELOPlayer, ELOPlayer>> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            ELOPlayer player1 = playersCopy.get(0);
            ELOPlayer player2 = playersCopy.get(1);
            result.add(new Pair<>(player1, player2));
            playersCopy.remove(0);
            playersCopy.remove(1);
        }
        return result;
    }

    private static List<ELOPlayer> randomPlayers() {

        List<Integer> playersElo = List.of(
                1627, 2343, 1683, 980, 1285, 826, 1307, 1682, 1488, 1504, 1627, 1948, 1390, 1204,
                975, 1370, 1044, 1497, 1526, 1427, 1515, 1414, 1141, 764, 1844, 1451, 1864, 1379,
                2072, 1210, 1683, 1006, 1030, 872, 985, 1016, 1512, 1203, 1442, 1015
        );

        List<ELOPlayer> result = new ArrayList<>();
        for (int i = 0; i < TeamAssignment.nPlayers; i++) {
            result.add(new ELOPlayer(generateRandomString(), playersElo.get(i), null));
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


    /**
     * Determins how good the team assignment is
     *
     * @param teams       The teams
     * @param preferences The team request preferences
     * @return A score between 0 and 100, where 100 is perfect
     */
    private static int scoreFunction(TeamList teams, List<Pair<ELOPlayer, ELOPlayer>> preferences) {
        int satisfiedPreferences = 0;
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            ELOPlayer player1 = pair.key;
            if (TeamList.inTeamWithReq(teams.getTeam(teams.findPlayersTeam(player1)), player1)) {
                satisfiedPreferences++;
            }
        }
        //% of preferences filled
        double prefScore = 100.0 * (satisfiedPreferences / (double) preferences.size());
        List<Double> combinedElos = teams.stream()
                .map(Team::getAverageElo)
                .collect(Collectors.toList());
        double eloScore = (500000.0 - pvariance(combinedElos)) / 5000.0;
        return (int) (prefScore + eloScore);
    }


    /**
     * Swaps two random players in a random team.
     *
     * @param teams The teams
     * @return A new list of teams with the swapped players (not a reference)
     */
    private static TeamList makeChild(TeamList teams) {

        Random random = new Random();
        int randomTeam1 = random.nextInt(teams.teamAmount());
        int randomTeam2 = random.nextInt(teams.teamAmount());
        int randomPlayer1Index = random.nextInt(playersPerTeam);
        int randomPlayer2Index = random.nextInt(playersPerTeam);

        TeamList newTeams = new TeamList(teams);

        newTeams.swapPlayerTeams(randomTeam1, randomTeam2, randomPlayer1Index, randomPlayer2Index);

        return newTeams;
    }


    private static double pvariance(List<Double> elos) {
        double mean = mean(elos);
        double sum = 0.0;
        for (double elo : elos) {
            double diff = elo - mean;
            sum += diff * diff;
        }
        return sum / elos.size();
    }

    private static double mean(List<Double> elos) {
        double sum = 0.0;
        for (double elo : elos) {
            sum += elo;
        }
        return sum / elos.size();
    }

    private record Pair<K, V>(K key, V value) implements Comparable<Pair<K, V>> {

        @Override
            public int compareTo(Pair<K, V> other) {
                if (key instanceof Comparable) {
                    Comparable<K> comparableKey = (Comparable<K>) key;
                    return comparableKey.compareTo(other.key());
                }
                throw new UnsupportedOperationException("Key does not implement Comparable.");
            }

            @Override
            public String toString() {
                return "(" + key + ", " + value + ")";
            }
        }
}
