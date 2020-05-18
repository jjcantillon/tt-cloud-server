package de.bausdorf.simcacing.tt.web.model;

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

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.planning.model.ScheduleDriverOptionType;

public class ScheduleDriverOptionTypeFormatter implements Formatter<ScheduleDriverOptionType> {

	@Override
	public ScheduleDriverOptionType parse(String s, Locale locale) throws ParseException {
		return ScheduleDriverOptionType.fromCssClass(s);
	}

	@Override
	public String print(ScheduleDriverOptionType scheduleDriverOptionType, Locale locale) {
		return scheduleDriverOptionType.cssClassName();
	}
}
