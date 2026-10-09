package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final GuardianName guardianName;
    private final GuardianPhone guardianPhone;
    private final OutstandingFee outstandingFee;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null, except {@code outstandingFee} which is
     * null when the person has no outstanding fee.
     */
    public Person(Name name, Phone phone, Email email, Address address, GuardianName guardianName,
            GuardianPhone guardianPhone, OutstandingFee outstandingFee, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, guardianName, guardianPhone, tags);
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

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public GuardianName getGuardianName() {
        return guardianName;
    }

    public GuardianPhone getGuardianPhone() {
        return guardianPhone;
    }

    /**
     * Returns the outstanding fee, or null if the person has no outstanding fee.
     */
    public OutstandingFee getOutstandingFee() {
        return outstandingFee;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons share the normalized student name and guardian phone.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && normalizeName(otherPerson.getName()).equals(normalizeName(getName()))
                && otherPerson.getGuardianPhone().equals(getGuardianPhone());
    }

    private static String normalizeName(Name name) {
        return java.text.Normalizer.normalize(name.fullName, java.text.Normalizer.Form.NFC)
                .trim().replaceAll(" +", " ").toLowerCase(java.util.Locale.ROOT);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
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
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
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
                .add("guardian name", guardianName)
                .add("guardian phone", guardianPhone)
                .add("outstanding fee", outstandingFee)
                .add("tags", tags)
                .toString();
    }

}
