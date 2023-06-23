import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignment {

    private static final int nPlayers = 40;
    private static final int nTeams = 8;
    private static final int playersPerTeam = 5;
    public static final int poolSize = 100;
    private static final int nGenerations = 400;

    private List<ELOPlayer> players;
    private List<Pair<ELOPlayer, ELOPlayer>> preferences;

    public static void main(String[] args) {
        TeamAssignment teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(nPlayers, 7);
        teamAssignment.run();

    }

    public TeamAssignment(List<ELOPlayer> players, List<Pair<ELOPlayer, ELOPlayer>> preferences) {
        this.players = players;
        this.preferences = preferences;
    }

    public void run() {

        System.out.println("There are " + players.size() + " players");

        for (Pair<ELOPlayer, ELOPlayer> preference : preferences) {
            preference.key().teamRequest = preference.value();
            preference.value().teamRequest = preference.key();
        }

        System.out.println("PLAYERS:\n" + players + "\n");
        System.out.println("PREFERENCES:\n" + preferences + "\n");

        List<Pair<Integer, TeamList>> scoredPool = makeAssignments();
        scoredPool.sort(Collections.reverseOrder());

        System.out.println("FINAL SCORES: ");
        for (int i = 0; i < scoredPool.size(); i++) {
          //  System.out.println(i + ": " + scoredPool.get(i).key()());
            System.out.println(i + ": "  + scoreFunction(scoredPool.get(i).value(), preferences));
        }


        TeamList teams =  scoredPool.get(0).value();

        System.out.println("SCORE:" + scoreFunction(teams, preferences));

        System.out.println("PREFERENCES:");
        for (var preference : preferences){
            System.out.println(preference.key().elo + " " + preference.value().elo);
            if (teams.findPlayersTeam(preference.key()) == teams.findPlayersTeam(preference.value())){
                System.out.println("TRUE");
            }

        }


        System.out.println("TEAMS:");
        for (Team team : teams) {
            //System.out.print(team + " ");
            List<Double> eLOS = team.stream().map(p -> p.elo * 1.0d).toList();
            System.out.println(eLOS + " " + mean(eLOS));
        }
    }



    public List<Pair<Integer, TeamList>> makeAssignments() {
        int podium = 10;
        List<TeamList> pool = initPool();
        List<Pair<Integer, TeamList>> scoredPool = new ArrayList<>();

        for (TeamList teams : pool) {
            scoredPool.add(new Pair<>(scoreFunction(teams, preferences), teams));
        }
        Collections.sort(scoredPool, Collections.reverseOrder());

        Random random = new Random();

        for (int i = 0; i < nGenerations; i++) {

            List<TeamList> children1 = new ArrayList<>();
            List<TeamList> children2 = new ArrayList<>();

            for (int j = 0; j < scoredPool.size() - podium; j++) {
                children1.add(scoredPool.get(j).value().makeChild());
                children2.add(scoredPool.get(j).value().makeChild());
            }

            List<TeamList> newPool = new ArrayList<>();

            for (int j = 0; j < scoredPool.size() - podium; j++) {
                double s = scoredPool.get(j).key();
                System.out.println(j);
                TeamList p = scoredPool.get(j).value();
                TeamList c1 = children1.get(j);
                TeamList c2 = children2.get(j);

                double expS = Math.exp(s);
                double expScoreC1 = Math.exp(scoreFunction(c1, preferences));
                double expScoreC2 = Math.exp(scoreFunction(c2, preferences));
                double[] weights = {expS, expScoreC1, expScoreC2};

                TeamList chosenTeam = ChoicesFunction.choices(Arrays.asList(p, c1, c2), weights, random);

                System.out.println("Parent: " + expS);
                System.out.print(", Child 1: " + expScoreC1);
                System.out.print(", Child 2: " + expScoreC2);
                System.out.println(", Chosen: " + Math.exp(scoreFunction(chosenTeam, preferences)));

                newPool.add(chosenTeam);
            }

            for (int j = 0; j < podium; j++) {
                newPool.add(scoredPool.get(j).value());
            }

            scoredPool = new ArrayList<>();

            for (TeamList teams : newPool) {
                scoredPool.add(new Pair<>(scoreFunction(teams, preferences), teams));
            }
            scoredPool = scoredPool.stream()
                    .sorted(Comparator.comparing(e -> e.key()))
                    .sorted(Collections.reverseOrder()).collect(Collectors.toList());

            System.out.println("-------------------");
            System.out.println();

            for (int j = 0; j< scoredPool.size(); j++){
                System.out.print(scoredPool.get(j).key() + ", ");
            }

            System.out.println("-------------------");

        }

        return scoredPool;
    }

    /**
     * Creates a pool of random team assignments. Elo is not cared about here
     *
     * @return The pool of random team assignments
     */
    public List<TeamList> initPool() {
        List<TeamList> pool = new ArrayList<>();
        for (int i = 0; i < TeamAssignment.poolSize; i++) {
            pool.add(randomTeamsWithPreferences());
        }
        return pool;
    }


    private TeamList randomTeamsWithPreferences() {

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
            if (player1Team == player2Team){
                continue;
            }

            ELOPlayer replacePlayerTeam1 = teams.getTeam(player1Team).findOtherPlayerInTeam(player1);
            if (replacePlayerTeam1 != null) {
                teams.swapPlayerTeams(player1Team, player2Team, replacePlayerTeam1, player2);
            } else {
                ELOPlayer replacePlayerTeam2 = teams.getTeam(player2Team).findOtherPlayerInTeam(player2);
                if (replacePlayerTeam2 != null) {
                    teams.swapPlayerTeams(player1Team, player2Team, player1, replacePlayerTeam2);

                }
            }
        }

        return teams;
    }


    /**
     * Determines how good the team assignment is
     * @param teams       The teams
     * @param preferences The team request preferences
     * @return A score between 0 and 100, where 100 is perfect
     */
    private static int scoreFunction(TeamList teams, List<Pair<ELOPlayer, ELOPlayer>> preferences) {
        int satisfiedPreferences = 0;
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            ELOPlayer player1 = pair.key();
            if (TeamList.inTeamWithReq(teams.getTeam(teams.findPlayersTeam(player1)), player1)) {
                satisfiedPreferences++;
            }
        }

        //% of preferences filled
        double prefScore = 100.0 * (satisfiedPreferences / (double) preferences.size());
        List<Double> combinedElos = teams.stream()
                .map(Team::getAverageElo)
                .collect(Collectors.toList());
        double eloScore = (500000.0 - pVariance(combinedElos)) / 5000.0;

        int totalScore = (int) (prefScore + eloScore);

        return totalScore;
    }




    /**
     * Variance is a statistical measure that quantifies the spread or dispersion of a set of data points.
     * It provides an indication of how much the individual data points deviate from the mean (average) of the data set.
     * @param elos The elos to calculate the variance from
     * @return the variance of the elos
     */
    public static double pVariance(List<Double> elos) {
        double mean = mean(elos);
        double sum = 0.0;
        for (double elo : elos) {
            sum += Math.pow(elo - mean, 2);;
        }
        return sum / elos.size();
    }

    private static double mean(List<Double> elos) {
        double sum = 0.0;
        for (double elo : elos) {
            sum += elo;
        }
        return sum / elos.size() * 1.0f;
    }


}
