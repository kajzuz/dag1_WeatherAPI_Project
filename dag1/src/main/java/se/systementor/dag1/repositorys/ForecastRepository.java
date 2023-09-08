package se.systementor.dag1.repositorys;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.models.Forecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository // When spring starts it makes a "map", ohh there is @Autowired let's go there
public interface ForecastRepository extends CrudRepository<Forecast, UUID> {



//    @Query("SELECT f.date, f.hour, AVG(p.temperature) " +
//            "FROM forecast f " +
//            "WHERE " +
//            "   (f.date = CURRENT_DATE AND f.hour <= HOUR(CURRENT_TIME)) " +
//            "   OR (f.date < CURRENT_DATE AND f.datasource = :dateSource) " +
//            "GROUP BY f.date, f.hour " +
//            "ORDER BY " +
//            "   CASE " +
//            "      WHEN f.date = CURRENT_DATE AND f.hour = HOUR(CURRENT_TIME) THEN 0 " +
//            "      ELSE 1 " +
//            "   END, " +
//            "   f.date ASC, " +
//            "   f.hour ASC"
//    )
//
//    List <Object> findAverageByDataSource(@Param("dateSource") DataSource dateSource, @Param("date") LocalDate date);


    @Override
    List<Forecast> findAll();

    List<Forecast> findByDate(LocalDateTime date);



}
