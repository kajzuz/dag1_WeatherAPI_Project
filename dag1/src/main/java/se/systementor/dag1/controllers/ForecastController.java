package se.systementor.dag1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.dto.ForcastForPostDTO;
import se.systementor.dag1.dto.ForecastListDTO;
import se.systementor.dag1.dto.NewForecastDTO;
import se.systementor.dag1.models.*;
import se.systementor.dag1.repositorys.ForecastRepository;
import se.systementor.dag1.services.ForecastService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ForecastController {

    @Autowired
    ForecastService forecastService;

//    @Autowired
//    ForecastRepository forecastRepository;


    @GetMapping("/api/forecasts")
    public ResponseEntity<List<ForecastListDTO>> getAll() {
        return new ResponseEntity<List<ForecastListDTO>>(forecastService.getForecastList().stream().map(forecast -> {
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.setId(forecast.getId());
            forecastListDTO.setDate(forecast.getDate());
            forecastListDTO.setTemperature(forecast.getTemperature());
            forecastListDTO.setHour(forecast.getHour());
            return forecastListDTO;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }


    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> getById(@PathVariable UUID id) { //Extracts the id we want and passes it on to getByid
        Optional<Forecast> forecast = forecastService.getById(id); //Optional better for when only one forecast is returned
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build(); // Same as HTTPStatus.OK, just another way
    }


    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> update(@PathVariable UUID id, @RequestBody NewForecastDTO newForecastDto) { //@Requestbody to convert incoming data to java object
        Forecast forecast = new Forecast();
        forecast.setId(id);
        forecast.setDate(newForecastDto.getDate());
        forecast.setTemperature(newForecastDto.getTemperature());
        forecast.setHour(newForecastDto.getHour());
        forecastService.update(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> add(@RequestBody ForcastForPostDTO forcastForPostDTO) {
        Forecast forecast = new Forecast();
        forecast.setId(UUID.randomUUID());
        forecast.setTemperature(forcastForPostDTO.getTemperature());
        forecast.setHour(forcastForPostDTO.getHour());
        forecast.setDate(forcastForPostDTO.getDate());
        forecastService.add(forecast);
        return ResponseEntity.ok(forecast);
    }

    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        forecastService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }



    // Get temperature
    @GetMapping("/api/forecasts/date/{date}")
    public ResponseEntity<List<Forecast>> getByDate(@PathVariable LocalDate date) {
        List<Forecast> forecast = forecastService.getByDate(date);

        if (!forecast.isEmpty()) {
            return ResponseEntity.ok(forecast);
        } else {
            return ResponseEntity.notFound().build();
        }
    }






     //Average temperature from database with input predictions and smhi api together
    @GetMapping("/api/average/{date}")
    public ResponseEntity<ArrayList<Map.Entry<LocalDateTime, Double>>> getAverageTemperature(@PathVariable LocalDate date) {
        List<Forecast> forecast = forecastService.getAverageTemperature(date);

        if (forecast.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        //  Map to store hourly average temperatures as well as same temperature same hour
        Map<LocalDateTime, Double> averageHourTemperatures = new HashMap<>();
        Map<LocalDateTime, Integer> temperatureCountsAHour = new HashMap<>();

        for (Forecast forecasts : forecast) {
            //int hour = forecast.getHour();
            LocalDateTime hours = LocalDateTime.of(date, LocalTime.of(forecasts.getHour(), 0));
            double temperature = forecasts.getTemperature();

            // Adding temperatures and counts per hour
            averageHourTemperatures.merge(hours, temperature, Double::sum);
            temperatureCountsAHour.merge(hours, 1, Integer::sum);
        }


        // Calculates the average temperature every hour
        // totalTemp stores the amount of temperatures there is for a specific hour
        averageHourTemperatures.forEach((hours, totalTemp) -> {
            int count = temperatureCountsAHour.get(hours);
            if (count > 0) {
                averageHourTemperatures.put(hours, totalTemp / count); // ads the hours(date) and the average temp per hour in the map
            }
        });

        // Making my HashMap to a Arraylist so it's able to be sorted
        ArrayList<Map.Entry <LocalDateTime, Double>> sortedList = new ArrayList<>(averageHourTemperatures.entrySet());
        sortedList.sort(Map.Entry.comparingByKey());


        return ResponseEntity.ok(sortedList);
    }




    // Make this with Stefan on Monday
    /*@GetMapping("/api/forecasts/average/{date}")
    public ResponseEntity<List<ForcastAverageTempDTO>> average(@PathVariable LocalDate date) {
        List<Forecast> forecast = forecastService.average(date);

        ForcastAverageTempDTO forcastAverageTempDTO;

        List <Float> temperatures = new ArrayList<>();
        List <Integer> hours = new ArrayList<>();


        for (Forecast forecast1 : forecast){

            int hour = forecast1.getHour();
            LocalDateTime localDateTime = forecast1.getDate();
            Float temperature = forecast1.getTemperature();

            int sum = 0;
            for (Float temperature : temperatureList){

                sum += temperatures;

            }

            float averageTemp = sum / temperatures.size();

            if (hour == forcastAverageTempDTO.getHour() && localDateTime.equals(forcastAverageTempDTO.getDate())){

                temperatures ++;

                temperatures.add(hours, temperature / temperatures);

            }

            ForcastAverageTempDTO forcastAverageTempDTO = new ForcastAverageTempDTO();
            forcastAverageTempDTO.getAverageTemp();
            forcastAverageTempDTO.getHour();

        }


        if (forecast.isEmpty()) {
            return ResponseEntity.notFound().build(); // make the average here
      }
        return ResponseEntity.ok(averageTemp);

    }*/




    // Filtering by dataSource and date
    @GetMapping("/api/average/{dataSource}/{date}")
    public ResponseEntity<List<Object>> dataSourceAverage
            (@PathVariable("dataSource") DataSource dataSource,
             @PathVariable("date") LocalDate date) {


        List<Object> averageDataSource = forecastService.dataSourceAverage(dataSource, date);



        if (!averageDataSource.isEmpty()) {
            return ResponseEntity.ok(averageDataSource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
