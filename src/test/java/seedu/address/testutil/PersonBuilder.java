package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Name;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_GUARDIAN_NAME = "Guardian";
    public static final String DEFAULT_GUARDIAN_PHONE = "91234567";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private GuardianName guardianName;
    private GuardianPhone guardianPhone;
    private OutstandingFee outstandingFee;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        guardianName = new GuardianName(DEFAULT_GUARDIAN_NAME);
        guardianPhone = new GuardianPhone(DEFAULT_GUARDIAN_PHONE);
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone().orElse(null);
        email = personToCopy.getEmail().orElse(null);
        address = personToCopy.getAddress().orElse(null);
        guardianName = personToCopy.getGuardianName();
        guardianPhone = personToCopy.getGuardianPhone();
        outstandingFee = personToCopy.getOutstandingFee().orElse(null);
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Removes the student's {@code Phone}, {@code Email} and {@code Address} from the {@code Person} that we are
     * building, as when only the required details are given.
     */
    public PersonBuilder withoutContactDetails() {
        this.phone = null;
        this.email = null;
        this.address = null;
        return this;
    }

    /**
     * Sets the {@code GuardianName} of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianName(String guardianName) {
        this.guardianName = new GuardianName(guardianName);
        return this;
    }

    /**
     * Sets the {@code GuardianPhone} of the {@code Person} that we are building.
     */
    public PersonBuilder withGuardianPhone(String guardianPhone) {
        this.guardianPhone = new GuardianPhone(guardianPhone);
        return this;
    }

    /**
     * Sets the {@code OutstandingFee} of the {@code Person} that we are building.
     */
    public PersonBuilder withOutstandingFee(String outstandingFee) {
        this.outstandingFee = outstandingFee == null ? null : new OutstandingFee(outstandingFee);
        return this;
    }

    /**
     * Removes the {@code OutstandingFee} of the {@code Person} that we are building.
     */
    public PersonBuilder withoutOutstandingFee() {
        this.outstandingFee = null;
        return this;
    }

    public Person build() {
        return new Person(name, phone, email, address, guardianName, guardianPhone, outstandingFee, tags);
    }

}
