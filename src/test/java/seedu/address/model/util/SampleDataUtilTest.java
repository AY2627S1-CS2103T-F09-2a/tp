package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    private static final Set<String> AB3_TAGS = Set.of("friends", "colleagues", "neighbours", "family", "classmates");

    @Test
    public void getSamplePersons_tutorTags_noAddressBookTagsLeft() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        assertEquals(6, samplePersons.length);
        for (Person person : samplePersons) {
            assertFalse(person.getTags().isEmpty());
            for (Tag tag : person.getTags()) {
                assertFalse(AB3_TAGS.contains(tag.tagName), "Sample data still has AB3 tag " + tag.tagName);
            }
        }
    }

    @Test
    public void getSampleAddressBook_allSamplePersonsAdded() {
        assertEquals(SampleDataUtil.getSamplePersons().length,
                SampleDataUtil.getSampleAddressBook().getPersonList().size());
    }
}
