/*
 * Copyright (C) 2020 by the geOrchestra PSC
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
package org.georchestra.datafeeder.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * {@link ConfigurationProperties @ConfigurationProperties} for the DataFeeder
 * application
 */
public @Data class DataFeederConfigurationProperties {

    private FileUploadConfig fileUpload = new FileUploadConfig();

    public static @Data class FileUploadConfig {
        /** maximum size allowed for uploaded files. */
        private String maxFileSize;

        /** maximum size allowed for multipart/form-data requests */
        private String maxRequestSize;

        /** size threshold after which files will be written to disk. */
        private String fileSizeThreshold;

        /**
         * directory location where files will be stored by the servlet container once
         * the request exceeds the {@link #fileSizeThreshold}
         */
        private String temporaryLocation = "";

        /** directory location where files will be stored. */
        private Path persistentLocation = Paths.get("/tmp/datafeeder/uploads");
    }
}
