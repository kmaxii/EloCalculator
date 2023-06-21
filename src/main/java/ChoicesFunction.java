import java.util.List;
import java.util.Random;

public class ChoicesFunction {
    public static <T> T choices(List<T> options, double[] weights, Random random) {
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        double choice = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        for (int i = 0; i < options.size(); i++) {
            cumulativeWeight += weights[i];
            if (choice < cumulativeWeight) {
                return options.get(i);
            }
        }

        // This point should not be reached unless there are no options or weights are invalid.
        throw new IllegalArgumentException("Invalid options or weights");
    }
}
