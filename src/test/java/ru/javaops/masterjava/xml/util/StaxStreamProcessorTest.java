package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class StaxStreamProcessorTest {
    @Test
    public void readCities() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("City".equals(reader.getLocalName())) {
                        System.out.println(reader.getElementText());
                    }
                }
            }
        }
    }

    @Test
    public void readCities2() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            String city;
            while ((city = processor.getElementValue("City")) != null) {
                System.out.println(city);
            }
        }
    }

    // Есть дублирование вложенного кода.
    @Test
    public void readUsers() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("User".equals(reader.getLocalName())) {
                        while (reader.hasNext()) {
                            int event2 = reader.next();
                            if (event2 == XMLEvent.START_ELEMENT) {
                                if ("email".equals(reader.getLocalName())) {
                                    System.out.println(reader.getElementText());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Убрал дублирование вложенного кода.
    @Test
    public void readUsers2() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            while ((processor.doUntil(XMLEvent.START_ELEMENT, "User"))) {
                while ((processor.doUntil(XMLEvent.START_ELEMENT, "email"))) {
                    System.out.println(processor.getText());
                }
            }
        }
    }

    @Test
    public void readProjects2() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            while ((processor.doUntil(XMLEvent.START_ELEMENT, "Project"))) {
                while ((processor.doUntil(XMLEvent.START_ELEMENT, "name"))) {
                    System.out.println(processor.getText());
                }
            }
        }
    }
}