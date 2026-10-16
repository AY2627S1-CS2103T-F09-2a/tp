package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies guardian-only editing, validation and preservation of student data.
 */
public class GuardianEditTest {
    @Test
    public void execute_guardianFields_preservesOtherData() throws Exception {
        Model model = new ModelManager();
        Person original = new PersonBuilder().withoutContactDetails().withOutstandingFee("99.99").build();
        model.addPerson(original);
        new EditCommandParser().parse("1 g/New Guardian gp/98887777").execute(model);
        Person expected = new PersonBuilder(original).withGuardianName("New Guardian")
                .withGuardianPhone("98887777").build();
        assertEquals(expected, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_singleGuardianField_preservesOtherGuardianField() throws Exception {
        Model model = new ModelManager();
        Person original = new PersonBuilder().build();
        model.addPerson(original);
        new EditCommandParser().parse("1 gp/98887777").execute(model);
        assertEquals(new PersonBuilder(original).withGuardianPhone("98887777").build(),
                model.getFilteredPersonList().get(0));
        new EditCommandParser().parse("1 g/New Guardian").execute(model);
        assertEquals(new PersonBuilder(original).withGuardianName("New Guardian")
                .withGuardianPhone("98887777").build(), model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_duplicateGuardianPhone_rejectsWithoutMutation() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withGuardianPhone("91111111").build());
        model.addPerson(new PersonBuilder().withGuardianPhone("92222222").build());
        model.updateFilteredPersonList(person -> person.getGuardianPhone().toString().equals("92222222"));
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> new EditCommandParser().parse("1 gp/91111111").execute(model));
        assertEquals(before, model.getAddressBook());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals("92222222", model.getFilteredPersonList().get(0).getGuardianPhone().toString());
    }

    @Test
    public void execute_filteredIndex_updatesVisibleStudent() throws Exception {
        Model model = new ModelManager();
        Person hidden = new PersonBuilder().withName("Hidden Student").build();
        Person visible = new PersonBuilder().withName("Visible Student").build();
        model.addPerson(hidden);
        model.addPerson(visible);
        model.updateFilteredPersonList(person -> person.equals(visible));
        new EditCommandParser().parse("1 g/Updated Guardian").execute(model);
        assertEquals(hidden, model.getAddressBook().getPersonList().get(0));
        assertEquals(new GuardianName("Updated Guardian"),
                model.getAddressBook().getPersonList().get(1).getGuardianName());
    }

    @Test
    public void parse_invalidOrRepeatedGuardianFields_rejects() {
        EditCommandParser parser = new EditCommandParser();
        for (String input : new String[]{"1 g/", "1 gp/", "1 gp/123", "1 g/123",
            "1 g/First g/Second", "1 gp/91111111 gp/92222222", "1 g/Valid f/10"}) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }

    @Test
    public void descriptor_copyAndEquality_includeGuardianFields() {
        EditPersonDescriptor original = new EditPersonDescriptor();
        original.setGuardianName(new GuardianName("First Guardian"));
        original.setGuardianPhone(new GuardianPhone("91111111"));
        EditPersonDescriptor copy = new EditPersonDescriptor(original);
        assertEquals(original, copy);
        copy.setGuardianName(new GuardianName("Second Guardian"));
        assertNotEquals(original, copy);
        copy = new EditPersonDescriptor(original);
        copy.setGuardianPhone(new GuardianPhone("92222222"));
        assertNotEquals(original, copy);
    }
}
