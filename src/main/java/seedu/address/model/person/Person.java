package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a student in Poco Book.
 * Guarantees: name and guardian contact are present and not null; field values are validated, immutable.
 * The student's phone, email, address and outstanding fee are optional.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final GuardianPhone guardianPhone;

    // Data fields
    private final GuardianName guardianName;
    private final Phone phone;
    private final Email email;
    private final Address address;
    private final OutstandingFee outstandingFee;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Name, guardian name, guardian phone and tags must be present and not null.
     * {@code phone}, {@code email}, {@code address} and {@code outstandingFee} may be null when not provided.
     */
    public Person(Name name, Phone phone, Email email, Address address, GuardianName guardianName,
            GuardianPhone guardianPhone, OutstandingFee outstandingFee, Set<Tag> tags) {
        requireAllNonNull(name, guardianName, guardianPhone, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.outstandingFee = outstandingFee;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Optional<Phone> getPhone() {
        return Optional.ofNullable(phone);
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }

    public GuardianName getGuardianName() {
        return guardianName;
    }

    public GuardianPhone getGuardianPhone() {
        return guardianPhone;
    }

    /**
     * Returns the outstanding fee, or an empty {@code Optional} if there is none.
     */
    public Optional<OutstandingFee> getOutstandingFee() {
        return Optional.ofNullable(outstandingFee);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both students have the same name, ignoring case, and the same guardian phone.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().isSameNameIgnoreCase(getName())
                && otherPerson.getGuardianPhone().equals(getGuardianPhone());
    }

    /**
     * Returns true if both students would be displayed and saved with exactly the same text in every field.
     * Unlike {@link #equals(Object)}, a difference in capitalisation counts, because guardian names and tags
     * compare without regard to case.
     */
    public boolean hasIdenticalDetails(Person otherPerson) {
        return equals(otherPerson)
                && guardianName.getFullName().equals(otherPerson.guardianName.getFullName())
                && tagNames().equals(otherPerson.tagNames());
    }

    private Set<String> tagNames() {
        return tags.stream().map(tag -> tag.tagName).collect(Collectors.toSet());
    }

    /**
     * Returns true if both students have the same identity and data fields.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && Objects.equals(phone, otherPerson.phone)
                && Objects.equals(email, otherPerson.email)
                && Objects.equals(address, otherPerson.address)
                && guardianName.equals(otherPerson.guardianName)
                && guardianPhone.equals(otherPerson.guardianPhone)
                && Objects.equals(outstandingFee, otherPerson.outstandingFee)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, guardianName, guardianPhone, outstandingFee, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("guardianName", guardianName)
                .add("guardianPhone", guardianPhone)
                .add("outstandingFee", outstandingFee)
                .add("tags", tags)
                .toString();
    }

}
