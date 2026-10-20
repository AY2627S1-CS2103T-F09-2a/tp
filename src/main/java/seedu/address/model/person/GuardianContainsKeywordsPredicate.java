package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code GuardianName} contains any of the keywords as a whole word,
 * or that the {@code GuardianPhone} equals any of the keywords exactly.
 */
public class GuardianContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public GuardianContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        String guardianName = person.getGuardianName().getFullName();
        String guardianPhone = person.getGuardianPhone().getValue();
        return keywords.stream()
                .anyMatch(keyword -> keyword.equals(guardianPhone)
                        || StringUtil.containsWordIgnoreCase(guardianName, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuardianContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
