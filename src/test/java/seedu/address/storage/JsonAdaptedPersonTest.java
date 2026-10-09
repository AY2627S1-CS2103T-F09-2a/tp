package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import seedu.address.model.person.Phone;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_GUARDIAN_NAME = "";
    private static final String INVALID_GUARDIAN_PHONE = "9-123";
    private static final String INVALID_TAG = "#friend";

    // Invalid outstanding fee data: malformed, zero, negative, over-limit and over-precision values.
    private static final String MALFORMED_FEE = "forty";
    private static final String ZERO_FEE = "0";
    private static final String ZERO_DECIMAL_FEE = "0.00";
    private static final String NEGATIVE_FEE = "-1.00";
    private static final String OVER_LIMIT_FEE = "100000.00";
    private static final String OVER_PRECISION_FEE = "1.234";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_GUARDIAN_NAME = BENSON.getGuardianName().toString();
    private static final String VALID_GUARDIAN_PHONE = BENSON.getGuardianPhone().toString();
    private static final String VALID_FEE = "45.50";
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_validPersonWithoutFee_outstandingFeeIsNull() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, null, VALID_TAGS);
        assertNull(person.toModelType().getOutstandingFee());
    }

    @Test
    public void toModelType_minimumBoundaryFee_returnsPersonWithFee() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, "0.01", VALID_TAGS);
        assertEquals(new OutstandingFee("0.01"), person.toModelType().getOutstandingFee());
    }

    @Test
    public void toModelType_maximumBoundaryFee_returnsPersonWithFee() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, "99999.99", VALID_TAGS);
        assertEquals(new OutstandingFee("99999.99"), person.toModelType().getOutstandingFee());
    }

    @Test
    public void toModelType_unnormalizedGuardianPhone_normalizesGuardianPhone() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, " 8765-4321 ", null, VALID_TAGS);
        assertEquals(new GuardianPhone("87654321"), person.toModelType().getGuardianPhone());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidGuardianName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                INVALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = GuardianName.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullGuardianName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                null, VALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidGuardianPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, INVALID_GUARDIAN_PHONE, VALID_FEE, VALID_TAGS);
        String expectedMessage = GuardianPhone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullGuardianPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, null, VALID_FEE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GuardianPhone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_malformedFee_throwsIllegalValueException() {
        assertInvalidFeeRejected(MALFORMED_FEE);
    }

    @Test
    public void toModelType_zeroFee_throwsIllegalValueException() {
        assertInvalidFeeRejected(ZERO_FEE);
        assertInvalidFeeRejected(ZERO_DECIMAL_FEE);
    }

    @Test
    public void toModelType_negativeFee_throwsIllegalValueException() {
        assertInvalidFeeRejected(NEGATIVE_FEE);
    }

    @Test
    public void toModelType_overLimitFee_throwsIllegalValueException() {
        assertInvalidFeeRejected(OVER_LIMIT_FEE);
    }

    @Test
    public void toModelType_overPrecisionFee_throwsIllegalValueException() {
        assertInvalidFeeRejected(OVER_PRECISION_FEE);
    }

    /**
     * Asserts that {@code fee} is rejected as invalid persisted fee data instead of being
     * silently converted to an absent fee.
     */
    private static void assertInvalidFeeRejected(String fee) {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, fee, VALID_TAGS);
        String expectedMessage = OutstandingFee.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_GUARDIAN_NAME, VALID_GUARDIAN_PHONE, VALID_FEE, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }
}
