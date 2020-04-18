package de.bausdorf.simcacing.tt.planning;

import de.bausdorf.simcacing.tt.planning.model.PitStop;
import de.bausdorf.simcacing.tt.planning.model.PitStopServiceType;
import de.bausdorf.simcacing.tt.planning.model.RacePlanParameters;
import de.bausdorf.simcacing.tt.planning.model.Stint;
import de.bausdorf.simcacing.tt.util.FirestoreDB;
import de.bausdorf.simcacing.tt.util.TimeCachedRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RacePlanRepository extends TimeCachedRepository<RacePlanParameters> {
    public static final String COLLECTION_NAME = "RacePlanParameters";

    public RacePlanRepository(@Autowired FirestoreDB db) {
        super(db, 720 * 60000);
    }

    @Override
    protected RacePlanParameters fromMap(Map<String, Object> data) {
        if( data == null) {
            return null;
        }
        return RacePlanParameters.builder()
                .id(stringFromMap(RacePlanParameters.ID, data))
                .name(stringFromMap(RacePlanParameters.NAME, data))
                .driverCount(((Long)data.get(RacePlanParameters.DRIVER_COUNT)).intValue())
                .raceDuration(durationFromMap(RacePlanParameters.RACE_DURATION, data))
                .sessionStartTime(dateTimeFromMap(RacePlanParameters.SESSION_START_TIME, data))
                .teamId(stringFromMap(RacePlanParameters.TEAM_ID, data))
                .trackId(stringFromMap(RacePlanParameters.TRACK_ID, data))
                .carId(stringFromMap(RacePlanParameters.CAR_ID, data))
                .avgFuelPerLap(doubleFromMap(RacePlanParameters.AVG_FUEL_PER_LAP, data))
                .avgLapTime(durationFromMap(RacePlanParameters.AGV_LAP_TIME, data))
                .avgPitStopTime(durationFromMap(RacePlanParameters.AVG_PIT_STOP_TIME, data))
                .maxCarFuel(doubleFromMap(RacePlanParameters.MAX_CAR_FUEL, data))
                .greenFlagOffsetTime(timeFromMap(RacePlanParameters.GREEN_FLAG_OFFSET_TIME, data))
                .todStartTime(dateTimeFromMap(RacePlanParameters.TOD_START_TIME, data))
                .stints(stintsFromMap(RacePlanParameters.STINTS, data))
                .build();
    }

    @Override
    protected Map<String, Object> toMap(RacePlanParameters object) {
        return object.toMap();
    }

    public void save(RacePlanParameters planParameters) {
        super.save(COLLECTION_NAME, planParameters.getId(), planParameters);
    }

    public List<RacePlanParameters> findByTeamIds(List<String> teamIds) {
        List<RacePlanParameters> plans = new ArrayList<>();
        for( String teamId : teamIds ) {
            plans.addAll(super.findByFieldValue(COLLECTION_NAME, RacePlanParameters.TEAM_ID, teamId));
        }
        return plans;
    }

    public Optional<RacePlanParameters> findById(String id) {
        return super.findByName(COLLECTION_NAME, id);
    }

    private String stringFromMap(String key, Map<String, Object> data) {
        try {
            return (String)data.get(key);
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return "";
    }

    private double doubleFromMap(String key, Map<String, Object> data) {
        try {
            return (Double)data.get(key);
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return 0.0D;
    }

    private Duration durationFromMap(String key, Map<String, Object> data) {
        try {
            return Duration.parse((String)data.get(key));
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return Duration.ZERO;
    }

    private LocalTime timeFromMap(String key, Map<String, Object> data) {
        try {
            return LocalTime.parse((String)data.get(key));
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return LocalTime.MIN;
    }

    private LocalDateTime dateTimeFromMap(String key, Map<String, Object> data) {
        try {
            return LocalDateTime.parse((String)data.get(key));
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return LocalDateTime.MIN;
    }

    private List<String> stringListFromMap(String key, Map<String, Object> data) {
        try {
            return (List<String>)data.get(key);
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<Stint> stintsFromMap(String key, Map<String, Object> data) {
        SortedMap<Integer, Stint> stints = new TreeMap<>();
        try {
            Map<String, Object> stintsMap = (Map<String, Object>) data.get(key);
            for( String stintKey : stintsMap.keySet() ) {
                Map<String, Object> stintMap = (Map<String, Object>)stintsMap.get(stintKey);
                Stint stint = Stint.builder()
                        .driverName(stringFromMap(Stint.DRIVER_NAME, stintMap))
                        .startTime(dateTimeFromMap(Stint.START_TIME, stintMap))
                        .todStartTime(dateTimeFromMap(Stint.TOD_START_TIME, stintMap))
                        .endTime(dateTimeFromMap(Stint.END_TIME, stintMap))
                        .laps(((Long)stintMap.get(Stint.LAPS)).intValue())
                        .refuelAmount(doubleFromMap(Stint.REFUEL_AMOUNT, stintMap))
                        .pitStop(Optional.empty())
                        .build();

                List<String> pitService = stringListFromMap(Stint.PITSTOP_SERVICE, stintMap);
                if( pitService != null && !pitService.isEmpty() ) {
                    PitStop pitstop = PitStop.defaultPitStop();
                    pitstop.getService().clear();
                    pitService.stream().forEach(s -> pitstop.addService(PitStopServiceType.valueOf(s)));
                    stint.setPitStop(Optional.of(pitstop));
                }

                stints.put(Integer.parseInt(stintKey), stint);
            }
        } catch( Exception e ) {
            log.warn(e.getMessage());
        }
        return stints.tailMap(0).values().stream().collect(Collectors.toList());
    }
}