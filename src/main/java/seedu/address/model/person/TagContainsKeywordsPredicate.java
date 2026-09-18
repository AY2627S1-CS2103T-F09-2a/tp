package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * Tests whether a person has any of the specified tags, ignoring case.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Creates a predicate using a defensive copy of the given tag keywords.
     */
    public TagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(requireNonNull(keywords));
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return person.getTags().stream()
                .anyMatch(tag -> keywords.stream().anyMatch(keyword -> tag.tagName.equalsIgnoreCase(keyword)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TagContainsKeywordsPredicate otherPredicate)) {
            return false;
        }
        return keywords.equals(otherPredicate.keywords);
    }
}
