package se.systementor.dag1.repositorys;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.systementor.dag1.models.Forecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository // When spring starts it makes a "map", ohh there is @Autowired let's go there
public interface ForecastRepository extends CrudRepository<Forecast, UUID> {


    @Override
    List<Forecast> findAll();

    List<Forecast> findByDate(LocalDateTime date);

    //Maybe has to be of type map?
    //List<Forecast> findAverageTemperature(LocalDateTime date);



}
