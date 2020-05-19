package de.bausdorf.simcacing.tt.live.model.live;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.bausdorf.simcacing.tt.live.impl.SessionController;
import de.bausdorf.simcacing.tt.live.model.client.Pitstop;
import de.bausdorf.simcacing.tt.live.model.client.RunData;
import de.bausdorf.simcacing.tt.live.model.client.Stint;
import de.bausdorf.simcacing.tt.stock.model.IRacingDriver;
import de.bausdorf.simcacing.tt.util.TimeTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class PitstopDataView {
	private String stintNo;
	private String raceTimeLeft;
	private String lapNo;
	private String driver;
	private List<String> allDrivers;
	private String timePitted;
	private String pitStopDuration;
	private String service;
	private String serviceDuration;
	private String refuelAmount;
	private String repairTime;

	public static List<PitstopDataView> getPitstopDataView(SessionController controller) {
		List<PitstopDataView> pitstopDataViews = new ArrayList<>();
		if (controller != null) {
			RaceClock clock = new PitstopDataView.RaceClock();
			ZonedDateTime now = ZonedDateTime.now();
			viewForPittedStints(controller, clock, pitstopDataViews);
			if (controller.getRacePlan() != null) {
				viewForPlannedStints(controller, now, clock, pitstopDataViews);
			}
		}
		return pitstopDataViews;
	}

	private static void viewForPlannedStints(SessionController controller, ZonedDateTime now, RaceClock clock, List<PitstopDataView> pitstopDataViews) {
		RunData runData = controller.getRunData();
		LocalDateTime sessionToD = controller.getRacePlan().getPlanParameters().getTodStartTime();
		if (runData != null) {
			sessionToD = LocalDateTime.of(sessionToD.toLocalDate(), runData.getSessionToD());
		}
		List<de.bausdorf.simcacing.tt.planning.model.Stint> stints =
				controller.getRacePlan().calculateStints(now, sessionToD, now.plus(controller.getRemainingSessionTime()));
		controller.getRacePlan().setCurrentRacePlan(stints);
		for (de.bausdorf.simcacing.tt.planning.model.Stint stint : stints) {
			clock.lapCount += stint.getLaps();
			clock.stintNo++;
			clock.accumulatedStintDuration = clock.accumulatedStintDuration.plus(stint.getStintDuration(true));
			Duration raceTimeLeft = controller.getRacePlan().getPlanParameters().getRaceDuration()
					.minus(clock.accumulatedStintDuration);
			List<String> driverList = controller.getRacePlan().getPlanParameters().getAllDrivers().stream()
					.map(IRacingDriver::getName)
					.collect(Collectors.toList());
			driverList.add("unassigned");

			PitstopDataView view = PitstopDataView.builder()
					.driver(stint.getDriverName())
					.lapNo(stint.isLastStint() ? "(" + Integer.toUnsignedString(stint.getLaps()) + ")"
							: Integer.toUnsignedString(clock.lapCount))
					.timePitted(stint.getEndTime().format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS_XXX)))
					.stintNo(Integer.toUnsignedString(clock.stintNo))
					.allDrivers(driverList)
					.raceTimeLeft(TimeTools.shortDurationString(raceTimeLeft))
					.pitStopDuration("")
					.repairTime("")
					.service("")
					.serviceDuration("")
					.refuelAmount(String.format(Locale.US, "%.3f", stint.getRefuelAmount()))
					.build();

			pitstopDataViews.add(view);
		}
	}

	private static void viewForPittedStints(SessionController controller, RaceClock clock, List<PitstopDataView> pitstopDataViews) {
		for (Stint stint : controller.getStints().tailMap(0).values()) {
			Pitstop pitstop = controller.getPitStops().get(stint.getNo());
			if (pitstop == null || !pitstop.isComplete()) {
				// Stint not complete
				continue;
			}
			clock.lapCount += stint.getLaps();
			clock.stintNo++;
			Duration stintDuration = stint.getCurrentStintDuration() != null ? stint.getCurrentStintDuration() : Duration.ZERO;
			clock.accumulatedStintDuration = clock.accumulatedStintDuration.plus(stintDuration);
			Duration raceDuration = controller.getRacePlan() != null
					? controller.getRacePlan().getPlanParameters().getRaceDuration()
					: Duration.ofSeconds(controller.getSessionData().getSessionDuration().orElse(LocalTime.MIN).toSecondOfDay());
			Duration raceTimeLeft = raceDuration.minus(clock.accumulatedStintDuration);

			PitstopDataView view = PitstopDataView.builder()
					.allDrivers(Collections.singletonList(stint.getDriver()))
					.driver(stint.getDriver())
					.lapNo(Integer.toUnsignedString(clock.lapCount))
					.raceTimeLeft(raceTimeLeft.isNegative() ? "" : TimeTools.shortDurationString(raceTimeLeft))
					.stintNo(Integer.toUnsignedString(stint.getNo()))
					.timePitted(ZonedDateTime.of(LocalDate.now(), pitstop.getEnterPits(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(TimeTools.HH_MM_SS_XXX)))
					.serviceDuration(TimeTools.shortDurationString(pitstop.getPitstopServiceTime()))
					.service(pitstop.getServiceFlagsString())
					.repairTime(TimeTools.shortDurationString(pitstop.getRepairAndTowingTime()))
					.pitStopDuration(TimeTools.shortDurationString(pitstop.getPitstopDuration()))
					.refuelAmount(fuelString(pitstop.getRefuelAmount()))
					.build();

			pitstopDataViews.add(view);
		}
	}

	private static String fuelString(double fuelAmount) {
		return String.format("%.3f", fuelAmount).replace(",", ".");
	}

	@Data
	private static class RaceClock {
		private int stintNo = 0;
		private int lapCount = 0;
		private Duration accumulatedStintDuration = Duration.ZERO;
	}
}
