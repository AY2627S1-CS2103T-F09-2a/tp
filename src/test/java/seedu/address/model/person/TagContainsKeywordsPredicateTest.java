package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TagContainsKeywordsPredicateTest {
    @Test
    public void test_matchesWholeTagsIgnoringCase() {
        Person person = new PersonBuilder().withTags("friends", "colleagues").build();
        assertTrue(new TagContainsKeywordsPredicate(List.of("FRIENDS", "family")).test(person));
        assertFalse(new TagContainsKeywordsPredicate(List.of("friend")).test(person));
        assertFalse(new TagContainsKeywordsPredicate(List.of()).test(person));
        assertFalse(new TagContainsKeywordsPredicate(List.of("friends"))
                .test(new PersonBuilder().withTags().build()));
    }

    @Test
    public void constructor_copiesKeywords() {
        List<String> keywords = new ArrayList<>(List.of("friends"));
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);
        keywords.clear();
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
    }

    @Test
    public void equals_comparesKeywords() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(List.of("friends"));
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new TagContainsKeywordsPredicate(List.of("friends"))));
        assertFalse(predicate.equals(new TagContainsKeywordsPredicate(List.of("family"))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("friends"));
    }
}
