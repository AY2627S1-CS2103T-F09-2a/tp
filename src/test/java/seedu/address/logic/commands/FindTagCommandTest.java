package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.testutil.PersonBuilder;

public class FindTagCommandTest {
    @Test
    public void execute_multipleTags_filtersFullListWithoutChangingRecords() throws Exception {
        Person alice = new PersonBuilder().withName("Alice").withTags("friends").build();
        Person bob = new PersonBuilder().withName("Bob").withTags("colleagues", "friends").build();
        Person carl = new PersonBuilder().withName("Carl").withTags("family").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(alice);
        addressBook.addPerson(bob);
        addressBook.addPerson(carl);
        Model model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredPersonList(person -> person.equals(carl));

        Command command = new AddressBookParser().parseCommand("findtag FRIENDS colleagues");
        CommandResult result = command.execute(model);

        assertEquals(List.of(alice, bob), model.getFilteredPersonList());
        assertEquals(List.of(alice, bob, carl), model.getAddressBook().getPersonList());
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        new ListCommand().execute(model);
        assertEquals(List.of(alice, bob, carl), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatch_returnsEmptyList() {
        Model model = new ModelManager();
        CommandResult result = new FindTagCommand(new TagContainsKeywordsPredicate(List.of("missing"))).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());
    }

    @Test
    public void equals_comparesPredicates() {
        FindTagCommand command = new FindTagCommand(new TagContainsKeywordsPredicate(List.of("friends")));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new FindTagCommand(new TagContainsKeywordsPredicate(List.of("friends")))));
        assertFalse(command.equals(new FindTagCommand(new TagContainsKeywordsPredicate(List.of("family")))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("friends"));
    }
}
