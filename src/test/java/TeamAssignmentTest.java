import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TeamAssignmentTest {


    @Test
    public void testInitPool() {
        // Create an instance of the class containing the initPool method
        TeamAssignment teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(40, 7);

        // Call the initPool method
        List<TeamList> pool = teamAssignment.initPool();

        // Assert that the pool has the expected size
        assertEquals(TeamAssignment.poolSize, pool.size());

        // Assert that each element in the pool is not null
        for (TeamList teamList : pool) {
            assertNotNull(teamList);
        }
    }

    @Test
    public void testScoreFunction() {
        TeamAssignment teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(40, 0);

        TeamList balancedTeams = get1000EloTeamList();
        TeamList randomTeam = RandomTeamAssignment.getRandomTeamList(8, 5);

        double balancedTeamsScore = teamAssignment.scoreFunction(balancedTeams);
        double randomTeamsScore = teamAssignment.scoreFunction(randomTeam);

        System.out.println("Perfect balance: " + balancedTeamsScore);
        System.out.println("PRandom teams: " + randomTeamsScore);
        assertTrue(balancedTeamsScore > randomTeamsScore);


        teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(40, 7);
        TeamList withPreferences = teamAssignment.randomTeamsWithPreferences();
        TeamList noPreferences = teamAssignment.randomTeam();

        double withPreferencesScore = teamAssignment.scoreFunction(withPreferences);
        double noPreferencesScore = teamAssignment.scoreFunction(noPreferences);

        System.out.println("With preferences: " + withPreferencesScore);
        System.out.println("No preferences: " + noPreferencesScore);
        assertTrue(withPreferencesScore > noPreferencesScore);
    }

    @Test
    public void testRandomTeamsWithPreferences() {
        for (int i = 0; i < 10000; i++) {

            TeamAssignment teamAssignment = RandomTeamAssignment.getRandomTeamAssignment(40, 7);
            TeamList teamList = teamAssignment.randomTeamsWithPreferences();

            for (var teamReq : teamAssignment.preferences) {
                int player1Team = teamList.findPlayersTeam(teamReq.key());
                int player2Team = teamList.findPlayersTeam(teamReq.value());


                assertTrue(player1Team == player2Team);


            }

            //System.out.println(teamList);
        }


    }


    private static TeamList get1000EloTeamList() {
        TeamList team = new TeamList();
        for (int i = 0; i < 8; i++) {

            team.addTeam(new Team(Arrays.asList(
                    new ELOPlayer(RandomTeamAssignment.generateRandomString(), 2000, null),
                    new ELOPlayer(RandomTeamAssignment.generateRandomString(), 500, null),
                    new ELOPlayer(RandomTeamAssignment.generateRandomString(), 2000, null),
                    new ELOPlayer(RandomTeamAssignment.generateRandomString(), 500, null),
                    new ELOPlayer(RandomTeamAssignment.generateRandomString(), 1000, null))));
        }
        return team;
    }


}
