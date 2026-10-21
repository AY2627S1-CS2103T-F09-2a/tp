package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code GuardianName} contains any of the keywords as a whole word,
 * or that the {@code GuardianPhone} equals any of the keywords once separators are removed.
 */
public class GuardianContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Creates a predicate matching any of the given keywords.
     * Each keyword must be a single nonempty token; NFC normalization is the caller's responsibility.
     *
     * @param keywords Keywords to match against guardian names and phones.
     */
    public GuardianContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        String guardianName = person.getGuardianName().getFullName();
        GuardianPhone guardianPhone = person.getGuardianPhone();
        return keywords.stream()
                .anyMatch(keyword -> matchesPhone(keyword, guardianPhone)
                        || StringUtil.containsWordIgnoreCase(guardianName, keyword));
    }

    /**
     * Returns true if {@code keyword} is a valid phone that equals {@code phone} after normalization,
     * so a keyword typed as {@code 9234-5678} matches a phone stored as {@code 92345678}.
     */
    private static boolean matchesPhone(String keyword, GuardianPhone phone) {
        return GuardianPhone.isValidPhone(keyword) && new GuardianPhone(keyword).equals(phone);
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
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
