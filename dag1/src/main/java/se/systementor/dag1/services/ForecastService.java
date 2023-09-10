package se.systementor.dag1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.models.Forecast;

import se.systementor.dag1.repositorys.ForecastRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ForecastService {

    @Autowired
    ForecastRepository forecastRepository;


    public ForecastService() {


    }


    public List<Forecast> getForecastList() {
        return forecastRepository.findAll();
        //return forecastList;
    }

    public void add(Forecast forecast) {
        forecastRepository.save(forecast);


    }

    public void update(Forecast forecast) { // Middle hand for database
        forecastRepository.save(forecast);
    }

    public void delete(Forecast forecast) {

        forecastRepository.deleteById(forecast.getId());
    }

    public void deleteById(UUID id) {

        forecastRepository.deleteById(id);
    }


    //Get by id
    public Optional<Forecast> getById(UUID id) {
        return forecastRepository.findById(id);
    }



    // Get average temperature OpenMeteo API
    /*public List<Forecast> getAverageTemperatureOpenMeteoApi(String date){
        return getForecastList().stream().filter(forecast -> forecast.getDate().equals(date)).collect(Collectors.toList());

    }*/



    // Get by date
    public List<Forecast> getByDate(LocalDate date) {

        return forecastRepository.findByDate(date.atStartOfDay());

    }

    public List<Forecast> getAverageTemperature(LocalDate date){
        return getForecastList()
                .stream()
                .filter(forecast -> {
                    LocalDate forecastDate = forecast.getDate().toLocalDate();
                    return forecastDate.isEqual(date);
                })
                .collect(Collectors.toList());


        //return forecastRepository.findAverageTemperature(LocalDateTime.from(date));
    }


    // Make this work with Stefan
    /*public List<Forecast> average(LocalDate date){
        return getForecastList()
                .stream()
                .filter(forecast -> {
                    LocalDate forecastDate = forecast.getDate().toLocalDate();
                    return forecastDate.isEqual(date);
                })
                .sorted(Comparator.comparing(forecast -> forecast.getHour()))
                .collect(Collectors.toList());


        //return forecastRepository.findAverageTemperature(LocalDateTime.from(date));
    }*/




    public List<Object> dataSourceAverage(DataSource dataSource, LocalDate date){

        return forecastRepository.findAverageByDataSource(dataSource, date.atStartOfDay());
    }

}
