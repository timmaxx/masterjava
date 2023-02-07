package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JaxbParserTest {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml")
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(strPayload);
        System.out.println(strPayload);
    }

    @Test
    public void testCity() throws Exception {
        JAXBElement<CityType> cityElement = JAXB_PARSER.unmarshal(
                Resources.getResource("city.xml").openStream());
        CityType city = cityElement.getValue();
        JAXBElement<CityType> cityElement2 =
                new JAXBElement<>(new QName("http://javaops.ru", "City"), CityType.class, city);
        String strCity = JAXB_PARSER.marshal(cityElement2);
        JAXB_PARSER.validate(strCity);
        System.out.println(strCity);
    }

/*  // Тест не проходит. Ломается на той строке, перед которой комментарий с ошибкой.
    @Test
    public void testUsersFromPayload() throws Exception {
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(strPayload);

        for (User user: payload.getUsers().getUser()) {
            // Caused by: org.xml.sax.SAXParseException; lineNumber: 0; columnNumber: 0; cvc-id.1: There is no ID/IDREF binding for IDREF 'kiv'.
            String strUser = JAXB_PARSER.marshal(user);
            System.out.println(strUser);
        }
    }
*/
    @Test
    public void testProjectsFromPayload() throws Exception {
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(strPayload);

        for (Project proj: payload.getProjects().getProject()) {
            String strProject = JAXB_PARSER.marshal(proj);
            System.out.println(strProject);
        }
    }
}