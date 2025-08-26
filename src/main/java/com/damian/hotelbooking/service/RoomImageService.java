package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.HotelImage;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.RoomImage;
import com.damian.hotelbooking.repository.HotelImageRepository;
import com.damian.hotelbooking.repository.RoomImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final String uploadDir = "src/main/resources/static/images/rooms/";

    public RoomImageService(RoomImageRepository roomImageRepository) {
        this.roomImageRepository = roomImageRepository;
    }

    public void saveImages(List<MultipartFile> files, Room room) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            try {
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            RoomImage img = new RoomImage();
            img.setFileName(fileName);
            img.setUrl("/images/rooms/" + fileName);
            img.setRoom(room);

            roomImageRepository.save(img);
        }
    }
}
