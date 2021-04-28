/*
 * Copyright (C) 2021 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.georchestra.datafeeder.event;

import org.georchestra.datafeeder.model.DataUploadJob;
import org.georchestra.datafeeder.model.UserInfo;

import lombok.Getter;

public class AnalysisFailedEvent extends DatafeederEvent<DataUploadJob> {
    private static final long serialVersionUID = 1L;

    private @Getter Exception cause;

    public AnalysisFailedEvent(DataUploadJob source, UserInfo user, Exception error) {
        super(source, user);
        this.cause = error;
    }
}
