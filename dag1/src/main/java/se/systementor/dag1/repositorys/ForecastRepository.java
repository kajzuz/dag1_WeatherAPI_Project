package se.systementor.dag1.repositorys;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.systementor.dag1.models.Forecast;

import java.util.List;
import java.util.UUID;

@Repository // When spring starts it makes a "map", ohh there is @Autowired let's go there
public interface ForecastRepository extends CrudRepository<Forecast, UUID> {

    @Override
    List<Forecast> findAll();

    //List<Forecast> findAllByPredictionDatum();

}
