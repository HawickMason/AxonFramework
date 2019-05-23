package org.axonframework.eventhandling;

import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiSourceTrackingTokenTest {

    MultiSourceTrackingToken testSubject;

    @Before
    public void setUp() {
        Map<String, TrackingToken> tokenMap = new HashMap<>();
        tokenMap.put("token1", new GlobalSequenceTrackingToken(0));
        tokenMap.put("token2", new GlobalSequenceTrackingToken(0));

        testSubject = new MultiSourceTrackingToken(tokenMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleToken() {
        testSubject.covers(new GlobalSequenceTrackingToken(0));
    }

    @Test
    public void lowerBound() {
        Map<String, TrackingToken> newTokens = new HashMap<>();
        newTokens.put("token1", new GlobalSequenceTrackingToken(1));
        newTokens.put("token2", new GlobalSequenceTrackingToken(2));

        Map<String, TrackingToken> expectedTokens = new HashMap<>();
        expectedTokens.put("token1", new GlobalSequenceTrackingToken(0));
        expectedTokens.put("token2", new GlobalSequenceTrackingToken(0));
        ;

        MultiSourceTrackingToken newMultiToken = (MultiSourceTrackingToken) testSubject
                .lowerBound(new MultiSourceTrackingToken(newTokens));

        assertEquals(new MultiSourceTrackingToken(expectedTokens), newMultiToken);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundMismatchTokens() {
        Map<String, TrackingToken> newTokens = new HashMap<>();
        newTokens.put("token1", new GlobalSequenceTrackingToken(1));
        newTokens.put("token3", new GlobalSequenceTrackingToken(2));

        Map<String, TrackingToken> expectedTokens = new HashMap<>();
        expectedTokens.put("token1", new GlobalSequenceTrackingToken(0));
        expectedTokens.put("token2", new GlobalSequenceTrackingToken(0));
        ;

        MultiSourceTrackingToken newMultiToken = (MultiSourceTrackingToken) testSubject
                .lowerBound(new MultiSourceTrackingToken(newTokens));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundDifferentNumberOfTokens() {
        Map<String, TrackingToken> newTokens = new HashMap<>();
        newTokens.put("token1", new GlobalSequenceTrackingToken(1));

        Map<String, TrackingToken> expectedTokens = new HashMap<>();
        expectedTokens.put("token1", new GlobalSequenceTrackingToken(0));
        expectedTokens.put("token2", new GlobalSequenceTrackingToken(0));
        ;

        MultiSourceTrackingToken newMultiToken = (MultiSourceTrackingToken) testSubject
                .lowerBound(new MultiSourceTrackingToken(newTokens));
    }

    @Test
    public void upperBound() {
        Map<String, TrackingToken> newTokens = new HashMap<>();

        newTokens.put("token1", new GlobalSequenceTrackingToken(1));
        newTokens.put("token2", new GlobalSequenceTrackingToken(2));

        MultiSourceTrackingToken newMultiToken = (MultiSourceTrackingToken) testSubject
                .upperBound(new MultiSourceTrackingToken(newTokens));

        assertEquals(new MultiSourceTrackingToken(newTokens), newMultiToken);
    }

    @Test
    public void covers() {
        Map<String, TrackingToken> newTokens = new HashMap<>();
        newTokens.put("token1", new GlobalSequenceTrackingToken(0));
        newTokens.put("token2", new GlobalSequenceTrackingToken(0));

        assertTrue(testSubject.covers(new MultiSourceTrackingToken(newTokens)));
    }

    @Test
    public void doesNotCover() {
        Map<String, TrackingToken> newTokens = new HashMap<String, TrackingToken>();
        newTokens.put("token1", new GlobalSequenceTrackingToken(1));
        newTokens.put("token2", new GlobalSequenceTrackingToken(0));

        assertFalse(testSubject.covers(new MultiSourceTrackingToken(newTokens)));
    }

    @Test
    public void advancedTo() {
        testSubject.advancedTo("token1", new GlobalSequenceTrackingToken(4));

        assertEquals(new GlobalSequenceTrackingToken(4), testSubject.getTokenForStream("token1"));
        //other token remains unchanged
        assertEquals(new GlobalSequenceTrackingToken(0), testSubject.getTokenForStream("token2"));
    }

    @Test
    public void getTokenForStream() {
        assertEquals(new GlobalSequenceTrackingToken(0), testSubject.getTokenForStream("token1"));
    }

    @Test
    public void hasPosition() {
        assertEquals(0L, testSubject.position().getAsLong());
    }

    @Test
    public void equals() {
        Map<String, TrackingToken> tokenMap = new HashMap<>();
        tokenMap.put("token1", new GlobalSequenceTrackingToken(0));
        tokenMap.put("token2", new GlobalSequenceTrackingToken(0));

        MultiSourceTrackingToken newMultiToken = new MultiSourceTrackingToken(tokenMap);

        assertTrue(newMultiToken.equals(testSubject));
    }

    @Test
    public void notEquals() {
        Map<String, TrackingToken> tokenMap = new HashMap<>();
        tokenMap.put("token1", new GlobalSequenceTrackingToken(1));
        tokenMap.put("token2", new GlobalSequenceTrackingToken(0));

        MultiSourceTrackingToken newMultiToken = new MultiSourceTrackingToken(tokenMap);

        assertFalse(newMultiToken.equals(testSubject));
    }

    @Test
    public void constituantTokenNotInitialized() {
        Map<String, TrackingToken> tokenMap = new HashMap<>();
        tokenMap.put("token1", null);
        tokenMap.put("token2", new GlobalSequenceTrackingToken(0));

        MultiSourceTrackingToken newMultiToken = new MultiSourceTrackingToken(tokenMap);

        assertFalse(newMultiToken.equals(testSubject));
    }
}