package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Name;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String guardianName;
    private final String guardianPhone;
    private final String outstandingFee;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("guardianName") String guardianName,
            @JsonProperty("guardianPhone") String guardianPhone,
            @JsonProperty("outstandingFee") String outstandingFee,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.outstandingFee = outstandingFee;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().map(p -> p.value).orElse(null);
        email = source.getEmail().map(e -> e.value).orElse(null);
        address = source.getAddress().map(a -> a.value).orElse(null);
        guardianName = source.getGuardianName().getFullName();
        guardianPhone = source.getGuardianPhone().getValue();
        outstandingFee = source.getOutstandingFee().map(OutstandingFee::toString).orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
            }
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone != null && !Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = phone == null ? null : new Phone(phone);

        if (email != null && !Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = email == null ? null : new Email(email);

        if (address != null && !Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = address == null ? null : new Address(address);

        if (guardianName == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianName.class.getSimpleName()));
        }
        if (!GuardianName.isValidName(guardianName)) {
            throw new IllegalValueException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        final GuardianName modelGuardianName = new GuardianName(guardianName);

        if (guardianPhone == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianPhone.class.getSimpleName()));
        }
        if (!GuardianPhone.isValidPhone(guardianPhone)) {
            throw new IllegalValueException(GuardianPhone.MESSAGE_CONSTRAINTS);
        }
        final GuardianPhone modelGuardianPhone = new GuardianPhone(guardianPhone);

        final OutstandingFee modelOutstandingFee;
        if (outstandingFee == null) {
            modelOutstandingFee = null;
        } else if (!OutstandingFee.isValidOutstandingFee(outstandingFee)) {
            throw new IllegalValueException(OutstandingFee.MESSAGE_CONSTRAINTS);
        } else {
            modelOutstandingFee = new OutstandingFee(outstandingFee);
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelGuardianName, modelGuardianPhone,
                modelOutstandingFee, modelTags);
    }

}
