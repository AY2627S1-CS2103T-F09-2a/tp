package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.FEE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.GUARDIAN_NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.GUARDIAN_NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.GUARDIAN_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.GUARDIAN_PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_FEE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_GUARDIAN_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_GUARDIAN_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_MISSING_PARAMETER;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_REPEATED_PARAMETER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Name;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String REQUIRED_DESC_BOB = NAME_DESC_BOB + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB;
    private static final String ALL_DESC_BOB = REQUIRED_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
            + ADDRESS_DESC_BOB + FEE_DESC_BOB;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + ALL_DESC_BOB, new AddCommand(expectedPerson));

        // any order
        assertParseSuccess(parser, FEE_DESC_BOB + ADDRESS_DESC_BOB + GUARDIAN_PHONE_DESC_BOB + EMAIL_DESC_BOB
                + NAME_DESC_BOB + PHONE_DESC_BOB + GUARDIAN_NAME_DESC_BOB, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // only name and guardian contact
        Person expectedPerson = new PersonBuilder(BOB).withoutContactDetails().withoutOutstandingFee().withTags()
                .build();
        assertParseSuccess(parser, REQUIRED_DESC_BOB, new AddCommand(expectedPerson));

        // no outstanding fee
        expectedPerson = new PersonBuilder(BOB).withoutOutstandingFee().withTags().build();
        assertParseSuccess(parser, REQUIRED_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_specExamples_success() {
        Person alex = new PersonBuilder().withName("Alex Tan").withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").withoutContactDetails().build();
        assertParseSuccess(parser, " n/Alex Tan g/Mei Tan gp/92345678", new AddCommand(alex));

        Person riya = new PersonBuilder().withName("Riya Kumar").withGuardianName("Anil Kumar")
                .withGuardianPhone("97654321").withPhone("98765432").withEmail("riya@example.com")
                .withAddress("12 Clementi Ave 3").withOutstandingFee("75.50").build();
        assertParseSuccess(parser, " n/Riya Kumar g/Anil Kumar gp/97654321 p/98765432 e/riya@example.com"
                + " a/12 Clementi Ave 3 f/75.50", new AddCommand(riya));
    }

    @Test
    public void parse_valuesNeedingNormalization_success() {
        Person expected = new PersonBuilder().withName("Alex Tan").withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").withPhone("91234567").withAddress("12 Clementi Ave 3")
                .withEmail("alex@example.com").withOutstandingFee("180").build();
        assertParseSuccess(parser, " n/  Alex   Tan  g/Mei   Tan gp/9234-5678 p/9123 4567 e/alex@example.com"
                + " a/12  Clementi Ave 3 f/180", new AddCommand(expected));
    }

    @Test
    public void parse_feeBoundaries() {
        Person minimumFee = new PersonBuilder(BOB).withTags().withOutstandingFee("0.01").build();
        assertParseSuccess(parser, REQUIRED_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + " f/0.01", new AddCommand(minimumFee));

        Person maximumFee = new PersonBuilder(BOB).withTags().withOutstandingFee("99999.99").build();
        assertParseSuccess(parser, REQUIRED_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + " f/99999.99", new AddCommand(maximumFee));

        String[] invalidFees = {"0", "0.00", "100000", "99999.991", "-5", "+5", "$5", "1,000", "1 000", "1e3", "5."};
        for (String fee : invalidFees) {
            assertParseFailure(parser, REQUIRED_DESC_BOB + " f/" + fee, OutstandingFee.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedValue_failure() {
        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + ALL_DESC_BOB, String.format(MESSAGE_REPEATED_PARAMETER, "name"));

        // multiple guardian names
        assertParseFailure(parser, ALL_DESC_BOB + GUARDIAN_NAME_DESC_AMY,
                String.format(MESSAGE_REPEATED_PARAMETER, "guardian name"));

        // multiple guardian phones
        assertParseFailure(parser, ALL_DESC_BOB + GUARDIAN_PHONE_DESC_AMY,
                String.format(MESSAGE_REPEATED_PARAMETER, "guardian phone"));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + ALL_DESC_BOB, String.format(MESSAGE_REPEATED_PARAMETER, "phone"));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + ALL_DESC_BOB, String.format(MESSAGE_REPEATED_PARAMETER, "email"));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + ALL_DESC_BOB,
                String.format(MESSAGE_REPEATED_PARAMETER, "address"));

        // multiple outstanding fees
        assertParseFailure(parser, ALL_DESC_BOB + " f/10", String.format(MESSAGE_REPEATED_PARAMETER,
                "outstanding fee"));

        // repeated even when the repeated value is invalid
        assertParseFailure(parser, ALL_DESC_BOB + INVALID_PHONE_DESC,
                String.format(MESSAGE_REPEATED_PARAMETER, "phone"));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // missing name prefix
        assertParseFailure(parser, GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB,
                String.format(MESSAGE_MISSING_PARAMETER, PREFIX_NAME) + "\n" + AddCommand.MESSAGE_USAGE);

        // missing guardian name prefix
        assertParseFailure(parser, NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB + PHONE_DESC_BOB,
                String.format(MESSAGE_MISSING_PARAMETER, PREFIX_GUARDIAN_NAME) + "\n" + AddCommand.MESSAGE_USAGE);

        // missing guardian phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + GUARDIAN_NAME_DESC_BOB,
                String.format(MESSAGE_MISSING_PARAMETER, PREFIX_GUARDIAN_PHONE) + "\n" + AddCommand.MESSAGE_USAGE);

        // several prefixes missing, all named at once
        assertParseFailure(parser, NAME_DESC_BOB,
                String.format(MESSAGE_MISSING_PARAMETER, PREFIX_GUARDIAN_NAME + ", " + PREFIX_GUARDIAN_PHONE)
                        + "\n" + AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, PHONE_DESC_BOB,
                String.format(MESSAGE_MISSING_PARAMETER,
                        PREFIX_NAME + ", " + PREFIX_GUARDIAN_NAME + ", " + PREFIX_GUARDIAN_PHONE)
                        + "\n" + AddCommand.MESSAGE_USAGE);

        // all prefixes missing
        assertParseFailure(parser, " " + VALID_NAME_BOB, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_addressWithSlashes_success() {
        for (String address : new String[]{"c/o Mr Lim, Blk 5", "Blk A/B Clementi Ave 3", "N/A"}) {
            Person expectedPerson = new PersonBuilder(BOB).withoutContactDetails().withoutOutstandingFee()
                    .withAddress(address).withTags().build();
            assertParseSuccess(parser, REQUIRED_DESC_BOB + " a/" + address, new AddCommand(expectedPerson));
        }
    }

    @Test
    public void parse_patronymicNames_success() {
        Person expectedPerson = new PersonBuilder(BOB).withoutContactDetails().withoutOutstandingFee()
                .withName("Ravi S/O Raj").withGuardianName("Raj D/O Kumar").withTags().build();
        assertParseSuccess(parser, " n/Ravi S/O Raj g/Raj D/O Kumar" + GUARDIAN_PHONE_DESC_BOB,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_unexpectedText_failure() {
        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + ALL_DESC_BOB, MESSAGE_INVALID_FORMAT);

        // unknown prefix
        assertParseFailure(parser, ALL_DESC_BOB + TAG_DESC_FRIEND, MESSAGE_INVALID_FORMAT);

        // upper-case prefix
        assertParseFailure(parser, " N/Alex Tan" + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB,
                MESSAGE_INVALID_FORMAT);

        // prefix-like text inside a name
        assertParseFailure(parser, " n/Alex x/Tan" + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB,
                MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // blank name
        assertParseFailure(parser, " n/ " + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // invalid guardian name
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_GUARDIAN_NAME_DESC + GUARDIAN_PHONE_DESC_BOB,
                GuardianName.MESSAGE_CONSTRAINTS);

        // invalid guardian phone
        assertParseFailure(parser, NAME_DESC_BOB + GUARDIAN_NAME_DESC_BOB + INVALID_GUARDIAN_PHONE_DESC,
                GuardianPhone.MESSAGE_CONSTRAINTS);

        // guardian phone with a country code other than +65
        assertParseFailure(parser, NAME_DESC_BOB + GUARDIAN_NAME_DESC_BOB + " gp/+60 9123 4567",
                GuardianPhone.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, REQUIRED_DESC_BOB + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, REQUIRED_DESC_BOB + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);

        // blank address
        assertParseFailure(parser, REQUIRED_DESC_BOB + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS);

        // invalid outstanding fee
        assertParseFailure(parser, REQUIRED_DESC_BOB + INVALID_FEE_DESC, OutstandingFee.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + GUARDIAN_NAME_DESC_BOB + GUARDIAN_PHONE_DESC_BOB
                + INVALID_PHONE_DESC, Name.MESSAGE_CONSTRAINTS);
    }
}
