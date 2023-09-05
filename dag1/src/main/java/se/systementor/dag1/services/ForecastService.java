package se.systementor.dag1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.systementor.dag1.models.Forecast;

import se.systementor.dag1.repositorys.ForecastRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ForecastService {

    @Autowired
    private ForecastRepository forecastRepository;
    //private static List<Forecast> forecastList = new ArrayList<>();

    public ForecastService(){
        /*try {
            forecastList = readFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }




    /*private List<Forecast> readFromFile() throws IOException {
        if(!Files.exists(Path.of("Predictions.JSON"))) return new ArrayList<Forecast>();
        ObjectMapper objectMapper = getObjectMapper();
        var jsonStr = Files.readString(Path.of("Predictions.JSON"));
        return  new ArrayList(Arrays.asList(objectMapper.readValue(jsonStr, Forecast[].class )));
    }


    private void writeAllToFile(List<Forecast> weatherPredictions) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, weatherPredictions);

        Files.writeString(Path.of("Predictions.JSON"), stringWriter.toString());

    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }*/



    public List <Forecast> getForecastList(){
        return forecastRepository.findAll();
        //return forecastList;
    }

    public void add(Forecast forecast)
    {
        forecastRepository.save(forecast);

        /*forecastList.add(forecast);
        try {
            writeAllToFile(forecastList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void update(Forecast forecast) { // Middle hand for database
        forecastRepository.save(forecast);
        /*try {
            var foreCastInList = getById(forecast.getId()).get();
            foreCastInList.setTemperature(forecast.getTemperature());
            foreCastInList.setDate(forecast.getDate());
            foreCastInList.setHour(forecast.getHour());
            writeAllToFile(forecastList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void delete(Forecast forecast){

        forecastRepository.deleteById(forecast.getId());

        /*forecastList.remove(forecast);
        try {
            writeAllToFile(forecastList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void deleteById(UUID id){

        forecastRepository.deleteById(id);
       /* Forecast forecast = getById(id).get();
        forecastList.remove(forecast);
        try {
            writeAllToFile(forecastList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }




    // Get average temperature SMHI API
    public List<Forecast> getAverageTemperatureSmhiApi(String date){
        return getForecastList().stream().filter(forecast -> forecast.getDate().equals(date)).collect(Collectors.toList());

    }

    // Get average temperature OpenMeteo API
    public List<Forecast> getAverageTemperatureOpenMeteoApi(String date){
        return getForecastList().stream().filter(forecast -> forecast.getDate().equals(date)).collect(Collectors.toList());

    }


    // Get average temperature json list
   public List<Forecast> getAverageTemperature(String date){
        return getForecastList().stream().filter(forecast -> forecast.getDate().equals(date)).collect(Collectors.toList());

   }

    // Get by date
    public List<Forecast> getByDate(String date){
        return getForecastList().stream().filter(forecast -> forecast.getDate().equals(date)).collect(Collectors.toList());
    }

    //Get by id
    public Optional<Forecast> getById(UUID id){
        return forecastRepository.findById(id);
        //return getForecastList().stream().filter(forecast -> forecast.getId().equals(id)).findFirst();
    }
}
