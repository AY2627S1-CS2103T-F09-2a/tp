package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class GuardianContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        GuardianContainsKeywordsPredicate firstPredicate =
                new GuardianContainsKeywordsPredicate(firstPredicateKeywordList);
        GuardianContainsKeywordsPredicate secondPredicate =
                new GuardianContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        GuardianContainsKeywordsPredicate firstPredicateCopy =
                new GuardianContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // same keywords as a name predicate -> returns false
        assertFalse(firstPredicate.equals(new NameContainsKeywordsPredicate(firstPredicateKeywordList)));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuardianContainsKeywordsPredicate(null));
    }

    @Test
    public void constructor_keywordsChangedAfterwards_predicateUnchanged() {
        List<String> keywords = new ArrayList<>(List.of("Anil"));
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(keywords);
        keywords.add("Mei");

        assertFalse(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));
        assertEquals(new GuardianContainsKeywordsPredicate(List.of("Anil")), predicate);
    }

    @Test
    public void test_nullPerson_throwsNullPointerException() {
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(List.of("Mei"));
        assertThrows(NullPointerException.class, () -> predicate.test(null));
    }

    @Test
    public void hashCodeMethod() {
        assertEquals(new GuardianContainsKeywordsPredicate(List.of("Mei")).hashCode(),
                new GuardianContainsKeywordsPredicate(List.of("Mei")).hashCode());
    }

    @Test
    public void test_guardianNameContainsKeywords_returnsTrue() {
        // One keyword
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(List.of("Mei"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Multiple keywords
        predicate = new GuardianContainsKeywordsPredicate(List.of("Mei", "Tan"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Only one matching keyword
        predicate = new GuardianContainsKeywordsPredicate(List.of("Anil", "Tan"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Mixed-case keywords
        predicate = new GuardianContainsKeywordsPredicate(List.of("mEI", "tAN"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));
    }

    @Test
    public void test_guardianPhoneEqualsKeyword_returnsTrue() {
        // Exact phone
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(List.of("92345678"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianPhone("92345678").build()));

        // Phone entered with separators is stored without them
        assertTrue(predicate.test(new PersonBuilder().withGuardianPhone("9234-5678").build()));

        // Phone keyword typed with separators
        predicate = new GuardianContainsKeywordsPredicate(List.of("9234-5678"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianPhone("92345678").build()));

        // Phone keyword mixed with a non-matching name keyword
        predicate = new GuardianContainsKeywordsPredicate(List.of("Anil", "92345678"));
        assertTrue(predicate.test(new PersonBuilder().withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").build()));
    }

    @Test
    public void test_compoundGuardianName_matchesWholeTokensOnly() {
        // Words are split on whitespace only, so hyphenated and apostrophe names match as whole tokens
        Person person = new PersonBuilder().withGuardianName("Anne-Marie O'Neil").build();
        assertTrue(new GuardianContainsKeywordsPredicate(List.of("anne-marie")).test(person));
        assertTrue(new GuardianContainsKeywordsPredicate(List.of("O'NEIL")).test(person));

        assertFalse(new GuardianContainsKeywordsPredicate(List.of("Anne", "Marie", "Neil")).test(person));
    }

    @Test
    public void test_numericKeywordInGuardianName_returnsTrue() {
        // Digits are allowed in guardian names, so a numeric keyword falls back to name matching
        Person person = new PersonBuilder().withGuardianName("Mei Tan 2").withGuardianPhone("92345678").build();
        assertTrue(new GuardianContainsKeywordsPredicate(List.of("2")).test(person));

        // An 8-digit name word matches even though it is not the guardian phone
        person = new PersonBuilder().withGuardianName("Mei 87654321").withGuardianPhone("92345678").build();
        assertTrue(new GuardianContainsKeywordsPredicate(List.of("87654321")).test(person));
    }

    @Test
    public void test_guardianDoesNotMatchKeywords_returnsFalse() {
        // Zero keywords
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Non-matching keyword
        predicate = new GuardianContainsKeywordsPredicate(List.of("Anil"));
        assertFalse(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Partial guardian name word
        predicate = new GuardianContainsKeywordsPredicate(List.of("Me"));
        assertFalse(predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));

        // Partial guardian phone
        predicate = new GuardianContainsKeywordsPredicate(List.of("9234"));
        assertFalse(predicate.test(new PersonBuilder().withGuardianPhone("92345678").build()));

        // Keywords match student name, phone, email and address, but not guardian details
        predicate = new GuardianContainsKeywordsPredicate(
                List.of("Alex", "91234567", "alex@example.com", "Clementi"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alex").withPhone("91234567")
                .withEmail("alex@example.com").withAddress("Clementi").withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(keywords);

        String expected = GuardianContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
