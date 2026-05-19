package delft;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for CanalMonitor.classifyCanalReadings()
 */
class CanalMonitorTest {

    // Exception / invalid input tests

    @Test
    // null readings throws
    void nullReadings() {
        assertThrows(IllegalArgumentException.class,
                () -> CanalMonitor.classifyCanalReadings(null, 100, false))
    }

    @Test
    // empty array (length 0) throws
    void emptyArray() {
        assertThrows(IllegalArgumentException.class,
                () -> CanalMonitor.classifyCanalReadings(new int[]{}, 100, false));
    }

    @Test
    // array too long (length 25) throws
    void arrayTooLong() {
        int[] r = new int[25];
        assertThrows(IllegalArgumentException.class,
                () -> CanalMonitor.classifyCanalReadings(r, 100, false));
    }

    @Test
    // warningLevel 0 throws
    void warningLevelZero() {
        assertThrows(IllegalArgumentException.class,
                () -> CanalMonitor.classifyCanalReadings(new int[]{50}, 0, false));
    }

    @Test
    // negative element in readings throws
    void negativeReading() {
        assertThrows(IllegalArgumentException.class,
                () -> CanalMonitor.classifyCanalReadings(new int[]{50, -1, 80}, 100, false));
    }

//    @Test
//    // all readings at or below warningLevel - 0
//    void allReadingsNormal() {
//        int[] readings = {80, 90, 99};
//        assertEquals(0, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    @Test
//    // minority of readings high, non-flood season - 1
//    void minorityHighNonFlood() {
//        int[] readings = {102, 80, 80, 80};
//        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    @Test
//    // majority of readings high, non-flood season - 2
//    void majorityHighNonFlood() {
//        int[] readings = {102, 103, 104, 80};
//        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    @Test
//    // one reading exceeds warningLevel + 50 - 3
//    void criticalReading() {
//        int[] readings = {80, 90, 152};
//        assertEquals(3, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }

    // Partition + boundary value tests

    @Test
    // minimum valid length (1 element)
    void arrayLengthOne() {
        assertEquals(0, CanalMonitor.classifyCanalReadings(new int[]{50}, 100, false));
    }

    @Test
    // maximum valid length (24 elements)
    void arrayLengthTwentyFour() {
        int[] readings = new int[24]; // all zeros
        assertEquals(0, CanalMonitor.classifyCanalReadings(readings, 100, false));
    }

    @Test
    // warningLevel minimum valid (1)
    void warningLevelOne() {
        assertEquals(0, CanalMonitor.classifyCanalReadings(new int[]{0}, 1, false));
    }

    @Test
    // reading exactly at warningLevel
    void readingAtWarningLevel() {
        assertEquals(0, CanalMonitor.classifyCanalReadings(new int[]{100}, 100, false));
    }

    @Test
    // reading one above warningLevel
    void readingOneAboveWarningLevel() {
        assertEquals(2, CanalMonitor.classifyCanalReadings(new int[]{101}, 100, false));
    }

    @Test
    // reading exactly at warningLevel + 50
    void readingAtCriticalThreshold() {
        assertEquals(2, CanalMonitor.classifyCanalReadings(new int[]{150}, 100, false));
    }

    @Test
    // reading one above warningLevel + 50
    void readingOneAboveCriticalThreshold() {
        assertEquals(3, CanalMonitor.classifyCanalReadings(new int[]{151}, 100, false));
    }

