package delft;

/**
 * Delft Canal Monitoring System
 *
 * Processes water-level sensor readings from Delft's canal network
 * and returns a status code for the segment.
 */
class CanalMonitor {

    /**
     * Classifies a day's worth of canal sensor readings.
     *
     * @param readings      water levels in cm, one per sensor interval (1–24 values)
     * @param warningLevel  segment-specific threshold in cm; readings strictly above
     *                      this are considered "high" (must be >= 1)
     * @param isFloodSeason true during October–March, applies stricter thresholds
     * @return 0 = normal, 1 = elevated, 2 = high risk, 3 = critical
     * @throws IllegalArgumentException if any argument is invalid
     */
    public static int classifyCanalReadings(int[] readings,
                                            int warningLevel,
                                            boolean isFloodSeason) {
        if (readings == null)
            throw new IllegalArgumentException("readings must not be null");
        if (readings.length < 1 || readings.length > 24)
            throw new IllegalArgumentException(
                    "readings must contain between 1 and 24 elements, got " + readings.length);

        if (warningLevel < 1)
            throw new IllegalArgumentException(
                    "warningLevel must be >= 1, got " + warningLevel);


        int n = readings.length;
        int h = 0;

        for (int i = 0; i < readings.length; i++) {
            if (readings[i] < 0)
                throw new IllegalArgumentException(
                        "readings[" + i + "] must be >= 0, got " + readings[i]);
            if (readings[i] > warningLevel + 50)
                return 3;
            if (readings[i] > warningLevel)
                h++;
        }

        double r = (double) h / n;

        if ((isFloodSeason && r > 0.5) || (!isFloodSeason && r > 0.75)) {
            return 2;
        }

        return r > 0 ? 1 : 0;
    }
}
