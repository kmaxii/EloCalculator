package me.kmaxi.elocalculator.teamassigner;

import me.kmaxi.elocalculator.ELOPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignment {

    private static final int nPlayers = 40;
    private static final int nTeams = 8;
    private static final int playersPerTeam = 5;
    public static final int poolSize = 100;
    private static final int nGenerations = 400;

    private final List<ELOPlayer> players;
    public List<Pair<ELOPlayer, ELOPlayer>> preferences;

    public static void main(String[] args) {
        TeamAssignment teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(nPlayers, 7);
        TeamList teams = teamAssignment.calculateTeams();

        System.out.println("TEAMS:");
        teams.printELOS();

    }

    public TeamAssignment(List<ELOPlayer> players, List<Pair<ELOPlayer, ELOPlayer>> preferences) {
        this.players = players;
        this.preferences = preferences;

        for (Pair<ELOPlayer, ELOPlayer> preference : preferences) {
            preference.key().teamRequest = preference.value();
            preference.value().teamRequest = preference.key();
        }
    }

    public TeamList calculateTeams() {

        System.out.println("There are " + players.size() + " players");

        System.out.println("PLAYERS:\n" + players + "\n");
        System.out.println("PREFERENCES:\n" + preferences + "\n");

        List<Pair<Integer, TeamList>> scoredPool = makeAssignments();
        scoredPool.sort(Collections.reverseOrder());

        TeamList teams = scoredPool.get(0).value();
        System.out.println("SCORE:" + scoreFunction(teams));

        System.out.println("PREFERENCES:");
        printPreferences(teams);

        return teams;
    }


    private void printPreferences(TeamList teams) {
        for (var preference : preferences) {
            System.out.println(preference.key().elo + " " + preference.value().elo);
            if (teams.findPlayersTeam(preference.key()) == teams.findPlayersTeam(preference.value())) {
                System.out.println("TRUE");
            }
        }
    }


    public List<Pair<Integer, TeamList>> makeAssignments() {
        int podium = 10;
        List<TeamList> pool = initPool();
        List<Pair<Integer, TeamList>> scoredPool = new ArrayList<>();

        for (TeamList teams : pool) {
            scoredPool.add(new Pair<>(scoreFunction(teams), teams));
        }
        scoredPool.sort(Collections.reverseOrder());

        Random random = new Random();

        for (int i = 0; i < nGenerations; i++) {

            List<TeamList> newPool = new ArrayList<>();

            for (int j = 0; j < scoredPool.size() - podium; j++) {
                double parentScore = scoredPool.get(j).key();

                TeamList parent = scoredPool.get(j).value();
                TeamList child1 = scoredPool.get(j).value().makeChild();
                TeamList child2 = scoredPool.get(j).value().makeChild();

                double expScoreParent = Math.exp(parentScore);
                double expScoreChild1 = Math.exp(scoreFunction(child1));
                double expScoreChild2 = Math.exp(scoreFunction(child2));
                double[] weights = {expScoreParent, expScoreChild1, expScoreChild2};

                TeamList chosenTeam = ChoicesFunction.choices(Arrays.asList(parent, child1, child2), weights, random);

                newPool.add(chosenTeam);
            }

            for (int j = 0; j < podium; j++) {
                newPool.add(scoredPool.get(j).value());
            }

            scoredPool = new ArrayList<>();

            for (TeamList teams : newPool) {
                scoredPool.add(new Pair<>(scoreFunction(teams), teams));
            }
            scoredPool = scoredPool.stream()
                    .sorted(Comparator.comparing(Pair::key))
                    .sorted(Collections.reverseOrder()).collect(Collectors.toList());


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


    public TeamList randomTeamsWithPreferences() {

        TeamList teams = randomTeam();

        Collections.shuffle(preferences);

        //Assigns all team requests that are possible to make.
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            moveToTeamRequest(teams, pair);
        }

        return teams;
    }

    private void moveToTeamRequest(TeamList teams, Pair<ELOPlayer, ELOPlayer> pair) {

        ELOPlayer player1 = pair.key();
        ELOPlayer player2 = pair.value();

        int player1Team = teams.findPlayersTeam(player1);
        int player2Team = teams.findPlayersTeam(player2);

        //If the team request is already in a team, skip it.
        if (player1Team == player2Team) {
            return;
        }

        ELOPlayer replacePlayerTeam1 = teams.getTeam(player1Team).findOtherPlayerInTeam(player1);
        if (replacePlayerTeam1 != null) {
            teams.swapPlayerTeams(player1Team, player2Team, replacePlayerTeam1, player2);
            return;
        }

        ELOPlayer replacePlayerTeam2 = teams.getTeam(player2Team).findOtherPlayerInTeam(player2);
        if (replacePlayerTeam2 != null) {
            teams.swapPlayerTeams(player1Team, player2Team, player1, replacePlayerTeam2);
            return;
        }


        //At this point we can't move these players into the same team in one of the requested teams. We instead need to swap both
        for (int i = 0; i < nTeams; i++) {
            ELOPlayer replacePlayer1 = teams.getTeam(i).findOtherPlayerInTeam(null);
            ELOPlayer replacePlayer2 = teams.getTeam(i).findOtherPlayerInTeam(replacePlayer1);

            if (replacePlayer1 != null && replacePlayer2 != null) {
                teams.swapPlayerTeams(i, player1Team, replacePlayer1, player1);
                teams.swapPlayerTeams(i, player2Team, replacePlayer2, player2);
                return;
            }

        }

        System.out.println("No swap possible");
    }

    public TeamList randomTeam() {

        Collections.shuffle(players);


        //Throws all players into random teams
        TeamList teams = new TeamList();
        for (int teamNumber = 0; teamNumber < nTeams; teamNumber++) {
            List<ELOPlayer> teamPlayers = new ArrayList<>(players.subList(playersPerTeam * teamNumber, playersPerTeam * teamNumber + playersPerTeam));
            Team team = new Team(teamPlayers);
            teams.addTeam(team);
        }
        return teams;
    }


    /**
     * Determines how good the team assignment is
     *
     * @param teams The teams
     * @return A score between 0 and 100, where 100 is perfect
     */
    public int scoreFunction(TeamList teams) {
        int satisfiedPreferences = 0;
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            ELOPlayer player1 = pair.key();
            if (TeamList.inTeamWithReq(teams.getTeam(teams.findPlayersTeam(player1)), player1)) {
                satisfiedPreferences++;
            }
        }

        //% of preferences filled
        double prefScore = preferences.size() > 0 ? 100.0 * (satisfiedPreferences / (double) preferences.size()) : 100.0;
        List<Double> combinedElos = teams.stream()
                .map(Team::getAverageElo)
                .collect(Collectors.toList());
        double eloScore = (500000.0 - pVariance(combinedElos)) / 5000.0;

        return (int) (prefScore + eloScore);
    }


    /**
     * Variance is a statistical measure that quantifies the spread or dispersion of a set of data points.
     * It provides an indication of how much the individual data points deviate from the mean (average) of the data set.
     *
     * @param elos The elos to calculate the variance from
     * @return the variance of the elos
     */
    public static double pVariance(List<Double> elos) {
        double mean = mean(elos);
        double sum = 0.0;
        for (double elo : elos) {
            sum += Math.pow(elo - mean, 2);
        }
        return sum / elos.size();
    }

    public static double mean(List<Double> elos) {
        double sum = 0.0;
        for (double elo : elos) {
            sum += elo;
        }
        return sum / elos.size() * 1.0f;
    }


}
