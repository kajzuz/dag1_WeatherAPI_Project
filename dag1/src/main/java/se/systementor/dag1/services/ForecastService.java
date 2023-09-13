package se.systementor.dag1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.dto.ForcastAverageTempDTO;
import se.systementor.dag1.models.Forecast;

import se.systementor.dag1.repositorys.ForecastRepository;

import java.time.LocalDate;
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




    // Get by date
    public List<Forecast> getByDate(LocalDate date) {

        return forecastRepository.findByDate(date.atStartOfDay());

    }



//    public List<Forecast> getAverageTemperature(LocalDate date){
//        return getForecastList()
//                .stream()
//                .filter(forecast -> {
//                    LocalDate forecastDate = forecast.getDate().toLocalDate();
//                    return forecastDate.isEqual(date);
//                })
//                .collect(Collectors.toList());
//
//    }


    // Get average temp every hour
    public List<ForcastAverageTempDTO> average(LocalDate date){

       var resultList = new ArrayList<ForcastAverageTempDTO>();


       var allPredictionsForDay = forecastRepository.findAllByDate(date.atStartOfDay());

       for (int time = 0; time <= 23; time ++){
           var forecastAverageTempDTO = new ForcastAverageTempDTO();
           forecastAverageTempDTO.setHour(time);
           forecastAverageTempDTO.setDate(date);
           float amount = 0;
           float sum = 0;

           for (var forecast : allPredictionsForDay){

               if (forecast.getHour() == time){
                   amount++;
                   sum += forecast.getTemperature();
               }

           }
           if (amount > 0){

               forecastAverageTempDTO.setAverageTemp(sum / amount);

               resultList.add(forecastAverageTempDTO);

           }

       }

        return resultList;
    }




    public List<Map<String, Object>> dataSourceAverage(DataSource dataSource, LocalDate date){

        List<Object[]> averageDataSource = forecastRepository.findAverageByDataSource(dataSource, date.atStartOfDay());

        List<Map<String, Object>> arrayWithObjectsList = averageDataSource.stream().map(index -> {
            Map<String,Object> map = new HashMap<>();

            map.put("id",index[0]);
            map.put("date",index[1]);
            map.put("hour",index[2]);
            map.put("temperature",index[3]);
            map.put("dataSource",index[4]);

            return map;

        }).collect(Collectors.toList());


        return arrayWithObjectsList;

    }

}
