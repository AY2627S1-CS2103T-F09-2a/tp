package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Checks layout constraints in {@code MainWindow.fxml} without starting JavaFX.
 */
public class MainWindowLayoutTest {

    @Test
    public void resultDisplayPlaceholder_heightNotFixed() throws Exception {
        Element placeholder = findById("resultDisplayPlaceholder");
        assertNotNull(placeholder);
        // An empty attribute means the height is computed from the result display's content.
        assertEquals("", placeholder.getAttribute("prefHeight"));
        assertEquals("", placeholder.getAttribute("maxHeight"));
    }

    private static Element findById(String id) throws Exception {
        try (InputStream fxml = MainWindowLayoutTest.class.getResourceAsStream("/view/MainWindow.fxml")) {
            NodeList elements = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fxml)
                    .getElementsByTagName("*");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                if (id.equals(element.getAttribute("fx:id"))) {
                    return element;
                }
            }
            return null;
        }
    }
}
