import org.junit.jupiter.api.Test;

public class TeamListTest {
    @Test
    void makeChildTest() {


        TeamList teamList = RandomTeamAssignment.getRandomTeamList(8, 5);

        var childList = teamList.makeChild();
    }
}
