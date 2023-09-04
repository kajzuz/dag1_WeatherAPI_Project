package se.systementor.dag1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import se.systementor.dag1.models.*;
import se.systementor.dag1.repositorys.ForecastRepository;
import se.systementor.dag1.services.ForecastService;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootApplication
public class Dag1Application implements CommandLineRunner { // G: get average on jsonlist and SMHI combined per day

	// Abstraction, we use it without knowing what it does

	@Autowired // "newar" for us when compiled
	private ForecastService forecastService;
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
				System.out.println("7. Average temperature (JSON List)");
				System.out.println("8. Average temperature (API SMHI)");
				System.out.println("9. Average temperature (Open Meteo API) ");
				System.out.println("10. Average of (json + smhi) temperatures ");
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

					case 7:
						average();
						break;

					case 8:
						averageSmhiAPI();
						break;

					case 9:
						averageOpenMeteoAPI();
						break;

					case 10:
						averageSmhiAndJsonTemperatures();
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


	private void addPrediction(Scanner scanner) throws ParseException {


		System.out.println("<-- Create Prediction -->");
		System.out.println("State day, in following format (yyyy-MM-dd): ");
		String day = scanner.next();


		DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateFormatted = LocalDate.parse(day, date);

		//SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		//LocalDateTime dateFormatted = LocalDateTime.parse(day,date);
		//LocalDateTime dateFormatted = LocalDateTime.parse(String.valueOf(date2));

		//Instant instant = dateFormatted.toInstant();
		//LocalDateTime dateActually = instant.atZone(ZoneId.from(Instant.now())).toLocalDateTime();

		System.out.println("Hour: ");
		int hour = scanner.nextInt();
		System.out.println("Temperature: ");
		float temp = scanner.nextFloat();

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setDate(dateFormatted.atStartOfDay());
		forecast.setHour(hour);
		forecast.setTemperature(temp);

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


		forecast.setTemperature(newTemp);

		System.out.println("New temp added!");

		forecastService.update(forecast); // Database use

	}


	private void deletePrediction(Scanner scanner) {

		System.out.println("Select one of the following row numbers you want to DELETE: \n ");
		var deleteRow = scanner.nextInt();
		var forecast = forecastService.getForecastList().get(deleteRow - 1);
		forecastService.delete(forecast);
	}


