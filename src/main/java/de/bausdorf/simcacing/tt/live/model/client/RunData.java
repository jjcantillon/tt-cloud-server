package de.bausdorf.simcacing.tt.live.model.client;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunData implements ClientData {

	private LocalTime sessionTime;
	private LocalTime sessionToD;
	private double fuelLevel;
	private List<FlagType> flags;
	private Duration estLapTime;
	private int lapNo;
	private Duration timeInLap;

	public boolean isGreenFlag() {
		return flags.contains(FlagType.GREEN);
	}
}
