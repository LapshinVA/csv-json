import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parceCSV(columnMapping, fileName);
        String json = listToJson(list);
        try (FileWriter writer = new FileWriter("data.csv")) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

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
     * Метод парсит из файла данные в формате CSV в объекты типа {@link Employee}
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
