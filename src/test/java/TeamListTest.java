import me.kmaxi.elocalculator.teamassigner.RandomTeamAssignment;
import me.kmaxi.elocalculator.teamassigner.TeamList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TeamListTest {
    @Test
    void makeChildTest() {


        TeamList teamList = RandomTeamAssignment.getRandomTeamList(8, 5);

        var child1 = teamList.makeChild();

        var child2 = teamList.makeChild();

                System.out.println(teamList);

        System.out.println("Child1 ");
        System.out.println(child1);

        System.out.println("Child2");
        System.out.println(child2);

        //The teams are not the same
        Assertions.assertFalse(teamList.compare(child1) && teamList.compare(child2) && child1.compare(child2));



    }
}
