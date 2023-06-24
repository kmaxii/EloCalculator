import me.kmaxi.elocalculator.teamassigner.ChoicesFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChoicesFunctionTest {

    @Test
    public void ChoicesTest() {

        var fruits = Arrays.asList("Apple", "Banana", "Orange");
        var weights = new double[] {6.663176216410896d, 6.663176216410896d, 5.92097202766467d};

        ArrayList<Integer> choosen = new ArrayList<>();
        choosen.add(0);
        choosen.add(0);
        choosen.add(0);

        Random random = new Random();

        for (int i = 0; i < 1800; i++){
            var choice = ChoicesFunction.choices(fruits, weights, random);

            switch (choice){
                case "Apple":
                    choosen.set(0, choosen.get(0) + 1);
                    break;
                case "Banana":
                    choosen.set(1, choosen.get(1) + 1);
                    break;
                case "Orange":
                    choosen.set(2, choosen.get(2) + 1);
                    break;
            }
        }

        System.out.println(choosen);


    }
}
