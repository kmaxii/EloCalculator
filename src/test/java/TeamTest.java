import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class TeamTest {


    @Test
    public void TestFindOtherPlayerInTeam(){
        var players = RandomTeamAssignment.randomPlayers(40);
        Team team = new Team(players.stream().limit(5).toList());
        assertEquals(team.size(), 5);

        assertNotNull(team.findOtherPlayerInTeam(players.get(0)));

        players.get(0).teamRequest = players.get(1);
        players.get(1).teamRequest = players.get(0);

        assertNotNull(team.findOtherPlayerInTeam(players.get(2)));

        players.get(2).teamRequest = players.get(3);
        players.get(3).teamRequest = players.get(2);

        assertNull(team.findOtherPlayerInTeam(players.get(4)));
    }
}
