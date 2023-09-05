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


    public ForecastService(){



    }



    public List <Forecast> getForecastList(){
        return forecastRepository.findAll();
        //return forecastList;
    }

    public void add(Forecast forecast)
    {
        forecastRepository.save(forecast);


    }

    public void update(Forecast forecast) { // Middle hand for database
        forecastRepository.save(forecast);
    }

    public void delete(Forecast forecast){

        forecastRepository.deleteById(forecast.getId());
    }

    public void deleteById(UUID id){

        forecastRepository.deleteById(id);
    }


    //Get by id
    public Optional<Forecast> getById(UUID id){
        return forecastRepository.findById(id);
    }






    // Get average temperature SMHI API, not in use right now
    /*public List<Forecast> getAverageTemperatureSmhiApi(String date){
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
    }*/


}
