package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_validData_returnsSixPersonsWithTutorTags() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        assertEquals(6, samplePersons.length);
        for (Person person : samplePersons) {
            assertNotNull(person);
            assertNotNull(person.getName());
            assertNotNull(person.getGuardianName());
            assertNotNull(person.getGuardianPhone());
            assertTrue(person.getTags().stream().allMatch(t -> Tag.isValidTagName(t.tagName)));
        }
    }

    @Test
    public void getSampleAddressBook_validData_containsAllSamplePersons() {
        ReadOnlyAddressBook addressBook = SampleDataUtil.getSampleAddressBook();
        assertEquals(6, addressBook.getPersonList().size());
    }

    @Test
    public void getTagSet_validStrings_returnsTagSet() {
        assertEquals(2, SampleDataUtil.getTagSet("Sec3", "Math").size());
        assertTrue(SampleDataUtil.getTagSet("Sec3", "Math").contains(new Tag("Sec3")));
        assertTrue(SampleDataUtil.getTagSet("Sec3", "Math").contains(new Tag("Math")));
    }
}
