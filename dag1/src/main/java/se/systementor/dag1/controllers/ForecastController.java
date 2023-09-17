package se.systementor.dag1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.dto.*;
import se.systementor.dag1.models.*;
import se.systementor.dag1.repositorys.ForecastRepository;
import se.systementor.dag1.services.ForecastService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ForecastController {

    @Autowired
    ForecastService forecastService;



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
        var forecast = forecastService.getById(id).get(); //getting the object from db, so all properties is set
        forecast.setId(id);
        forecast.setUpdated(LocalDateTime.now());
        forecast.setDate(newForecastDto.getDate());
        forecast.setTemperature(newForecastDto.getTemperature());
        forecast.setHour(newForecastDto.getHour());
        forecast.setDataSource(newForecastDto.getDataSource());
        forecast.setRainOrSnow(newForecastDto.getRainOrSnow());
        forecastService.update(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> add(@RequestBody ForcastForPostDTO forcastForPostDTO) {
        Forecast forecast = new Forecast();
        forecast.setCreated(LocalDateTime.now());
        forecast.setTemperature(forcastForPostDTO.getTemperature());
        forecast.setHour(forcastForPostDTO.getHour());
        forecast.setDate(forcastForPostDTO.getDate());
        forecast.setDataSource(forcastForPostDTO.getDataSource());
        forecast.setRainOrSnow(forcastForPostDTO.getRainOrSnow());
        forecastService.add(forecast);
        return ResponseEntity.ok(forecast);
    }

    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        forecastService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }




    @GetMapping("/api/forecasts/date/{date}")
    public ResponseEntity<List<Forecast>> getByDate(@PathVariable LocalDate date) {
        List<Forecast> forecast = forecastService.getByDate(date);

        if (!forecast.isEmpty()) {
            return ResponseEntity.ok(forecast);
        } else {
            return ResponseEntity.notFound().build();
        }
    }






    // Get average temp every hour
    @GetMapping("/api/forecasts/average/{date}")
    public ResponseEntity<List<ForcastAverageTempDTO>> average(@PathVariable LocalDate date) {

        List<ForcastAverageTempDTO> forecast = forecastService.average(date);


        if (forecast.isEmpty()) {
            return ResponseEntity.notFound().build();
      }
        return ResponseEntity.ok(forecast);

    }




    // Filtering by dataSource and date
    @GetMapping("/api/forecasts/average/{dataSource}/{date}")
    public ResponseEntity <List<Map<String, Object>>> dataSourceAverage
            (@PathVariable("dataSource") DataSource dataSource,
             @PathVariable("date") LocalDate date) {


        List<Map<String, Object>> averageDataSource = forecastService.dataSourceAverage(dataSource, date);



        if (!averageDataSource.isEmpty()) {
            return ResponseEntity.ok(averageDataSource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
