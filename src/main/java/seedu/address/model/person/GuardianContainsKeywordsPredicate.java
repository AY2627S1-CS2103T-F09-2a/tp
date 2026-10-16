package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;

/**
 * Matches any whole guardian-name word, ignoring case, or an exact guardian phone number.
 */
public class GuardianContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public GuardianContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(requireNonNull(keywords));
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return keywords.stream().anyMatch(keyword ->
                StringUtil.containsWordIgnoreCase(person.getGuardianName().getFullName(), keyword)
                || person.getGuardianPhone().toString().equals(keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof GuardianContainsKeywordsPredicate predicate
                && keywords.equals(predicate.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }
}
