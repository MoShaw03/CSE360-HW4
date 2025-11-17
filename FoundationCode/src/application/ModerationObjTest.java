package application;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * Unit tests for ModerationObj items for Staff (Questions, Answers, Reviews).
 */
public class ModerationObjTest {

    @Test
    public final void testModerationObj() { 
        LocalDateTime t = LocalDateTime.now();
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.QUESTION,
                101,
                t,
                "alice",
                "lorem ipsum",
                false
        );

        assertEquals(ModerationObj.ContentType.QUESTION, m.getContentType());
        assertEquals(101, m.getId());
        assertEquals(t, m.getDate());
        assertEquals("alice", m.getAuthor());
        assertEquals("lorem ipsum", m.getText());
        assertFalse(m.isDeleted());
    }

    @Test
    public final void testGetContentType() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.REVIEW,
                5,
                LocalDateTime.now(),
                "bob",
                "review body",
                false
        );
        assertEquals(ModerationObj.ContentType.REVIEW, m.getContentType());
    }

    @Test
    public final void testGetId() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.ANSWER,
                77,
                LocalDateTime.now(),
                "carol",
                "answer",
                false
        );
        assertEquals(77, m.getId());
    }

    @Test
    public final void testGetDate() {
        LocalDateTime ts = LocalDateTime.now().minusHours(2);
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.QUESTION,
                9,
                ts,
                "dave",
                "q body",
                false
        );
        assertEquals(ts, m.getDate());
    }

    @Test
    public final void testGetAuthor() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.ANSWER,
                12,
                LocalDateTime.now(),
                "eve",
                "a body",
                false
        );
        assertEquals("eve", m.getAuthor());
    }

    @Test
    public final void testGetText() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.REVIEW,
                3,
                LocalDateTime.now(),
                "frank",
                "review goes here",
                false
        );
        assertEquals("review goes here", m.getText());
    }

    @Test
    public final void testIsDeleted() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.QUESTION,
                1,
                LocalDateTime.now(),
                "greg",
                "body",
                true
        );
        assertTrue(m.isDeleted());
    }

    @Test
    public final void testSetText() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.ANSWER,
                2,
                LocalDateTime.now(),
                "heidi",
                "old",
                false
        );
        m.setText("new text");
        assertEquals("new text", m.getText());
    }

    @Test
    public final void testSetDeleted() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.REVIEW,
                4,
                LocalDateTime.now(),
                "ivan",
                "x",
                false
        );
        assertFalse(m.isDeleted());
        m.setDeleted(true);
        assertTrue(m.isDeleted());
    }

    @Test
    public final void testToString() {
        ModerationObj m = new ModerationObj(
                ModerationObj.ContentType.QUESTION,
                99,
                LocalDateTime.of(2025, 1, 2, 3, 4),
                "jane",
                "hello",
                false
        );
        String s = m.toString();
        // Kept this here for the key fields.
        assertTrue(s.contains("QUESTION"));
        assertTrue(s.contains("99"));
        assertTrue(s.contains("jane"));
        assertTrue(s.contains("hello"));
    }
}