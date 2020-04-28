package de.bausdorf.simcacing.tt.live.model.live;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunDataView {
    private String driverName;
    private String sessionTime;
    private double fuelLevel;
    private String fuelLevelStr;
    private List<String> flags;
}
