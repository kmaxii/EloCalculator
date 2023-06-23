import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RandomTeamAssignmentTest {
    @Test
    public void testRandomPreferences() {

        int preferenceAmount = 7;
        int playerAmount = 40;

        // Create a sample list of preferences
        List<Pair<ELOPlayer, ELOPlayer>> preferences = RandomTeamAssignment.randomPreferences(preferenceAmount
                , RandomTeamAssignment.randomPlayers(playerAmount));

        // Assert that the list is not null
        assertNotNull(preferences);

        // Assert that the list has the expected size
        assertEquals(7, preferences.size());

        // Add more assertions as needed
        HashSet<ELOPlayer> playersWithPreferences = new HashSet<>();
        for (Pair<ELOPlayer, ELOPlayer> pair : preferences) {
            playersWithPreferences.add(pair.key());
            playersWithPreferences.add(pair.value());
        }
        assertEquals(preferenceAmount * 2, playersWithPreferences.size());
    }



}
