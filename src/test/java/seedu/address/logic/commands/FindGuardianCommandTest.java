package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.FindGuardianCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests guardian searching across the full stored list.
 */
public class FindGuardianCommandTest {
    @Test
    public void execute_nameOrPhone_matchesWholeWordsAndAllStudents() throws Exception {
        Model model = new ModelManager();
        Person first = new PersonBuilder().withName("First Student").withGuardianName("Mei Tan")
                .withGuardianPhone("91111111").build();
        Person second = new PersonBuilder().withName("Second Student").withGuardianName("Meiling Lee")
                .withGuardianPhone("92222222").build();
        Person third = new PersonBuilder().withName("Third Student").withGuardianName("Other Guardian")
                .withGuardianPhone("93333333").build();
        model.addPerson(first);
        model.addPerson(second);
        model.addPerson(third);
        model.updateFilteredPersonList(person -> false);
        AddressBook before = new AddressBook(model.getAddressBook());
        new FindGuardianCommandParser().parse(" mEI 93333333 ").execute(model);
        assertEquals(List.of(first, third), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        new FindGuardianCommandParser().parse("92222222").execute(model);
        assertEquals(List.of(second), model.getFilteredPersonList());
        new FindGuardianCommandParser().parse("Nobody").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(first, second, third), model.getFilteredPersonList());
    }

    @Test
    public void parse_missingOrInvalidKeywords_rejects() {
        FindGuardianCommandParser parser = new FindGuardianCommandParser();
        for (String input : new String[]{"", "  ", "123", "9111", "g/Mei", "Mei !", "911111111"}) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }

    @Test
    public void predicate_defensiveCopyAndUnicode_matchesOriginalKeywords() throws Exception {
        List<String> keywords = new ArrayList<>(List.of("Mei"));
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(keywords);
        keywords.clear();
        assertEquals(true, predicate.test(new PersonBuilder().withGuardianName("Mei Tan").build()));
        Model model = new ModelManager();
        Person person = new PersonBuilder().withGuardianName("José Tan").build();
        model.addPerson(person);
        FindGuardianCommand command = new FindGuardianCommandParser().parse("Jose\u0301");
        command.execute(model);
        assertEquals(List.of(person), model.getFilteredPersonList());
        assertFalse(command.shouldSaveAddressBook());
    }
}
