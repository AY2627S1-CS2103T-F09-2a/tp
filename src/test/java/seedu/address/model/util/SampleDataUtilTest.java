package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * Tests the sample students shown on first launch.
 */
public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_returnsSixDistinctStudentsWithGuardians() {
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        assertEquals(6, samplePersons.length);
        assertEquals(6, Arrays.stream(samplePersons).map(Person::getName).distinct().count());
        for (Person person : samplePersons) {
            assertNotNull(person.getGuardianName());
            assertNotNull(person.getGuardianPhone());
        }
        // some sample students carry an outstanding fee, others owe nothing
        long withFee = Arrays.stream(samplePersons).filter(person -> person.getOutstandingFee() != null).count();
        assertTrue(withFee > 0 && withFee < samplePersons.length);
    }

    @Test
    public void getSampleAddressBook_containsExactlyTheSampleStudents() {
        ReadOnlyAddressBook sampleBook = SampleDataUtil.getSampleAddressBook();
        assertTrue(sampleBook instanceof AddressBook);
        assertEquals(Arrays.asList(SampleDataUtil.getSamplePersons()), sampleBook.getPersonList());
    }
}
