import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TeamAssignmentTest {



    @Test
    public void testInitPool() {
        System.out.println("TEST");

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




}
