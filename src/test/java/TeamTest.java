import me.kmaxi.elocalculator.ELOPlayer;
import me.kmaxi.elocalculator.teamassigner.RandomTeamAssignment;
import me.kmaxi.elocalculator.teamassigner.Team;
import me.kmaxi.elocalculator.teamassigner.TeamList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TeamTest {


    @Test
    public void TestFindOtherPlayerInTeam() {
        var players = RandomTeamAssignment.randomPlayers(40);
        Team team = new Team(players.stream().limit(5).toList());
        Assertions.assertEquals(team.size(), 5);

        Assertions.assertNotNull(team.findOtherPlayerInTeam(players.get(0)));

        players.get(0).teamRequest = players.get(1);
        players.get(1).teamRequest = players.get(0);

        Assertions.assertNotNull(team.findOtherPlayerInTeam(players.get(2)));

        players.get(2).teamRequest = players.get(3);
        players.get(3).teamRequest = players.get(2);

        Assertions.assertNull(team.findOtherPlayerInTeam(players.get(4)));
    }

    @Test
    public void SwapTeamTest() {
        Team team1 = new Team(RandomTeamAssignment.randomPlayers(5));
        Team team2 = new Team(RandomTeamAssignment.randomPlayers(5));

        TeamList teamList = new TeamList();
        teamList.addTeam(team1);
        teamList.addTeam(team2);

        ELOPlayer player1 = team1.getRandomPlayer();
        ELOPlayer player2 = team2.getRandomPlayer();

        teamList.swapPlayerTeams(0, 1, player1, player2);
        Assertions.assertTrue(team1.contains(player2));
        Assertions.assertTrue(!team1.contains(player1));
        Assertions.assertTrue(team2.contains(player1));
        Assertions.assertTrue(!team2.contains(player2));

        //Double swap test
        ELOPlayer test2player1 = team1.getRandomPlayer();
        ELOPlayer test2player2 = team1.getRandomPlayer(test2player1);
        ELOPlayer test2player3 = team2.getRandomPlayer();
        ELOPlayer test2player4 = team2.getRandomPlayer(test2player3);

        Assertions.assertTrue(team1.contains(test2player1));
        Assertions.assertTrue(!team2.contains(test2player1));
        Assertions.assertTrue(team1.contains(test2player2));
        Assertions.assertTrue(!team2.contains(test2player2));
        Assertions.assertTrue(team2.contains(test2player3));
        Assertions.assertTrue(!team1.contains(test2player3));
        Assertions.assertTrue(team2.contains(test2player4));
        Assertions.assertTrue(!team1.contains(test2player4));

        teamList.swapPlayerTeams(0, 1, test2player1, test2player3);
        teamList.swapPlayerTeams(0, 1, test2player2, test2player4);

        Assertions.assertTrue(!team1.contains(test2player1));
        Assertions.assertTrue(team2.contains(test2player1));
        Assertions.assertTrue(!team1.contains(test2player2));
        Assertions.assertTrue(team2.contains(test2player2));
        Assertions.assertTrue(!team2.contains(test2player3));
        Assertions.assertTrue(team1.contains(test2player3));
        Assertions.assertTrue(!team2.contains(test2player4));
        Assertions.assertTrue(team1.contains(test2player4));


        System.out.println("me.kmaxi.elocalculator.teamassigner.Team 1 size: " + team1.size());
        System.out.println("me.kmaxi.elocalculator.teamassigner.Team 2 size: " + team2.size());

        System.out.println(team1);
        System.out.println(team2);
        Assertions.assertEquals(team1.size(), 5);
        Assertions.assertEquals(team2.size(), 5);

    }
}