    @Test
    // r exactly 0.5, flood season
    void ratioExactlyHalfFloodSeason() {
        int[] readings = {101, 80};
        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, true));
    }

    @Test
    // r just above 0.5, flood season
    void ratioJustAboveHalfFloodSeason() {
        int[] readings = {101, 102, 80};
        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, true));
    }

    @Test
    // r exactly 0.75, non-flood season
    void ratioExactly75NonFlood() {
        int[] readings = {101, 102, 103, 80};
        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, false));
    }

    @Test
    // r just above 0.75, non-flood season
    void ratioJustAbove75NonFlood() {
        int[] readings = {101, 102, 103, 104};
        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, false));
    }

    @Test
    // r = 0 (zero high readings)
    void ratioZero() {
        assertEquals(0, CanalMonitor.classifyCanalReadings(new int[]{100, 50, 0}, 100, false));
    }

    @Test
    // r just above 0 (one high reading)
    void ratioJustAboveZero() {
        assertEquals(1, CanalMonitor.classifyCanalReadings(new int[]{101, 50, 50}, 100, false));
    }

    // MC/DC tests
    //
    // Conditions:
    //   C1: reading > warningLevel + 50     (inside loop, R1 — early exit)
    //   C2: isFloodSeason                   (controls which r-threshold applies)
    //   C3: r > 0.5                         (high-risk check when flood season)
    //   C4: r > 0.75                        (high-risk check when not flood season)
    //   C5: r > 0                           (at least one high reading → R4)
    //
    // For MC/DC each condition must independently determine the outcome
    // in at least one test pair.

    // --- C1: critical reading (R1 early exit) ---
    // C1=true  → 3  (all other conditions irrelevant)
    // C1=false → something else (must differ only in C1)

//    @Test
//    // MC/DC C1=true: single critical reading → 3
//    void c1True() {
//        // reading 151 > 100+50; other readings are normal
//        assertEquals(3,
//                CanalMonitor.classifyCanalReadings(new int[]{151, 80}, 100, false));
//    }
//
//    @Test
//    // MC/DC C1=false: reading at critical threshold only → not 3
//    void c1False() {
//        // 150 is NOT > 150; all readings normal → 0
//        assertEquals(0,
//                CanalMonitor.classifyCanalReadings(new int[]{100, 80}, 100, false));
//    }
//
//    // --- C2: isFloodSeason — changes which r-threshold is used ---
//    // Both tests have r = 0.6 (3 out of 5 high).
//    // With flood season:     0.6 > 0.5  → 2
//    // Without flood season:  0.6 ≤ 0.75 → 1
//
//    @Test
//    // MC/DC C2=true: r=0.6 in flood season → 2
//    void c2True() {
//        int[] readings = {101, 102, 103, 50, 50}; // 3/5 high
//        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, true));
//    }
//
//    @Test
//    // MC/DC C2=false: r=0.6 outside flood season → 1
//    void c2False() {
//        int[] readings = {101, 102, 103, 50, 50}; // 3/5 high
//        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    // --- C3: r > 0.5 (flood season path) ---
//    // C3=true  with flood season → 2
//    // C3=false with flood season, r > 0 → 1
//
//    @Test
//    // MC/DC C3=true: r > 0.5, flood season → 2
//    void c3True() {
//        int[] readings = {101, 102, 80}; // 2/3 high, r≈0.667
//        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, true));
//    }
//
//    @Test
//    // MC/DC C3=false: r = 0.5, flood season → 1
//    void c3False() {
//        int[] readings = {101, 80}; // 1/2 high, r=0.5 (not strictly >)
//        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, true));
//    }
//
//    // --- C4: r > 0.75 (non-flood season path) ---
//    // C4=true  with non-flood → 2
//    // C4=false with non-flood, r > 0 → 1
//
//    @Test
//    // MC/DC C4=true: r > 0.75, non-flood season → 2
//    void c4True() {
//        int[] readings = {101, 102, 103, 104}; // 4/4 high, r=1.0
//        assertEquals(2, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    @Test
//    // MC/DC C4=false: r = 0.75, non-flood season → 1
//    void c4False() {
//        int[] readings = {101, 102, 103, 80}; // 3/4 high, r=0.75 (not strictly >)
//        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    // --- C5: r > 0 (distinguishes 0 from 1) ---
//    // C5=true  → 1 (elevated)
//    // C5=false → 0 (normal)
//
//    @Test
//    // MC/DC C5=true: exactly one high reading → 1
//    void c5True() {
//        int[] readings = {101, 80, 80, 80}; // 1/4 high, r=0.25
//        assertEquals(1, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
//
//    @Test
//    // MC/DC C5=false: no high readings → 0
//    void c5False() {
//        int[] readings = {100, 80, 80, 80}; // 0/4 high
//        assertEquals(0, CanalMonitor.classifyCanalReadings(readings, 100, false));
//    }
}