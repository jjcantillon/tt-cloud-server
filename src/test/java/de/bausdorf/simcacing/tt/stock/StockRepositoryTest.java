package de.bausdorf.simcacing.tt.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bausdorf.simcacing.tt.util.UnitConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.bausdorf.simcacing.tt.stock.model.IRacingCar;
import de.bausdorf.simcacing.tt.stock.model.IRacingTrack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class StockRepositoryTest {

	public static final String CAR_JSON_BASE_PATH = "D:\\Users\\robert\\Projects\\SIMRacingAppsSIMPluginiRacing\\src\\com\\SIMRacingApps\\SIMPlugins\\iRacing\\Cars";

	@Autowired
	CarRepository carRepository;

	@Autowired
	TrackRepository trackRepository;

	@Test
	@Disabled
	public void loadCarsFromCSV() {
		List<IRacingCar> cars = loadObjectList(IRacingCar.class, "carIds.csv");
		for( IRacingCar o : cars) {
			if( !o.getUnit().equals("l") ) {
				o.setMaxFuel(UnitConverter.toLiters(o.getMaxFuel(), o.getUnit()));
				o.setUnit("l");
			}
			carRepository.save(o);
		}
	}


	@Test
	@Disabled
	public void loadTracksFromCSV() {
		List<IRacingTrack> cars = loadObjectList(IRacingTrack.class, "allTracksDetails.csv");
		for( IRacingTrack o : cars) {
//			if( o.getName().indexOf('/') > 0 ) {
//				o.setName(o.getName().replaceAll("/", " "));
//			}
			log.info("Try to save: {}", o);
			trackRepository.save(o);
		}
	}

	@Test
	@Disabled
	public void loadAllTracksFromRepository() {
		long loadStart = System.currentTimeMillis();
		List<IRacingTrack> allTracks = trackRepository.loadAll(true);
		long loadEnd = System.currentTimeMillis();
		assertThat(allTracks.isEmpty()).isFalse();
		log.info("Loading of {} tracks took {} ms", allTracks.size(), loadEnd - loadStart);

		allTracks.clear();

		loadStart = System.currentTimeMillis();
		allTracks = trackRepository.loadAll(true);
		loadEnd = System.currentTimeMillis();
		assertThat(allTracks.isEmpty()).isFalse();
		log.info("Loading of {} tracks took {} ms", allTracks.size(), loadEnd - loadStart);
	}

	@Test
	@Disabled
	public void loadAllCarsFromRepository() {
		long loadStart = System.currentTimeMillis();
		List<IRacingCar> allTracks = carRepository.loadAll(true);
		long loadEnd = System.currentTimeMillis();
		assertThat(allTracks.isEmpty()).isFalse();
		log.info("Loading of {} cars took {} ms", allTracks.size(), loadEnd - loadStart);

		allTracks.clear();

		loadStart = System.currentTimeMillis();
		allTracks = carRepository.loadAll(true);
		loadEnd = System.currentTimeMillis();
		assertThat(allTracks.isEmpty()).isFalse();
		log.info("Loading of {} cars took {} ms", allTracks.size(), loadEnd - loadStart);
	}

	public <T> List<T> loadObjectList(Class<T> type, String fileName) {
		try {
			CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			File file = new ClassPathResource(fileName).getFile();
			MappingIterator<T> readValues =
					mapper.reader(type).with(bootstrapSchema).readValues(file);
			return readValues.readAll();
		} catch (Exception e) {
			log.error("Error occurred while loading object list from file " + fileName, e);
			return Collections.emptyList();
		}
	}
}