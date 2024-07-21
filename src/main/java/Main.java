import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameJson1 = "data.json";
        String fileNameJson2 = "data2.json";

        List<Employee> list = parceCSV(columnMapping, fileNameCsv);
        String json1 = listToJson(list);
        writeString(json1, fileNameJson1);

        List<Employee> list1 = parseXML("data.xml");
        String json2 = listToJson(list1);
        writeString(json2, fileNameJson2);

    }

    private static void writeString(String str, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод парсит из файла данные в формате XML в список объектов типа {@link Employee}
     *
     * @param fileName - имя файла
     * @return - список объектов типа {@link Employee}
     */
    private static List<Employee> parseXML(String fileName) {
        List<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node root = document.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    NodeList childNodes2 = node.getChildNodes();
                    Element element2 = (Element) node;
                    long id = Long.parseLong(element2.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element2.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element2.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element2.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element2.getElementsByTagName("age").item(0).getTextContent());

                    list.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Метод сериализует список объектов {@link Employee}, в эквивалентное представление JSON
     *
     * @param list - список объектов {@link Employee} для которых необъодимо создать представление JSON
     * @return JSON-представление
     */
    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    /**
     * Метод парсит из файла данные в формате CSV в список объектов типа {@link Employee}
     *
     * @param columnMapping - порядок расположения полей
     * @param fileName      - имя файла
     * @return - Список объектов типа {@link Employee}
     */
    private static List<Employee> parceCSV(String[] columnMapping, String fileName) {
        List<Employee> list;
        try (FileReader reader = new FileReader(fileName)) {
            CSVReader csvReader = new CSVReader(reader);
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