	private void SMHIApiData() { //save to sql light somehow



		ObjectMapper objectMapper = new ObjectMapper();


		String SMHIUrl = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
		WeatherSMHI weatherSMHI = null;
		try {
			weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		System.out.println("----------------------------------------------------");

		System.out.println("ApprovedTime: " + weatherSMHI.getApprovedTime());
		System.out.println("ReferenceTime: " + weatherSMHI.getReferenceTime());
		System.out.println("Geometry: " + weatherSMHI.getGeometry());


		System.out.println("----------------------------------------------------");



		for (TimeSeries timeSeries : weatherSMHI.getTimeSeries()) { // Limit results to a day every hour
			//String validTime = String.valueOf(timeSeries.getValidTime());

			LocalDate dateFormatted = convertToLocalDateViaInstant(timeSeries.getValidTime());

			for (Parameter parameter : timeSeries.getParameters()) {

				List<Double> temperature = parameter.getValues();


				if (parameter.getName().equals("t") || parameter.getName().equals("pcat")) {


					System.out.println("----------------------------------------------------");
					System.out.println("Date " + dateFormatted);
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

	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}

	private void OpenMeteoAPIData() {

		ObjectMapper objectMapper = new ObjectMapper();

		String openMeteoApiUrl = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&past_days=1&hourly=temperature_2m,relativehumidity_2m,windspeed_10m";
		WeatherOpenMeteo weatherOpenMeteo = null;
		try {
			weatherOpenMeteo = objectMapper.readValue(new URL(openMeteoApiUrl), WeatherOpenMeteo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

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


		List<Double> temperature = hourlyOpenMeteo.getTemperature_2m();
		List<Integer> relativehumidity_2m = hourlyOpenMeteo.getRelativehumidity_2m();
		List<Double> windSpeed_10m = hourlyOpenMeteo.getWindspeed_10m();
		List<String> time = hourlyOpenMeteo.getTime();


		for (int i = 0; i < time.size(); i++) { 

			String originalFormattedTime = time.get(i);

			LocalDate localDate = LocalDate.parse(originalFormattedTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

			String dataFormatted = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


			System.out.println("----------------------------------------------------");
			System.out.println("Date: " + dataFormatted);
			System.out.println("Temperature: " + temperature.get(i));
			System.out.println("Relativehumidity_2m: " + relativehumidity_2m.get(i));
			System.out.println("Windspeed_10m: " + windSpeed_10m.get(i));
			System.out.println("----------------------------------------------------");


		}


	}


	public void average() {


		double totalSum = 0;

		List<Forecast> amount = forecastService.getForecastList();

		for (var forecast : amount) {

			float temp = forecast.getTemperature();

			totalSum += temp;

		}

		int number = amount.size();
		double averageTemp = totalSum / number;

		System.out.println("Total sum temperature JSON list : " + totalSum);
		System.out.println("Amount of temperatures JSON list: " + number);

		System.out.println("Average temp JSON list: " + averageTemp);

	}


	private void averageSmhiAPI() {

		ObjectMapper objectMapper = new ObjectMapper();

		String SMHIUrl = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
		WeatherSMHI weatherSMHI = null;
		try {
			weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		double totalSum = 0;
		int amountOfNumbers = 0;


		for (TimeSeries timeSeries : weatherSMHI.getTimeSeries()) {

			List<Parameter> amount = timeSeries.getParameters();

			for (Parameter parameter : amount) {

				List<Double> temperature = parameter.getValues();



				for (Double temp : temperature) {
					totalSum += temp;
					amountOfNumbers++;
				}

			}
		}
		double averageTemp = totalSum / amountOfNumbers;

		System.out.println("Total sum temperature SMHI Api: " + totalSum);
		System.out.println("Amount of temperatures SMHI Api: " + amountOfNumbers);

		System.out.println("\nAverage SMHI API temp: " + averageTemp);

	}


	private void averageOpenMeteoAPI() {


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

		int amountOfNumbers = amount.size();
		double averageTemp = totalSum / amountOfNumbers;

		System.out.println("Total sum temperature Open Meteo Api: " + totalSum);
		System.out.println("Amount of temperatures in Open Meteo Api: " + amountOfNumbers);

		System.out.println("\nAverage Open Meteo API temp: " + averageTemp);

	}


	public void averageSmhiAndJsonTemperatures() {

		/*var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setTemperature(12);
		forecast.setPredictionDate(LocalDateTime.now());
		forecast.setHour(10);*/




		Scanner scanner = new Scanner(System.in);

		ObjectMapper objectMapper = new ObjectMapper();

		String SMHIUrl = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
		WeatherSMHI weatherSMHI = null;
		try {
			weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//only smhi but add json list as well
		System.out.println("Enter date (yyyy-MM-dd): ");
		String inputDate = scanner.next();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		for (TimeSeries timeSeries : weatherSMHI.getTimeSeries()) {
			Date validTime = timeSeries.getValidTime(); //Date is now the type in Timeseries


			String dateFormatted = dateFormat.format(validTime);

			if (dateFormatted.equals(inputDate)) {
				for (Parameter parameter : timeSeries.getParameters()) {
					List<Double> temperature = parameter.getValues();

					if (parameter.getName().equals("t") || parameter.getName().equals("pcat")) {
						double totalTemp = 0;
						int count = 0;

						for (Double temp : temperature) {
							totalTemp += temp;
							count++;
						}

						if (count > 0) {
							double averageTemp = totalTemp / count;
							SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
							String hour = hourFormat.format(validTime);

							if (averageTemp == 0.0){
								continue;
							}

							System.out.println(inputDate + " hour: " + hour + ":00" + ", average temperature: " + averageTemp);
						}
					}
				}




		/*System.out.println("Sum of all temps: " + totalSumOfAllTemperatures);
		System.out.println("Amount of temps: " + amountOfTemperatures);

		System.out.println("Average (JSON list + SMHI) API temp: " + averageTemp);*/


//		ObjectMapper objectMapper = new ObjectMapper();
//
//		String SMHIUrl="https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json";
//		WeatherSMHI weatherSMHI = null;
//		try {
//			weatherSMHI = objectMapper.readValue(new URL(SMHIUrl), WeatherSMHI.class);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//
//		//Json
//		double totalTempJSONList = 0;
//
//		List<Forecast> amountJSONTemp = forecastService.getForecastList();
//
//		for (var forecast : amountJSONTemp){
//
//			float temp = forecast.getTemperature();
//
//			totalTempJSONList += temp;
//
//		}
//
//		int number = amountJSONTemp.size();
//		double averageTempJson = totalTempJSONList / number;
//
//		//System.out.println("Average temp: " + averageTempJson);//
//
//
//
//		//SMHI api
//		double totalTempSmhiApi = 0;
//		int amountOfNumbers = 0;
//
//
//		for (TimeSeries timeSeries: weatherSMHI.getTimeSeries()) {
//
//			List<Parameter> amountTempSmhi = timeSeries.getParameters();
//
//
//			for (Parameter parameter : amountTempSmhi) {
//
//				List<Double> temperature = parameter.getValues();
//
//
//				for (Double temp: temperature) {
//					totalTempSmhiApi += temp;
//					amountOfNumbers ++;
//				}
//
//			}
//		}//
//
//		// Count the total average temperature
//		int amountOfTemperatures = number + amountOfNumbers;
//		double totalSumOfAllTemperatures = totalTempJSONList + totalTempSmhiApi;
//
//		double averageTemp = totalSumOfAllTemperatures / amountOfTemperatures;
//
//		System.out.println("Sum of all temps: " + totalSumOfAllTemperatures);
//		System.out.println("Amount of temps: " + amountOfTemperatures);
//
//		System.out.println("Average (JSON list + SMHI) API temp: " + averageTemp);


			}

		}
	}
}









