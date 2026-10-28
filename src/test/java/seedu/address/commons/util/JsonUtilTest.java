package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void fromJsonString_whitespaceAfterValue_success() throws IOException {
        String json = SerializableTestClass.JSON_STRING_REPRESENTATION + " \t\r\n\n";

        SerializableTestClass serializableTestClass = JsonUtil.fromJsonString(json, SerializableTestClass.class);

        assertEquals(SerializableTestClass.getNameTestValue(), serializableTestClass.getName());
    }

    @Test
    public void fromJsonString_secondValueAfterValue_throwsJsonProcessingException() {
        for (String secondValue : new String[] {"\n{ \"name\" : \"Second\" }", "null", "[ ]"}) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + secondValue;

            JsonProcessingException exception = assertThrows(JsonProcessingException.class, () ->
                    JsonUtil.fromJsonString(json, SerializableTestClass.class));
            assertEquals(JsonUtil.MESSAGE_CONTENT_AFTER_VALUE, exception.getOriginalMessage());
        }
    }

    @Test
    public void fromJsonString_invalidContentAfterValue_throwsIoException() {
        for (String trailingContent : new String[] {"GARBAGE", "}", ","}) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + trailingContent;
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json, SerializableTestClass.class),
                    "Content after the value should be rejected: " + trailingContent);
        }
    }

    @Test
    public void fromJsonString_unknownField_ignoredOnRead() throws IOException {
        String jsonWithUnknown = "{ \"name\" : \"Some Name\", \"unknownField\" : \"extra data\" }";
        SerializableTestClass result = JsonUtil.fromJsonString(jsonWithUnknown, SerializableTestClass.class);
        assertEquals("Some Name", result.getName());
    }

    @Test
    public void fromJsonString_duplicateField_throwsIoException() {
        String duplicateTopLevelField = "{ \"name\" : \"First\", \"name\" : \"Second\" }";
        String duplicateNestedField = "{ \"mapOfIntegerToString\" : { \"1\" : \"One\", \"1\" : \"Uno\" } }";
        String duplicateFieldInUnknownField = "{ \"unknown\" : { \"a\" : 1, \"a\" : 2 } }";

        assertThrows(IOException.class, () ->
                JsonUtil.fromJsonString(duplicateTopLevelField, SerializableTestClass.class));
        assertThrows(IOException.class, () ->
                JsonUtil.fromJsonString(duplicateNestedField, SerializableTestClass.class));
        assertThrows(IOException.class, () ->
                JsonUtil.fromJsonString(duplicateFieldInUnknownField, SerializableTestClass.class));
    }

    //TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
