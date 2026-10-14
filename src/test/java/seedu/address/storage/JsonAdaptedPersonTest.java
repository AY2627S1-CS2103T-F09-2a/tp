package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

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
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_GUARDIAN_NAME = "M@ry";
    private static final String INVALID_GUARDIAN_PHONE = "12345678";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().get().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().get().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().get().toString();
    private static final String VALID_GUARDIAN_NAME = BENSON.getGuardianName().toString();
    private static final String VALID_GUARDIAN_PHONE = BENSON.getGuardianPhone().toString();
    private static final String VALID_OUTSTANDING_FEE = BENSON.getOutstandingFee().get().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_studentWithOnlyRequiredDetails_returnsPerson() throws Exception {
        Person student = new PersonBuilder(BENSON).withoutContactDetails().withTags().build();
        assertEquals(student, new JsonAdaptedPerson(student).toModelType());
    }

    @Test
    public void toModelType_studentWithOutstandingFee_preservesTwoDecimalPlaces() throws Exception {
        Person student = new PersonBuilder(BENSON).withOutstandingFee("180").build();
        Person loaded = new JsonAdaptedPerson(student).toModelType();
        assertEquals(student, loaded);
        assertEquals("180.00", loaded.getOutstandingFee().get().toString());
    }

    @Test
    public void toModelType_absentOutstandingFee_returnsPersonWithoutFee() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, null);
        Person expectedPerson = new PersonBuilder(BENSON).withoutOutstandingFee().build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_minimumOutstandingFee_returnsPerson() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, "0.01");
        Person expectedPerson = new PersonBuilder(BENSON).withOutstandingFee("0.01").build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_formattedGuardianPhone_returnsNormalizedPhone() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, "8 234-5678", VALID_OUTSTANDING_FEE);
        assertEquals("82345678", person.toModelType().getGuardianPhone().getValue());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, Address.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullOptionalDetails_returnsPersonWithoutThem() throws Exception {
        JsonAdaptedPerson person = createPerson(VALID_NAME, null, null, null,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, null);
        Person loaded = person.toModelType();
        assertTrue(loaded.getPhone().isEmpty());
        assertTrue(loaded.getEmail().isEmpty());
        assertTrue(loaded.getAddress().isEmpty());
        assertTrue(loaded.getOutstandingFee().isEmpty());
    }

    @Test
    public void toModelType_invalidGuardianName_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                INVALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, GuardianName.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullGuardianName_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                null, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidGuardianPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, INVALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE);
        assertThrows(IllegalValueException.class, GuardianPhone.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullGuardianPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, null, VALID_OUTSTANDING_FEE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianPhone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidOutstandingFees_throwIllegalValueException() {
        List<String> invalidOutstandingFees = List.of("invalid", "0", "0.00", "-0.01", "100000", "99999.991");

        for (String invalidOutstandingFee : invalidOutstandingFees) {
            JsonAdaptedPerson person = createPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                    VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, invalidOutstandingFee);
            assertThrows(IllegalValueException.class, OutstandingFee.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_nullTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_OUTSTANDING_FEE, invalidTags);
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    private static JsonAdaptedPerson createPerson(String name, String phone, String email, String address,
            String guardianName, String guardianPhone, String outstandingFee) {
        return new JsonAdaptedPerson(name, phone, email, address, guardianName, guardianPhone, outstandingFee,
                VALID_TAGS);
    }

}
