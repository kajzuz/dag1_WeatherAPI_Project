package se.systementor.dag1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se.systementor.dag1.dataSource.DataSource;
import se.systementor.dag1.models.*;
import se.systementor.dag1.openMeteo.HourlyOpenMeteo;
import se.systementor.dag1.openMeteo.HourlyUnitsOpenMeteo;
import se.systementor.dag1.openMeteo.WeatherOpenMeteo;
import se.systementor.dag1.repositorys.ForecastRepository;
import se.systementor.dag1.services.ForecastService;
import se.systementor.dag1.smhi.Geometry;
import se.systementor.dag1.smhi.Parameter;
import se.systementor.dag1.smhi.TimeSeries;
import se.systementor.dag1.smhi.WeatherSMHI;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootApplication
public class Dag1Application implements CommandLineRunner {

	// Abstraction, we use it without knowing what it does

	@Autowired // "newar" for us when compiled
	private ForecastService forecastService;

	@Autowired
	private ForecastRepository forecastRepository;


	public static void main(String[] args) {

		SpringApplication.run(Dag1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mainMenu();

	}


	private void mainMenu() {

		// Do not use (new) more than you have to
		var scanner = new Scanner(System.in); // var, changes to scanner when compiled


		try {
			boolean trueOrFalse = true;


			while (trueOrFalse) {
				System.out.println("\n1. List all predictions");
				System.out.println("2. Create prediction");
				System.out.println("3. Update prediction");
				System.out.println("4. Delete prediction");
				System.out.println("5. SMHI API Data");
				System.out.println("6. Open Meteo API Data");
				System.out.println("0. Quit\n");
				System.out.println("Action: ");

				int menuOption = scanner.nextInt();


				switch (menuOption) { //Make this switch "enhanced" when done
					case 1:
						listPredictions();
						break;

					case 2:
						addPrediction(scanner);
						break;

					case 3:
						updatePrediction(scanner);
						break;

					case 4:
						deletePrediction(scanner);
						break;

					case 5:
						SMHIApiData();
						break;

					case 6:
						OpenMeteoAPIData();
						break;

					case 0:
						System.out.println("Exiting...");
						trueOrFalse = false;
						break;

					default:
						System.out.println("Please, only enter valid options!\nTry again!");

				}


			}
		} catch (Exception e) {
			System.out.println("Not valid format!");

		}


	}


	private void listPredictions() {


		int number = 1;

		for (var forecast : forecastService.getForecastList()) {


			System.out.printf("\n" + number + ") ID: %s\nDate: %s\nHour: %d:00\nTemperature: %f %n\n",
					forecast.getId(),
					forecast.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
					forecast.getHour(),
					forecast.getTemperature());

			number++;

		}

	}


	private void addPrediction(Scanner scanner) { // Console is Provider 2


		System.out.println("<-- Create Prediction -->");
		System.out.println("State day, in following format (yyyy-MM-dd): ");
		String day = scanner.next();


		DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateFormatted = LocalDate.parse(day, date);


		System.out.println("Hour: ");
		int hour = scanner.nextInt();
		System.out.println("Temperature: ");
		float temp = scanner.nextFloat();

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setDate(dateFormatted.atStartOfDay());
		forecast.setHour(hour);
		forecast.setTemperature(temp);
		forecast.setCreated(LocalDateTime.now());


		forecast.setLatitude((float) 59.30996552541549);
		forecast.setLongitude((float) 18.02151508449004);



		if (temp < 10){
			forecast.setRainOrSnow(true);
		}else {
			forecast.setRainOrSnow(false);
		}


		forecast.setDataSource(DataSource.Console);


		forecastService.add(forecast);




	}


	private void updatePrediction(Scanner scanner) {

		int number = 1;

		for (var forecast : forecastService.getForecastList()) {


			System.out.printf("\n" + number + ") ID: %s\nDate: %s\nHour: %d:00\nTemperature: %f %n\n",
					forecast.getId(),
					forecast.getDate(),
					forecast.getHour(),
					forecast.getTemperature());
			number++;
		}
		System.out.print("Select one of the following row numbers you want to UPDATE: \n");
		int rowNumber = scanner.nextInt();
		var forecast = forecastService.getForecastList().get(rowNumber - 1);


		System.out.print("Add new temperature: ");
		int newTemp = scanner.nextInt();

		forecast.setTemperature((float) newTemp);

		System.out.println("New temp added!");

		if (newTemp < 10){
			forecast.setRainOrSnow(true);
		}else {
			forecast.setRainOrSnow(false);
		}

		forecast.setUpdated(LocalDateTime.now());

		forecastService.update(forecast); // Database use

	}


	private void deletePrediction(Scanner scanner) {

		System.out.println("Select one of the following row numbers you want to DELETE: \n ");
		var deleteRow = scanner.nextInt();
		var forecast = forecastService.getForecastList().get(deleteRow - 1);
		forecastService.delete(forecast);
	}



	private void SMHIApiData() { // SMHI API is Provider 1


		ObjectMapper objectMapper = new ObjectMapper();

		Calendar calendar = Calendar.getInstance();


		String SMHIUrl = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
		WeatherSMHI weatherSMHI = null;
		try {
			weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		var scanner = new Scanner(System.in);
		System.out.println("Enter date (yyyy-MM-dd): ");
		String userInput = scanner.next();


		System.out.println("----------------------------------------------------");

		System.out.println("ApprovedTime: " + weatherSMHI.getApprovedTime());
		System.out.println("ReferenceTime: " + weatherSMHI.getReferenceTime());
		System.out.println("Geometry: " + weatherSMHI.getGeometry());

		System.out.println("----------------------------------------------------");


		for (TimeSeries timeSeries : weatherSMHI.getTimeSeries()) { // Limit results to a day
			//String validTime = String.valueOf(timeSeries.getValidTime());

			LocalDate dateFormatted = convertToLocalDateViaInstant(timeSeries.getValidTime());


			Date validTime = timeSeries.getValidTime();

			calendar.setTime(validTime);

			int hour = calendar.get(Calendar.HOUR_OF_DAY);


			LocalDate inputDate = LocalDate.parse(userInput);



			if (dateFormatted.equals(inputDate)) {
				for (Parameter parameter : timeSeries.getParameters()) {


					List<Float> temperature = parameter.getValues();



					Geometry geometry = weatherSMHI.getGeometry();

					ArrayList<ArrayList<Float>> geometryCoordinates = geometry.getCoordinates();


					for (float temp : temperature) {

						if (!parameter.getUnit().equals("category")) {

							for (ArrayList<Float> coordinates : geometryCoordinates) {

								Float longitude = coordinates.get(0);
								Float latitude = coordinates.get(1);

								if (parameter.getName().equals("t")) {

									System.out.println("----------------------------------------------------");
									System.out.println("Date: " + dateFormatted + ", hour: " + hour + ":00");
									System.out.println("Name: " + parameter.getName());
									System.out.println("Level type: " + parameter.getLevelType());
									System.out.println("Level: " + parameter.getLevel());
									System.out.println("Unit: " + parameter.getUnit());
									System.out.println("Values (Temperature): " + temperature); //parameter.getValues() and temperature same
									System.out.println("----------------------------------------------------");



									Forecast forecast = new Forecast();
									forecast.setDate(dateFormatted.atStartOfDay());
									forecast.setTemperature(temp);
									forecast.setHour(hour);
									forecast.setLongitude(longitude);
									forecast.setLatitude(latitude);
									forecast.setCreated(LocalDateTime.now());


									if (temp < 10){
										forecast.setRainOrSnow(true);
									}else {
										forecast.setRainOrSnow(false);
									}


									forecast.setDataSource(DataSource.Smhi);
									forecastRepository.save(forecast);


								} else if (parameter.getName().equals("pcat")) {

									System.out.println("----------------------------------------------------");
									System.out.println("Date: " + dateFormatted);
									System.out.println("Name: " + parameter.getName());
									System.out.println("Level type: " + parameter.getLevelType());
									System.out.println("Level: " + parameter.getLevel());
									System.out.println("Unit: " + parameter.getUnit());
									System.out.println("Values (Temperature): " + temperature); //parameter.getValues() and temperature same
									System.out.println("----------------------------------------------------");

								}
							}

						}
					}
				}
			}
		}
	}






	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}





	private void OpenMeteoAPIData() { // OPEN Meteo API is Provider 3

		ObjectMapper objectMapper = new ObjectMapper();

		String openMeteoApiUrl = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&past_days=1&hourly=temperature_2m,relativehumidity_2m,windspeed_10m";
		WeatherOpenMeteo weatherOpenMeteo = null;
		try {
			weatherOpenMeteo = objectMapper.readValue(new URL(openMeteoApiUrl), WeatherOpenMeteo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		var scanner = new Scanner(System.in);
		System.out.println("Enter date (yyyy-MM-dd): ");
		String userInput = scanner.next();


		System.out.println("----------------------------------------------------");

		System.out.println("Latitude: " + weatherOpenMeteo.getLatitude());
		System.out.println("Longitude: " + weatherOpenMeteo.getLongitude());
		System.out.println("Generationtime_ms: " + weatherOpenMeteo.getGenerationtime_ms());
		System.out.println("Utc_offset_seconds: " + weatherOpenMeteo.getUtc_offset_seconds());
		System.out.println("Timezone: " + weatherOpenMeteo.getTimezone());
		System.out.println("Timezone_abbreviation: " + weatherOpenMeteo.getTimezone_abbreviation());
		System.out.println("Elevation: " + weatherOpenMeteo.getElevation());


		System.out.println("----------------------------------------------------");


		HourlyUnitsOpenMeteo hourlyUnitsOpenMeteo = weatherOpenMeteo.getHourly_units();

		System.out.println("Time: " + hourlyUnitsOpenMeteo.getTime());
		System.out.println("Temperature: " + hourlyUnitsOpenMeteo.getTemperatureS_2m());
		System.out.println("Relativehumidity_2m: " + hourlyUnitsOpenMeteo.getRelativehumidity_2m());
		System.out.println("Windspeed_10m: " + hourlyUnitsOpenMeteo.getWindspeed_10m());


		HourlyOpenMeteo hourlyOpenMeteo = weatherOpenMeteo.getHourly();


		LocalDate inputDate = LocalDate.parse(userInput);


		List<Float> temperatureOpenMeteo = hourlyOpenMeteo.getTemperature_2m();
		List<Integer> relativehumidity_2m = hourlyOpenMeteo.getRelativehumidity_2m();
		List<Double> windSpeed_10m = hourlyOpenMeteo.getWindspeed_10m();
		List<String> time = hourlyOpenMeteo.getTime();

		int totalTime = time.size();

		for (int i = 0; i < totalTime; i++) {//make a for each instead

			String originalFormattedTime = time.get(i);

			/*LocalDate localDate = LocalDate.parse(originalFormattedTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

			String dataFormatted = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));*/

			LocalDateTime dateTime = LocalDateTime.parse(originalFormattedTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

			LocalDate dataFormatted = dateTime.toLocalDate();
			int hour = dateTime.getHour();

			if (dataFormatted.equals(inputDate)) {
				System.out.println("----------------------------------------------------");
				System.out.println("Date: " + dataFormatted + ", hour: " + hour + ":00");
				System.out.println("Temperature: " + temperatureOpenMeteo.get(i));
				System.out.println("Relativehumidity_2m: " + relativehumidity_2m.get(i));
				System.out.println("Windspeed_10m: " + windSpeed_10m.get(i));
				System.out.println("----------------------------------------------------");

				Forecast forecast = new Forecast();

				forecast.setDate(dataFormatted.atStartOfDay());
				forecast.setHour(hour);
				forecast.setTemperature(temperatureOpenMeteo.get(i));
				forecast.setLatitude((float) weatherOpenMeteo.getLatitude());//
				forecast.setLongitude((float) weatherOpenMeteo.getLongitude());//
				forecast.setCreated(LocalDateTime.now());


				if (temperatureOpenMeteo.get(i) < 10){
					forecast.setRainOrSnow(true);
				}else {
					forecast.setRainOrSnow(false);
				}


				forecast.setDataSource(DataSource.OpenMeteo);

				forecastRepository.save(forecast);

			}
		}

	}

}












