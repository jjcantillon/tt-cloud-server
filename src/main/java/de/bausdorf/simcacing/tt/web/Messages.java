package de.bausdorf.simcacing.tt.web;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Messages extends ArrayList<Message> {

    public List<Message> errors() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.ERROR))
                .collect(Collectors.toList());
    }

    public List<Message> warnings() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.WARN))
                .collect(Collectors.toList());
    }

    public List<Message> infos() {
        return this.stream()
                .filter(s -> s.getType().equalsIgnoreCase(Message.INFO))
                .collect(Collectors.toList());
    }
}
