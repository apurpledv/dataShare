package com.openclassrooms.dataShare_api.config.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage {
    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public void copy(InputStream in, Path destination) throws IOException {
        Files.copy(in, destination);
    }

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    @Override
    public Resource load(Path path) throws MalformedURLException {
        return new UrlResource(path.toUri());
    }
}
