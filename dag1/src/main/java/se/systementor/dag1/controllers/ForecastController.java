package se.systementor.dag1.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.systementor.dag1.dto.ForcastForPostDTO;
import se.systementor.dag1.dto.ForecastListDTO;
import se.systementor.dag1.dto.NewForecastDTO;
import se.systementor.dag1.models.*;
import se.systementor.dag1.services.ForecastService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class ForecastController {

    @Autowired
    ForecastService forecastService;

    /*
    ResponseEntity represents the whole HTTP response: status code, headers, and body*/
    /*@GetMapping("/api/forecasts")
    public ResponseEntity<List<Forecast>> getAll(){
        return new ResponseEntity<>(forecastService.getForecastList(), HttpStatus.OK);
    }*/


    @GetMapping("/api/forecasts")
    public ResponseEntity<List<ForecastListDTO>>getAll(){
        return new ResponseEntity<List<ForecastListDTO>>(forecastService.getForecastList().stream().map(forecast->{
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.setId(forecast.getId());
            forecastListDTO.setDate(forecast.getDate());
            forecastListDTO.setTemperature(forecast.getTemperature());
            forecastListDTO.setHour(forecast.getHour());
            return forecastListDTO;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }


    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> getById(@PathVariable UUID id){ //Extracts the id we want and passes it on to getByid
        Optional<Forecast> forecast = forecastService.getById(id); //Optional better for when only one forecast is returned
        if(forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return  ResponseEntity.notFound().build(); // Same as HTTPStatus.OK, just another way
    }

    /*@PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> update(@PathVariable UUID id, @RequestBody Forecast forecast){ //@Requestbody to convert incoming data to java object
        forecastService.update(forecast);
        return ResponseEntity.ok(forecast);
    }*/

    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> update(@PathVariable UUID id, @RequestBody NewForecastDTO newForecastDto){ //@Requestbody to convert incoming data to java object
        Forecast forecast = new Forecast();
        forecast.setId(id);
        forecast.setDate(newForecastDto.getDate());
        forecast.setTemperature(newForecastDto.getTemperature());
        forecast.setHour(newForecastDto.getHour());
        forecastService.update(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> add(@RequestBody ForcastForPostDTO forcastForPostDTO){
        Forecast forecast = new Forecast();
        forecast.setId(UUID.randomUUID());
        forecast.setTemperature(forcastForPostDTO.getTemperature());
        forecast.setHour(forcastForPostDTO.getHour());
        forecast.setDate(forcastForPostDTO.getDate());
        forecastService.add(forecast);
        return ResponseEntity.ok(forecast);
    }

    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id){
        forecastService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }


    // Get average temperature SMHI Api
    @GetMapping("/api/smhi/averageTempSMHI/{date}")
    public ResponseEntity<Double> getAverageTemperatureSmhiApi() {

        ObjectMapper objectMapper = new ObjectMapper();

        String SMHIUrl="https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
        WeatherSMHI weatherSMHI = null;
        try {
            weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        double totalSum = 0;
        int amountOfNumbers = 0;


        for (TimeSeries timeSeries: weatherSMHI.getTimeSeries()) {

            List<Parameter> amount = timeSeries.getParameters();

            for (Parameter parameter : amount) {

                List<Double> temperature = parameter.getValues();


                for (Double temp: temperature) {
                    totalSum += temp;
                    amountOfNumbers ++;
                }

            }
        }
        double averageTemp = totalSum / amountOfNumbers;



        return ResponseEntity.ok(averageTemp);
    }



    // Get average temperature OpenMeteo Api
    @GetMapping("/api/OpenMeteo/averageTempOpenMeteo/{date}")
    public ResponseEntity<Double> getAverageTemperatureOpenMeteoApi() {
        ObjectMapper objectMapper = new ObjectMapper();

        String openMeteoApiUrl = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&past_days=1&hourly=temperature_2m,relativehumidity_2m,windspeed_10m";
        WeatherOpenMeteo weatherOpenMeteo = null;
        try {
            weatherOpenMeteo = objectMapper.readValue(new URL(openMeteoApiUrl), WeatherOpenMeteo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        double totalSum = 0;


        HourlyOpenMeteo hourlyOpenMeteo2 = weatherOpenMeteo.getHourly();

        List<Double> amount = hourlyOpenMeteo2.getTemperature_2m();

        for (Double allTemps : amount) {

            totalSum += allTemps;

        }

        int	amountOfNumbers = amount.size();
        double averageTemp = totalSum / amountOfNumbers;


        return ResponseEntity.ok(averageTemp);
    }




    // Get average temperature Json list
    @GetMapping("/api/forecasts/averageTemp/{date}")
    public ResponseEntity<Double> getAverageTemperature(@PathVariable String date) {
        List<Forecast> forecastDates = forecastService.getByDate(date);

        double totalSum = 0;

        if (forecastDates.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        for (Forecast forecast : forecastDates) {
            totalSum += forecast.getTemperature();
        }

        double averageTemp = totalSum / forecastDates.size();


        return ResponseEntity.ok(averageTemp);
    }


    // Get temperature
    @GetMapping("/api/forecasts/date/{date}")
    public ResponseEntity<List<Forecast>> getByDate(@PathVariable String date){
        List<Forecast> forecast = forecastService.getByDate(date);

        if (!forecast.isEmpty()) {return ResponseEntity.ok(forecast);
        } else {return ResponseEntity.notFound().build();
        }
    }


}
