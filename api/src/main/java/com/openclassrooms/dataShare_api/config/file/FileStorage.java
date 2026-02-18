package com.openclassrooms.dataShare_api.config.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;

public interface FileStorage {
    void createDirectories(Path path) throws IOException;
    void copy(InputStream in, Path destination) throws IOException;
    boolean deleteIfExists(Path path) throws IOException;
    Resource load(Path path) throws MalformedURLException;
}
